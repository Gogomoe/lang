package moe.gogo.lang.ast

import moe.gogo.lang.Environment

class AssignStmnt(list: List<ASTree>) : ASTList(list) {

    private val left = list[0] as Identifier

    private val right = list[2]

    override fun eval(env: Environment): Any? = right.eval(env).also {
        env.set(left.id, it)
    }

}