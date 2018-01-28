package moe.gogo.lang.lexer

import moe.gogo.lang.lexer.Lexer.LexerState
import moe.gogo.lang.lexer.Token.CommentToken
import moe.gogo.lang.lexer.Token.IdToken
import moe.gogo.lang.lexer.Token.NumToken
import moe.gogo.lang.lexer.Token.StrToken
import kotlin.reflect.KClass

abstract class TokenMatcher(val id: String) {

    abstract fun toToken(lineNo: Int, string: String): Token

    abstract fun matches(string: String): Boolean

    open internal fun putLexeme(state: LexerState, string: String) {
        state.add(toToken(state.lineNo, string))
    }

    override fun toString(): String = id

}

class StringTokenMatcher(id: String, val matcher: String) : TokenMatcher(id) {

    override fun toToken(lineNo: Int, string: String): Token = IdToken(lineNo, id, string)

    override fun matches(string: String): Boolean = string == matcher

}

class ArgumentTokenMatcher(
        id: String,
        private val regex: Regex,
        private val type: KClass<out Token>) : TokenMatcher(id) {

    override fun toToken(lineNo: Int, string: String): Token = buildToken(type, lineNo, id, string)

    override fun matches(string: String): Boolean = regex.matches(string)

}

class MultiWordsTokenMatcher(
        id: String,
        private val start: Regex,
        end: Regex,
        private val type: KClass<out Token>) : TokenMatcher(id) {

    private val startMacher = Regex(start.pattern + ".*")

    override fun toToken(lineNo: Int, string: String): Token = buildToken(type, lineNo, id, string)

    override fun matches(string: String): Boolean = startMacher.matches(string)

    override fun putLexeme(state: LexerState, string: String) {
        val builder = StringBuilder()
        val lineNo = state.lineNo

        handleBehindStartString(string, builder)

        while (state.hasWords()) {
            val it = state.nextWord()
            if (handleStop(it, builder, state)) {
                state.add(toToken(lineNo, builder.toString()))
                return
            }
            builder.append(it)
        }

    }

    private fun handleBehindStartString(string: String, builder: StringBuilder) {
        val behind = string.replace(start, "")
        if (behind.isNotEmpty()) {
            builder.append(behind)
        }
    }

    private val endMatcher = Regex("(.*)${end.pattern}(.*)")

    private fun handleStop(
            it: String,
            builder: StringBuilder,
            state: LexerState): Boolean {

        if (endMatcher.matches(it)) {
            handleStopRemindString(it, builder, state)
            return true
        }
        return false
    }

    private fun handleStopRemindString(it: String, builder: StringBuilder, state: LexerState) {
        val matcher = endMatcher.toPattern().matcher(it)!!
        matcher.lookingAt()
        if (matcher.group(1).isNotEmpty()) {
            builder.append(matcher.group(1))
        }
        if (matcher.group(2).isNotEmpty()) {
            state.words.push(matcher.group(2))
        }
    }

}

private fun buildToken(type: KClass<out Token>, lineNo: Int, id: String, string: String): Token = when (type) {
    IdToken::class -> IdToken(lineNo, id, string)
    NumToken::class -> NumToken(lineNo, id, if (string.contains('.')) string.toDouble() else string.toInt())
    StrToken::class -> StrToken(lineNo, id, string)
    CommentToken::class -> CommentToken(lineNo, id, string)
    else -> throw IllegalStateException("Token类型不正确")
}
