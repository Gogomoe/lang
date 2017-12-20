package moe.gogo.lang.parser

import moe.gogo.lang.ast.ASTLeaf
import moe.gogo.lang.ast.ASTree
import moe.gogo.lang.ast.BinaryExpr
import moe.gogo.lang.lexer.Lexer
import moe.gogo.lang.parser.ExpressionParser.Association
import moe.gogo.lang.parser.ExpressionParser.Association.LEFT
import moe.gogo.lang.parser.ExpressionParser.Association.RIGHT


internal data class Precedence(val value: Int, val association: Association)

/**
 * 采用 算符优先分析法 的易于解析表达式的Parser。
 *
 * 可以通过 [defineOperator] 定义二元运算符，并设定它的优先级、左结合或右结合。
 *
 * 构造时需要指定 Factor 的 name，并在语法中包含 Factor 对应的解析式
 *
 * @see ParserRegister
 */
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

    /**
     * 定义一个二元运算符
     * @param name 二元运算符的符号，即对应Token的name
     * @param precedence 优先级，数字越大优先级越高
     * @param association 结合方式，左结合或右结合
     */
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
