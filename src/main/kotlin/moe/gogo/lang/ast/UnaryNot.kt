package moe.gogo.lang.ast

import moe.gogo.lang.Environment
import moe.gogo.lang.type.Value
import moe.gogo.lang.type.bool
import moe.gogo.lang.type.wrap

class UnaryNot(private val list: List<ASTree>) : ASTList(list) {

    override fun eval(env: Environment): Value? = (!list[1].eval(env).bool()).wrap()

    override fun toString(): String = "<!${list[1]}>"

}