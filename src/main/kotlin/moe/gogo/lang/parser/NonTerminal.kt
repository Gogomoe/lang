package moe.gogo.lang.parser

import moe.gogo.lang.parser.Symbol.EMPTY
import moe.gogo.lang.parser.Symbol.END
import moe.gogo.lang.parser.Symbol.NonTerminalSymbol

internal class NonTerminal(val name: String) {

    val productions: MutableSet<Production> = mutableSetOf()

    var nullable: Boolean = false

    val symbol: NonTerminalSymbol = Symbol.from(this)

    val first: MutableSet<Symbol> = mutableSetOf()

    val follow: MutableSet<Symbol> = mutableSetOf()

    init {
        follow.add(END)
    }

    fun addFollow(symbol: Symbol) {
        if (symbol != EMPTY) {
            follow.add(symbol)
        }
    }

    fun addFollow(symbols: Set<Symbol>) {
        val c = symbols - EMPTY
        follow.addAll(c)
    }

    override fun toString(): String = name

}