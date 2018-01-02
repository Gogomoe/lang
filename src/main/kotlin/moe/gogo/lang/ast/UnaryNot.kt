package moe.gogo.lang.ast

import moe.gogo.lang.Environment
import moe.gogo.lang.toBool

class UnaryNot(private val list: List<ASTree>) : ASTList(list) {

    override fun eval(env: Environment): Any? = !list[1].eval(env).toBool()

    override fun toString(): String = "<!${list[1]}>"

}