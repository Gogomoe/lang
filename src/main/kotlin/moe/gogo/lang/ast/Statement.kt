package moe.gogo.lang.ast

import moe.gogo.lang.Environment
import moe.gogo.lang.type.Value

class Statement(list: List<ASTree>) : ASTList(list) {

    private val statement = list[0]

    override fun eval(env: Environment): Value? = statement.eval(env)

    override fun toString(): String = statement.toString()


}