package moe.gogo.lang.ast

import moe.gogo.lang.Environment

class Statements(list: List<ASTree>) : ASTList(list) {

    internal val statements = list

    override fun eval(env: Environment): Any? {
        var res: Any? = null
        statements.forEach {
            res = it.eval(env)
        }
        return res
    }

    override fun toString(): String {
        return statements.joinToString("\n")
    }


    companion object {
        fun statements(list: List<ASTree>): ASTree {
            return if (list.size == 2) {
                val sublist = list[1] as Statements
                val l = listOf(list[0], *sublist.statements.toTypedArray())
                Statements(l)
            } else {
                Statements(list)
            }
        }
    }

}