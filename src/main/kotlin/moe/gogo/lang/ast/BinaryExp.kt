package moe.gogo.lang.ast

import moe.gogo.lang.Environment
import moe.gogo.lang.LangRuntimeException
import moe.gogo.lang.type.Type
import moe.gogo.lang.type.Value
import moe.gogo.lang.type.bool
import moe.gogo.lang.type.call
import moe.gogo.lang.type.int
import moe.gogo.lang.type.isa
import moe.gogo.lang.type.string
import moe.gogo.lang.type.wrap

class BinaryExp(list: List<ASTree>) : ASTList(list) {

    private val left = list[0]
    private val right = list[2]

    private val operator = (list[1] as ASTLeaf).token.text

    override fun eval(env: Environment): Value? {
        if (left is Identifier && operator == "=") {
            return evalAssign(env)
        }
        val l = left.eval(env)
        val r = right.eval(env)
        if ((l isa Type.String || r isa Type.String) && operator == "+") {
            return (l.string() + r.string()).wrap()
        }
        when (operator) {
            "==" -> return (
                    if (l != null)
                        l["equals"].call(l, listOf(r)).bool()
                    else
                        l == r).wrap()
            "!=" -> return (
                    if (l != null)
                        l["equals"].call(l, listOf(r)).bool().not()
                    else
                        l != r).wrap()
            "&&" -> return (l.bool() && r.bool()).wrap()
            "||" -> return (l.bool() || r.bool()).wrap()
        }
        l ?: throw LangRuntimeException("null 不支持运算符")
        return when (operator) {
            "+" -> l["plus"].call(l, listOf(r))
            "-" -> l["minus"].call(l, listOf(r))
            "*" -> l["times"].call(l, listOf(r))
            "/" -> l["div"].call(l, listOf(r))
            "%" -> l["mod"].call(l, listOf(r))
            ">" -> (l["compareTo"].call(l, listOf(r)).int() > 0).wrap()
            ">=" -> (l["compareTo"].call(l, listOf(r)).int() >= 0).wrap()
            "<" -> (l["compareTo"].call(l, listOf(r)).int() < 0).wrap()
            "<=" -> (l["compareTo"].call(l, listOf(r)).int() <= 0).wrap()
            else -> throw LangRuntimeException("不支持运算 ${toString()}")

        }
    }

    private fun evalAssign(env: Environment): Value? = right.eval(env).also {
        env[(left as Identifier).id] = it
    }

    override fun toString(): String = "<$left $operator $right>"

}