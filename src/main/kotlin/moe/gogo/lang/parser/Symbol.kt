package moe.gogo.lang.parser

internal sealed class Symbol(val name: String) {
    companion object {
        fun from(nonTerminal: NonTerminal): NonTerminalSymbol {
            return NonTerminalSymbol(nonTerminal)
        }

        fun from(name: String): TerminalSymbol {
            return TerminalSymbol(name)
        }
    }

    object END : Symbol("END") {
        override fun toString(): String = "$"
    }

    object EMPTY : Symbol("EMPTY") {
        override fun toString(): String = "ε"
    }

    fun isTerminal(): Boolean = !isNonTerminal()
    fun isNonTerminal(): Boolean = this is NonTerminalSymbol

    internal class NonTerminalSymbol(val nonTerminal: NonTerminal) : Symbol(nonTerminal.name) {
        override fun toString(): String = nonTerminal.toString()
    }

    internal class TerminalSymbol(name: String) : Symbol(name) {
        override fun toString(): String = name
    }

}

