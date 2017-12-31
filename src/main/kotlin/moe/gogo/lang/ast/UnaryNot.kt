package moe.gogo.lang.ast

import moe.gogo.lang.Environment

class UnaryNot(private val list: List<ASTree>) : ASTList(list) {

    override fun eval(env: Environment): Any? = !(list[1].eval(env) as Boolean)

    override fun toString(): String = "<!${list[1]}>"

}