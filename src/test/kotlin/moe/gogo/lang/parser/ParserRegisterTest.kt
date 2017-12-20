package moe.gogo.lang.parser

import io.kotlintest.specs.StringSpec
import moe.gogo.lang.ast.BinaryExpr
import moe.gogo.lang.ast.NumberLiteral
import moe.gogo.lang.lexer.Lexer
import moe.gogo.lang.lexer.buildLexicon

class ParserRegisterTest : StringSpec() {

    init {
        "bind type"{
            val register = builderParsers()
            val parser = register.findParser("Exp")!!
            val lexer = Lexer("5 + 8".reader(), buildLexicon())
            println(parser.parse(lexer))
        }
    }

}

private fun builderParsers(): ParserRegister {
    val register = ParserRegister(buildProductions())
    register.defineType("number", NumberLiteral::class)
    register.defineType("Exp", BinaryExpr::class)
    return register
}

private fun buildProductions(): ProductionRegister {
    val register = ProductionRegister()
    register.register("Exp -> Factor OpFactor")
    register.register("Factor -> number")
    register.register("OpFactor -> + Factor OpFactor")
    register.register("OpFactor -> ε")
    return register
}
