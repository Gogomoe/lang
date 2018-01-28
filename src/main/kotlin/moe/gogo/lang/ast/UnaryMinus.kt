package moe.gogo.lang.ast

import moe.gogo.lang.Environment
import moe.gogo.lang.type.Value
import moe.gogo.lang.type.num
import moe.gogo.lang.type.wrap

class UnaryMinus(private val list: List<ASTree>) : ASTList(list) {

    override fun eval(env: Environment): Value? {
        val num = list[1].eval(env).num()
        return if (num is Int || num is Long) {
            (-num.toInt()).wrap()
        } else {
            (-num.toDouble()).wrap()
        }
    }

    override fun toString(): String = "<-${list[1]}>"

}