package moe.gogo.lang.parser

import io.kotlintest.specs.StringSpec
import moe.gogo.lang.ast.ASTList
import moe.gogo.lang.ast.ASTree
import moe.gogo.lang.ast.Identifier
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
        "complex expression"{
            val productions = ProductionRegister()
            productions.register("Exp")
            productions.register("Factor -> number")
            productions.register("Factor -> id")
            productions.register("Factor -> If")
            productions.register("If -> if ( Exp ) Exp else Exp")

            val register = ParserRegister(productions)
            val expr = ExpressionParser(register, productions)
            expr.defineOperator(">" ,1,LEFT)
            expr.defineOperator("+", 2, LEFT)
            expr.defineOperator("*", 3, LEFT)

            register.defineType("number", NumberLiteral::class)
            register.defineType("id", Identifier::class)
            register.defineType("If", IfExpr::class)
            register.register("Exp", expr)

            val parser = register.findParser("Exp")!!
            val lexer = Lexer("5 + if (a > 5) 10 else b + 5".reader(), buildLexicon())
            println(parser.parse(lexer))
        }
    }

}

class IfExpr(list: List<ASTree>) : ASTList(list) {

    val condition = list[2]
    val success = list[4]
    val fail = list[6]

    override fun toString(): String = "<if $condition \n $success \n $fail>"

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
