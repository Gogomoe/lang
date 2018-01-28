package moe.gogo.lang.ast

import moe.gogo.lang.Environment
import moe.gogo.lang.type.Value

class DeclareStmnt(val list: List<ASTree>) : ASTList(list) {

    private val left = list[1] as Identifier

    private val right = list.getOrNull(3)

    override fun eval(env: Environment): Value? = right?.eval(env).also {
        env.putNew(left.id, it)
    }

    override fun toString(): String {
        val ass = if (right != null) "= $right" else ""
        return "var $left $ass"
    }
}