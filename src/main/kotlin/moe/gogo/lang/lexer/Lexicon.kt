package moe.gogo.lang.lexer

import kotlin.reflect.KClass


class Lexicon {

    private val tokensField = mutableListOf<TokenMatcher>()

    val tokens: List<TokenMatcher> = tokensField

    fun defineString(id: String, matcher: String = id): StringTokenMatcher =
            StringTokenMatcher(id, matcher).also { tokensField.add(it) }

    fun defineArgument(id: String, regex: Regex, type: KClass<out Token>): ArgumentTokenMatcher =
            ArgumentTokenMatcher(id, regex, type).also { tokensField.add(it) }

    fun defineMultiWords(id: String, start: Regex, end: Regex, type: KClass<out Token>): MultiWordsTokenMatcher =
            MultiWordsTokenMatcher(id, start, end, type).also { tokensField.add(it) }
}