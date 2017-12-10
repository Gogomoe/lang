package moe.gogo.lang.lexer

import moe.gogo.lang.lexer.Token.EOF
import java.io.Reader
import java.util.ArrayDeque
import java.util.Deque

class Lexer(reader: Reader, val lexicon: Lexicon) {

    private val tokens = ArrayDeque<Token>()

    val allTokens: List<Token>

    init {
        val text = reader.readText()
        val words = splitString(text)
        buildTokens(words)
        allTokens = ArrayList(tokens)
    }


    private fun splitString(string: String): Deque<String> {
        val split = Regex(
                """[\p{Lu}\p{Ll}\p{Lt}\p{Lm}\p{Lo}\p{Nl}][\p{Lu}\p{Ll}\p{Lt}\p{Lm}\p{Lo}\p{Nl}\p{Mn}\p{Mc}\p{Nd}\p{Pc}\p{Cf}]*""" + "|" +
                        """\d+""" + "|" +
                        """[\p{Sm}\p{Sc}\p{Sk}\p{So}\p{Pd}\p{Pc}\p{Po}]+""" + "|" +
                        """[\p{Ps}\p{Pe}\p{Pi}\p{Pf}]""" + "|" +
                        """[\s&&[^\r\n]]+|\n"""
        )
        val lexemes = split.findAll(string).map { it.value }
        return ArrayDeque(lexemes.toList())
    }

    private fun buildTokens(words: Deque<String>) {
        var lineNo = 1
        while (!words.isEmpty()) {
            val it = words.pop()
            if (it.contains('\n')) {
                lineNo++
            }
            if (it.isNotEmpty() && it.isNotBlank()) {
                val matcher: TokenMatcher? = selectMatcher(it)
                val state = LexerState(tokens, words, lineNo)
                matcher?.putLexeme(state, it)
                lineNo = state.lineNo
            }
        }
    }

    internal class LexerState(
            val tokens: MutableCollection<Token>,
            val words: Deque<String>,
            var lineNo: Int) {
        fun add(token: Token) = tokens.add(token)
        fun hasWords() = words.isNotEmpty()
        fun nextWord() = words.pop().also {
            if (it.contains('\n')) {
                newLine()
            }
        }!!

        fun newLine() = lineNo++
    }

    private fun selectMatcher(string: String): TokenMatcher?
            = lexicon.tokens.find { it.matches(string) }

    fun peek(): Token = tokens.peek() ?: EOF

    fun read(): Token = if (tokens.isNotEmpty()) tokens.pop() else EOF

}