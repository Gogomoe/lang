package moe.gogo.lang.ast

import moe.gogo.lang.Environment
import moe.gogo.lang.type.Value


class ExpList(list: List<ASTree>) : ASTList(list) {

    internal val exps = list

    override fun eval(env: Environment): Value? {
        var res: Value? = null
        exps.forEach {
            res = it.eval(env)
        }
        return res
    }

    override fun toString(): String {
        return exps.joinToString(", ")
    }


    companion object {
        fun expList(list: List<ASTree>): ASTree {
            return if (list.size > 1) {
                ExpList(list.withIndex().filter { it.index % 2 == 0 }.map { it.value })
            } else {
                ExpList(list)
            }
        }
    }

}