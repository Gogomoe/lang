package moe.gogo.lang.parser

import moe.gogo.lang.ast.ASTLeaf
import moe.gogo.lang.ast.ASTree
import moe.gogo.lang.ast.BinaryExpr
import moe.gogo.lang.lexer.Lexer
import moe.gogo.lang.parser.ExpressionParser.Association
import moe.gogo.lang.parser.ExpressionParser.Association.LEFT
import moe.gogo.lang.parser.ExpressionParser.Association.RIGHT


internal data class Precedence(val value: Int, val association: Association)

class ExpressionParser(private val register: ParserRegister,
                       private val productions: ProductionRegister,
                       private val factorName: String = "Factor") : Parser() {

    private fun factor(lexer: Lexer): ASTree {
        val symbol = productions.gerNonTerminal(factorName)?.symbol
                ?: throw ParseException("")
        val parser = register.findParser(symbol, lexer)
        return parser.parse(lexer)
    }

    private val operators = mutableMapOf<String, Precedence>()

    enum class Association {
        LEFT, RIGHT
    }

    fun defineOperator(name: String, precedence: Int, association: Association) {
        operators[name] = Precedence(precedence, association)
    }

    override fun parseList(lexer: Lexer): List<ASTree> {
        return listOf(expression(lexer))
    }

    private fun expression(lexer: Lexer): ASTree {
        var left = factor(lexer)
        var next = nextOperator(lexer)
        while (next != null) {
            left = doShift(left, next.value, lexer)
            next = nextOperator(lexer)
        }
        return left
    }

    private fun doShift(left: ASTree, prec: Int, lexer: Lexer): ASTree {
        val op = ASTLeaf(lexer.read())
        var right = factor(lexer)
        var next = nextOperator(lexer)
        while (next != null && rightIsExpr(prec, next)) {
            right = doShift(right, next.value, lexer)
            next = nextOperator(lexer)
        }
        return BinaryExpr(listOf(left, op, right))
    }


    private fun nextOperator(lexer: Lexer): Precedence? {
        val token = lexer.peek()
        return if (token.isIdentifier && operators.containsKey(token.name)) {
            operators[token.name]
        } else {
            null
        }
    }

    private fun rightIsExpr(prec: Int, next: Precedence): Boolean = when (next.association) {
        LEFT -> prec < next.value
        RIGHT -> prec <= next.value
    }

}
