package moe.gogo.lang.parser

/**
 * 注册产生式的类。
 *
 * 通过[register]方法可以注册LL文法
 */
class ProductionRegister {

    internal val productions = mutableSetOf<Production>()
    private val nonTerminals = mutableMapOf<String, NonTerminal>()
    private val terminalSymbols = mutableMapOf<String, Symbol>()

    /**
     * 注册展开式
     *
     * 这几行都是合法的范例
     * ```
     * Expr -> Factor + Factor
     * Factor -> number
     * Factor -> - number
     * ```
     * 非终止符必须已大写字母开始，终止符不能以大写字母开始。每两个符号之间至少有一个空格。
     *
     * 若使用[ExpressionParser]之类的对应的Parser，也需要添加一条空语法 ` Expr ` 以便生成对应的非终止符
     */
    fun register(string: String) {
        val arr = splitProductionString(string)
        if (arr.size == 1) {
            val non = getOrCreateNonTerminal(arr[0])
            productions.add(Production(non, emptyList()))
            return
        }
        if (arr.size < 3 || arr[1] != "->" || !isNonTerminalName(arr[0])) {
            throw ParseException("语法 $string 错误")
        }
        val nonTerminal = getOrCreateNonTerminal(arr[0])
        val symbols = parseToSymbol(arr.subList(2, arr.size))
        val production = Production(nonTerminal, symbols)
        productions.add(production)
    }

    private fun splitProductionString(string: String): List<String> =
            string
                    .split(" ")
                    .filter { it.isNotBlank() }

    private fun isNonTerminalName(name: String) = name[0].isUpperCase()

    private fun getOrCreateNonTerminal(name: String): NonTerminal =
            if (nonTerminals.containsKey(name)) {
                nonTerminals[name]!!
            } else {
                NonTerminal(name).also { nonTerminals[name] = it }
            }

    private fun parseToSymbol(symbols: List<String>): List<Symbol> =
            symbols.map { getOrCreateSymbol(it) }

    private fun getOrCreateSymbol(name: String): Symbol =
            if (name == Symbol.EMPTY.name){
                Symbol.EMPTY
            } else if (isNonTerminalName(name)) {
                getOrCreateNonTerminal(name).symbol
            } else {
                getOrCreateTerminalSymbol(name)
            }

    private fun getOrCreateTerminalSymbol(name: String): Symbol =
            if (terminalSymbols.containsKey(name)) {
                terminalSymbols[name]!!
            } else {
                Symbol.from(name).also { terminalSymbols[name] = it }
            }

    /**
     * 根据 name 获取对应的id类型的 symbol
     */
    internal fun getSymbol(name: String): Symbol? =
            if (isNonTerminalName(name)) {
                nonTerminals[name]?.symbol
            } else {
                terminalSymbols[name]
            }


    internal fun gerNonTerminal(name: String): NonTerminal? = nonTerminals[name]


    fun make(): ParserTable {
        return ParserTable(productions)
    }

}