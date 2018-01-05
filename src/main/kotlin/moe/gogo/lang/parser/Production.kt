package moe.gogo.lang.parser

internal class Production(val nonTerminal: NonTerminal, val symbols: List<Symbol>, val production: String) {

    var nullable: Boolean = false

    val first: MutableSet<Symbol> = mutableSetOf()

    init {
        nonTerminal.productions.add(this)
    }

    fun addFirst(symbol: Symbol) {
        first.add(symbol)
        nonTerminal.first.add(symbol)
    }

    fun addFirst(symbols: Collection<Symbol>) {
        first.addAll(symbols)
        nonTerminal.first.addAll(symbols)
    }

    private fun symbolsString(): String = symbols.joinToString("")

    override fun toString(): String = production
}

