package moe.gogo.lang.ast

import moe.gogo.lang.Environment
import moe.gogo.lang.LangRuntimeException

class BinaryExpr(list: List<ASTree>) : ASTList(list) {


    val left = list[0]
    val right = list[2]

    val operator = (list[1] as ASTLeaf).token.text

    override fun eval(env: Environment): Any? {
        val l = left.eval(env)
        val r = right.eval(env)
        if ((l is String || r is String) && operator == "+") {
            return l as String + r as String
        }
        l as Int
        r as Int
        return when (operator) {
            "+" -> l + r
            "-" -> l - r
            "*" -> l * r
            "/" -> l / r
            "%" -> l % r
            else -> throw LangRuntimeException("不支持运算 ${toString()}")
        }
    }


    override fun toString(): String = "<$left $operator $right>"

}