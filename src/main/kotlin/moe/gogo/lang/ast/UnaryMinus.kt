package moe.gogo.lang.ast

import moe.gogo.lang.Environment

class UnaryMinus(private val list: List<ASTree>) : ASTList(list) {

    override fun eval(env: Environment): Any? = -(list[1].eval(env) as Int)

    override fun toString(): String = "<-${list[1]}>"

}