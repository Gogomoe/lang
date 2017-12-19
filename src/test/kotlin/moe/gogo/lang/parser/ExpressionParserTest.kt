package moe.gogo.lang.parser

import io.kotlintest.specs.StringSpec
import moe.gogo.lang.ast.NumberLiteral
import moe.gogo.lang.lexer.Lexer
import moe.gogo.lang.lexer.buildLexicon
import moe.gogo.lang.parser.ExpressionParser.Association.LEFT

class ExpressionParserTest : StringSpec() {

    init {
        "register parser"{
            val register = builderParsers()
            val parser = register.findParser("Exp")!!
            val lexer = Lexer("5 + 8 * 2".reader(), buildLexicon())
            println(parser.parse(lexer))
        }
    }

}


private fun builderParsers(): ParserRegister {
    val productions = buildProductions()
    val register = ParserRegister(productions)
    val expr = buildExprParser(register, productions)
    register.defineType("number", NumberLiteral::class)
    register.register("Exp", expr)
    return register
}

fun buildExprParser(register: ParserRegister, productions: ProductionRegister): Parser =
        ExpressionParser(register, productions).also {
            it.defineOperator("+", 1, LEFT)
            it.defineOperator("*", 2, LEFT)
        }

private fun buildProductions(): ProductionRegister {
    val register = ProductionRegister()
    register.register("Factor -> number")
    register.register("Exp")
    return register
}
