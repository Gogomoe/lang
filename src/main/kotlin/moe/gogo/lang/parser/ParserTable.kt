package moe.gogo.lang.parser

import com.google.common.collect.HashBasedTable
import com.google.common.collect.Table
import moe.gogo.lang.parser.Symbol.EMPTY
import moe.gogo.lang.parser.Symbol.NonTerminalSymbol

class ParserTable internal constructor(private val productions: Set<Production>) {

    private val nonTerminals: MutableSet<NonTerminal> = mutableSetOf()

    private val table: Table<NonTerminal, Symbol, Production> = HashBasedTable.create()

    private val nullableAnalyzer: NullableAnalyzer = NullableAnalyzer()

    private val firstAnalyzer: FirstAnalyzer = FirstAnalyzer()

    private val followAnalyzer: FollowAnalyzer = FollowAnalyzer()

    private val tableMaker: TableMaker = TableMaker()

    init {
        collectNonTerminals()

        nullableAnalyzer.analyzeAll()

        firstAnalyzer.analyzeAll()

        followAnalyzer.analyzeAll()

        tableMaker.make()

        nonTerminals.forEach {
            output(it.name)
            output(it.first)
            println()
        }
        println()
        nonTerminals.forEach {
            output(it.name)
            output(it.first)
            println()
        }

        tableMaker.printTable()
    }

    private fun collectNonTerminals() {
        productions.forEach {
            nonTerminals.add(it.nonTerminal)
        }
    }

    private inner class NullableAnalyzer {

        fun analyzeAll() {
            var count = -1
            while (count != countNullable()) {
                count = countNullable()
                productions.forEach {
                    val res = analyze(it.symbols)
                    it.nullable = it.nullable || res
                    it.nonTerminal.nullable = it.nonTerminal.nullable || res
                }
            }
        }

        private fun countNullable(): Int = productions.filter { it.nullable }.size

        fun analyze(symbols: List<Symbol>): Boolean {
            if (symbols.isEmpty()) {
                return true
            }
            symbols.forEach {
                if (it != Symbol.EMPTY) {
                    if (it.isTerminal()) {
                        return false
                    }
                    it as NonTerminalSymbol
                    if (!it.nonTerminal.nullable) {
                        return false
                    }
                }
            }
            return true
        }
    }

    private inner class FirstAnalyzer {
        // 直到 First 集合数量不再改变，说明生成结束
        fun analyzeAll() {
            var count = -1
            while (count != countFirst()) {
                count = countFirst()
                productions.forEach {
                    analyze(it)
                }
            }
        }

        private fun countFirst(): Int = productions.map { it.first.size }.reduce { acc, i -> acc + i }

        private fun analyze(production: Production) {
            for (symbol in production.symbols) {
                if (symbol == EMPTY) {
                    production.addFirst(symbol)
                    continue
                }
                // 第一个是终止符
                if (symbol.isTerminal()) {
                    production.addFirst(symbol)
                    break
                }
                // 第一个是非终止符
                if (symbol.isNonTerminal()) {
                    symbol as NonTerminalSymbol
                    production.addFirst(symbol.nonTerminal.first)
                    // 如果第一个符号不为空，则结束
                    // 换而言之，如果第一个字符可能为空，那么 first 应包括下一个字符的 first
                    if (!symbol.nonTerminal.nullable) {
                        break
                    }
                }
            }
        }
    }

    private inner class FollowAnalyzer {
        // 直到 Follow 集合数量不再改变，说明生成结束
        fun analyzeAll() {
            var count = -1
            while (count != countFollow()) {
                count = countFollow()
                productions.forEach {
                    analyze(it)
                }
            }
        }

        private fun countFollow(): Int = nonTerminals.map { it.follow.size }.reduce { acc, i -> acc + i }

        private fun analyze(production: Production) {
            val nonTerminal = production.nonTerminal
            val symbols = production.symbols
            for (i in 0 until symbols.size) {
                if (symbols[i].isNonTerminal()) {
                    val current = (symbols[i] as NonTerminalSymbol).nonTerminal

                    // 是表达式最后一个字符
                    if (i == symbols.size - 1) {
                        current.addFollow(nonTerminal.follow)
                        break
                    }

                    // 下个是终止符
                    val next = symbols[i + 1]
                    if (next.isTerminal() && next != Symbol.EMPTY) {
                        current.addFollow(next)
                        break
                    }

                    // 下个是非终止符
                    next as NonTerminalSymbol
                    current.addFollow(next.nonTerminal.first)

                    // 到结尾都有可能是空，则可能这个符号后直接结束了
                    if (nullableAnalyzer.analyze(symbols.subList(i + 1, symbols.size))) {
                        current.addFollow(nonTerminal.follow)
                    }
                }
            }
        }
    }

    private inner class TableMaker {

        fun make() {
            productions.forEach { production ->
                val nonterminal = production.nonTerminal

                // 遇到 first 中的关键字解析此 grammar
                production.first.forEach { symbol ->
                    if (symbol != Symbol.EMPTY) {
                        putTable(nonterminal, symbol, production)
                    }
                }
                // 如果这个 production 可能为空，遇到 follow 也会解析此 grammar
                if (production.nullable) {
                    nonterminal.follow.forEach { symbol ->
                        putTable(nonterminal, symbol, production)
                    }
                }
            }
        }

        fun printTable() {
            val keys = table.rowKeySet()
            val values = table.columnKeySet()


            output("")
            values.forEach {
                output(it)
            }
            println()
            keys.forEach { row ->
                output(row.symbol)
                values.forEach { column ->
                    val str = getTable(row, column)?.symbols?.joinToString("") ?: "ERROR"
                    output(str)
                }
                println()
            }
            println()
            println()
            println()
        }
    }

    private fun putTable(row: NonTerminal, column: Symbol, production: Production) {
        if (table.contains(row, column) && table.get(row, column) != production) {
            println("Warning: $row,$column 已存在 ${table.get(row, column)} ,被替换为 $production")
        }
        table.put(row, column, production)
    }


    internal fun getTable(row: NonTerminal, column: Symbol): Production? =
            if (table.contains(row, column)) table.get(row, column) else null

    fun output(any: Any?, length: Int = 15) {
        val str = if (any.toString().length >= length - 3)
            any.toString().substring(0..(length - 5)) + ".."
        else
            any.toString()
        print(String.format("%15s", str))
    }
}