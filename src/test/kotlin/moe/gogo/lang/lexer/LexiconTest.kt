package moe.gogo.lang.lexer

import moe.gogo.lang.lexer.Token.CommentToken
import moe.gogo.lang.lexer.Token.IdToken
import moe.gogo.lang.lexer.Token.NumToken

fun buildLexicon(): Lexicon {
    val lexicon = Lexicon()
    lexicon.defineString("if")
    lexicon.defineString("else")
    lexicon.defineString("for")

    lexicon.defineString("&&")
    lexicon.defineString("||")
    lexicon.defineString("!")
    lexicon.defineString("<=")
    lexicon.defineString("<")
    lexicon.defineString(">=")
    lexicon.defineString(">")
    lexicon.defineString("==")
    lexicon.defineString("=")
    lexicon.defineString("+")
    lexicon.defineString("-")
    lexicon.defineString("*")
    lexicon.defineString("/")
    lexicon.defineString("(")
    lexicon.defineString(")")
    lexicon.defineString("[")
    lexicon.defineString("]")
    lexicon.defineString("{")
    lexicon.defineString("}")
    lexicon.defineString(".")
    lexicon.defineString(",")
    lexicon.defineString(":")
    lexicon.defineString(";")

    lexicon.defineMultiWords("comment", Regex("""//"""), Regex("""\n"""), CommentToken::class)
    lexicon.defineMultiWords("comment", Regex("""/\*"""), Regex("""\*/"""), CommentToken::class)

    lexicon.defineArgument("number", Regex("""\d+"""), NumToken::class)
    lexicon.defineArgument("id", Regex("""[\p{Lu}\p{Ll}\p{Lt}\p{Lm}\p{Lo}\p{Nl}][\p{Lu}\p{Ll}\p{Lt}\p{Lm}\p{Lo}\p{Nl}\p{Mn}\p{Mc}\p{Nd}\p{Pc}\p{Cf}]*"""), IdToken::class)

    return lexicon
}