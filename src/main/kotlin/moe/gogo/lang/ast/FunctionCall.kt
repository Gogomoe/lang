package moe.gogo.lang.ast

import moe.gogo.lang.Environment
import moe.gogo.lang.LangRuntimeException
import moe.gogo.lang.type.Function


class FunctionCall(internal val function: ASTree, internal val params: List<ASTree>)
    : ASTList(listOf(function, *params.toTypedArray())) {

    override fun eval(env: Environment): Any? {
        val func = function.eval(env)
        if (func !is Function) {
            throw LangRuntimeException("$func 不是一个方法")
        }
        val ps = params.map { it.eval(env) }
        return func.call(ps)
    }

    override fun toString(): String {
        return "$function($params)"
    }
}