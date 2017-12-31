package moe.gogo.lang.ast

import moe.gogo.lang.Environment
import moe.gogo.lang.LangRuntimeException
import moe.gogo.lang.toBool

class BinaryExpr(list: List<ASTree>) : ASTList(list) {

    val left = list[0]
    val right = list[2]

    val operator = (list[1] as ASTLeaf).token.text

    override fun eval(env: Environment): Any? {
        val l = left.eval(env)
        val r = right.eval(env)
        if ((l is String || r is String) && operator == "+") {
            return l.toString() + r.toString()
        }
        return when {
            operator == "==" -> l == r
            operator == "!=" -> l != r
            operator == "&&" -> l.toBool() && r.toBool()
            operator == "||" -> l.toBool() || r.toBool()
            l is Int && r is Int -> evalInt(r, l, operator)
            else -> throw LangRuntimeException("不支持运算 ${toString()}")

        }
    }

    private fun evalInt(r: Int, l: Int, operator: String): Any? = when (operator) {
        "+" -> l + r
        "-" -> l - r
        "*" -> l * r
        "/" -> l / r
        "%" -> l % r
        ">" -> l > r
        ">=" -> l >= r
        "<" -> l <= r
        else -> throw LangRuntimeException("不支持运算 ${toString()}")
    }


    override fun toString(): String = "<$left $operator $right>"

}