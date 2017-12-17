package moe.gogo.lang.lexer

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

class LexerTest : StringSpec() {

    init {
        "token amount count" {
            val lexer = buildLexer()
            val tokens = lexer.allTokens
            tokens.size shouldBe 17
        }
        "check line number with multi-line token"{
            val lexer = buildLexer()
            val tokens = lexer.allTokens
            tokens.find { it.isComment }?.lineNumber shouldBe 4
            tokens.find { it.text == "d" }?.lineNumber shouldBe 7
        }
        "peek"{
            val lexer = buildLexer()
            lexer.peek().text shouldBe "a"
            lexer.peek().text shouldBe "a"
            lexer.peek().text shouldBe "a"
            lexer.read()
            lexer.peek().text shouldBe "="
            lexer.peek().text shouldBe "="
        }
        "read"{
            val lexer = buildLexer()
            lexer.read().text shouldBe "a"
            lexer.read().text shouldBe "="
            lexer.read().text shouldBe "10"
            lexer.read().text shouldBe "b"
            lexer.read().text shouldBe "="
            lexer.read().name shouldBe "("
            lexer.read().name shouldBe "name"
            lexer.read().name shouldBe "+"
            lexer.read().name shouldBe "number"
            lexer.read().name shouldBe ")"
        }
    }

}

fun buildLexer(): Lexer {
    val lexicon = buildLexicon()
    val code = """
                | a = 10
                | b = (b + 5)
                | c >= 18
                | /*
                | hello
                | */
                | d = 8
            """.trimMargin()
    return Lexer(code.reader(), lexicon)
}