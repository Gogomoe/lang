package moe.gogo.lang.ast

import moe.gogo.lang.Environment
import moe.gogo.lang.LangRuntimeException
import moe.gogo.lang.type.NativeFunction
import moe.gogo.lang.type.Type
import moe.gogo.lang.type.Value
import moe.gogo.lang.type.nota


class FunctionCall(internal val functionName: ASTree, internal val params: List<ASTree>)
    : ASTList(listOf(functionName, *params.toTypedArray())) {

    override fun eval(env: Environment): Value? {
        val func = functionName.eval(env)
        if (func nota Type.Function) {
            throw LangRuntimeException("$func 不是一个方法")
        }
        val ps = params.map { it.eval(env) }
        return (func as NativeFunction).call(null, ps)
    }

    override fun toString(): String {
        return "$functionName($params)"
    }
}