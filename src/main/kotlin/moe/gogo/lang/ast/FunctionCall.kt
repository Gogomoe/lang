package moe.gogo.lang.ast

import moe.gogo.lang.Environment
import moe.gogo.lang.LangRuntimeException
import moe.gogo.lang.type.NativeFunction
import moe.gogo.lang.type.Type
import moe.gogo.lang.type.Value
import moe.gogo.lang.type.nota


class FunctionCall(internal val function: ASTree, internal val params: List<ASTree>)
    : ASTList(listOf(function, *params.toTypedArray())) {

    override fun eval(env: Environment): Value? {
        val func = function.eval(env)
        if (func nota Type.Function) {
            throw LangRuntimeException("$func 不是一个方法")
        }
        val ps = params.map { it.eval(env) }
        return (func as NativeFunction).call(null, ps)
    }

    override fun toString(): String {
        return "$function($params)"
    }
}