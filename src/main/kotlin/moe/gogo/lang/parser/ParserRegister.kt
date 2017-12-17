package moe.gogo.lang.parser

import moe.gogo.lang.ast.ASTree
import moe.gogo.lang.ast.Identifier
import moe.gogo.lang.ast.NumberLiteral
import moe.gogo.lang.ast.StringLiteral
import moe.gogo.lang.lexer.Lexer
import moe.gogo.lang.lexer.Token
import kotlin.reflect.KClass

class ParserRegister(private val productions: ProductionRegister) {

    private val table = productions.make()

    private val parsers = mutableMapOf<Symbol, Parser>()
    private val builder = mutableMapOf<Symbol, KClass<out ASTree>>()
    private val defaults = mutableMapOf<Production, Parser>()

    private val terminalParser = TerminalParser()

    init {
        productions.productions.forEach {
            defaults.put(it, BaseParser(it))
        }
    }

    /**
     * 注册某个符号的Parser
     */
    fun register(name: String, parser: Parser) {
        val symbol = productions.getSymbol(name) ?:
                throw ParseException("找不到 Symbol $name")
        parsers[symbol] = parser
    }

    /**
     * 定义某个非终止符对应的树
     */
    fun defineType(name: String, treeType: KClass<out ASTree>) {
        val symbol = productions.getSymbol(name) ?:
                throw ParseException("找不到 Symbol $name")
        builder[symbol] = treeType
    }

    /**
     * 获取非终止符对应的产生式
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
        return if (symbol.isNonTerminal()) {
            findNonTerminalParser(symbol, lexer)
        } else {
            terminalParser
        }
    }

    private fun findNonTerminalParser(symbol: Symbol, lexer: Lexer): Parser {
        val nonTerminal = productions.gerNonTerminal(symbol.name) ?:
                throw ParseException("找不到 NonTerminal ${symbol.name}")
        val next = getSymbol(lexer.peek()) ?:
                throw ParseException("找不到 Symbol ${lexer.peek()}")
        val production = table.getTable(nonTerminal, next)
        return defaults[production] ?: throw ParseException("找不到对应的Parser $production")
    }

    /**
     * 把 token 转换为相应的 Symbol
     */
    private fun getSymbol(token: Token): Symbol? = when {
        token.isNumber -> findType(NumberLiteral::class)
        token.isString -> findType(StringLiteral::class)
        token.isIdentifier -> productions.getSymbol(token.name)
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
            if(lexer.peek() == Token.EOF){
                throw ParseException("未正确结束 ",lexer.peek())
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


}