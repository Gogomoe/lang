package moe.gogo.lang.parser

import moe.gogo.lang.ast.ASTree
import moe.gogo.lang.ast.Identifier
import moe.gogo.lang.ast.NumberLiteral
import moe.gogo.lang.ast.StringLiteral
import moe.gogo.lang.lexer.Lexer
import moe.gogo.lang.lexer.Token
import kotlin.reflect.KClass

/**
 * 本类是Parser解析的核心类
 *
 * 使用 Parser 前，需先在[ProductionRegister]按照 LL 文法注册相应的展开式。
 * 本类通过注册的展开式生成对应的[Parser表][ParserTable]。
 * 本类默认的Parser可以通过 Parser表 找到对应的解析方式。
 *
 * 然而这种方法存在两个问题。
 *
 * 一是某些语法对应相应的 ASTree，比如 If 语句有对应的 ASTree，
 * 这时应通过[defineType]方法来定义非终止符对应生成的 ASTree 类型。
 *
 * 二是解析表达式时为了考虑优先级，LL文法会显得很复杂。
 * 这时采取了另一种解析表达式的方法，[ExpressionParser]类可以定义二元运算符的优先级并解析表达式。
 *
 * 因此，本类提供了[register]方法注册某一自定义的Parser。
 *
 * 具体使用可以参考测试用例 ExpressionParserTest 类。
 *
 * @see ProductionRegister
 * @see ExpressionParser
 **/
class ParserRegister(private val productions: ProductionRegister) {

    private val table = productions.make()

    private val parsers = mutableMapOf<Symbol, Parser>()
    private val builder = mutableMapOf<Symbol, KClass<out ASTree>>()
    private val defaults = mutableMapOf<Production, Parser>()

    private val terminalParser = TerminalParser()
    private val emptyParser = EmptyParser()

    init {
        productions.productions.forEach {
            defaults.put(it, BaseParser(it))
        }
    }

    /**
     * 注册某个非终止符对应的Parser
     * @param name 非终止符的name
     * @param parser 对应的parser
     */
    fun register(name: String, parser: Parser) {
        val symbol = productions.getSymbol(name) ?:
                throw ParseException("找不到 Symbol $name")
        parsers[symbol] = parser
    }

    /**
     * 定义某个非终止符对应的树
     * @param name 非终止符的name
     * @param treeType 对应生成的ASTree的类型
     */
    fun defineType(name: String, treeType: KClass<out ASTree>) {
        val symbol = productions.getSymbol(name) ?:
                throw ParseException("找不到 Symbol $name")
        builder[symbol] = treeType
    }

    /**
     * 获取非终止符对应的产生式。
     *
     * 需要注意的是，如果存在多个Parser，只会返回第一个。
     * 因此不能保证返回的 Parser 能正确解析。
     * 可以通过只设置一种产生式来避免这个问题。
     *
     * @param name 非终止符的name
     */
    fun findParser(name: String): Parser? {
        val symbol = productions.getSymbol(name)!!
        if (parsers.containsKey(symbol)) {
            return parsers[symbol]
        }
        /**
         * 没有注册时找到第一个产生式
         */
        for ((k, v) in defaults) {
            if (k.nonTerminal.name == name) {
                return v
            }
        }
        return null
    }

    /**
     * 根据要解析的 symbol 和下一个 token 在表中查询对应的 parser
     */
    internal fun findParser(symbol: Symbol, lexer: Lexer): Parser {
        /**
         * 被手动注册的 parser
         */
        val parser = parsers[symbol]
        if (parser != null) {
            return parser
        }
        /**
         * 自动生成的表
         */
        return when {
            symbol.isNonTerminal() -> findNonTerminalParser(symbol, lexer)
            symbol == Symbol.EMPTY -> emptyParser
            else -> terminalParser
        }
    }

    private fun findNonTerminalParser(symbol: Symbol, lexer: Lexer): Parser {
        val nonTerminal = productions.gerNonTerminal(symbol.name) ?:
                throw ParseException("找不到 NonTerminal ${symbol.name}")
        val next = getSymbol(lexer.peek()) ?:
                throw ParseException("找不到 Symbol ${lexer.peek()}")
        val production = table.getTable(nonTerminal, next) ?:
                throw ParseException("找不到对应的 Production $nonTerminal -> $next")
        return defaults[production] ?: throw ParseException("找不到对应的Parser $production")
    }

    /**
     * 把 token 转换为相应的 Symbol
     */
    private fun getSymbol(token: Token): Symbol? = when {
        token.isNumber -> findType(NumberLiteral::class)
        token.isString -> findType(StringLiteral::class)
    // 没有找到对应符号时作为 id 解析
        token.isIdentifier -> productions.getSymbol(token.name) ?: productions.getSymbol("id")
        token == Token.EOF -> Symbol.END
        else -> throw ParseException("token 类型不匹配 $token")
    }

    private fun findType(kClass: KClass<out ASTree>): Symbol? {
        for ((k, v) in builder) {
            if (v == kClass) {
                return k
            }
        }
        return null
    }

    /**
     * 默认的 Parser，可以根据一个产生式解析
     */
    private inner class BaseParser(val production: Production) : Parser() {
        override fun parseList(lexer: Lexer): List<ASTree> {
            val results = mutableListOf<ASTree>()
            production.symbols.forEach {
                parserChild(it, lexer, results)
            }
            squeezeResults(results)
            return results
        }


        private fun parserChild(symbol: Symbol, lexer: Lexer, results: MutableList<ASTree>) {
            /**
             * 已经读取全部Token时退出解析
             */
            if (lexer.peek() == Token.EOF && production.nonTerminal.follow.contains(Symbol.END)) {
                return
            }
            if (lexer.peek() == Token.EOF) {
                throw ParseException("未正确结束 ", lexer.peek())
            }
            /**
             * 解析子树
             */
            val parser = findParser(symbol, lexer)
            val list = parser.parseList(lexer)
            results.addAll(list)
        }

        /**
         * 根据类型从列表重新生成树
         */
        fun squeezeResults(results: MutableList<ASTree>) {
            val type = builder[production.nonTerminal.symbol]
            if (type != null) {
                val c = type.java.getConstructor(List::class.java)!!
                val tree = c.newInstance(results)
                results.clear()
                results.add(tree)
            }
        }
    }

    private inner class TerminalParser : Parser() {
        override fun parseList(lexer: Lexer): List<ASTree> {
            val token = lexer.read()
            val type = builder[getSymbol(token)] ?: return listOf(Identifier(token))
            val c = type.java.getConstructor(Token::class.java)!!
            return listOf(c.newInstance(token))
        }
    }

    private inner class EmptyParser : Parser() {
        override fun parseList(lexer: Lexer): List<ASTree> = emptyList()
    }

}