package moe.gogo.lang.ast

import moe.gogo.lang.Environment
import moe.gogo.lang.LangRuntimeException
import moe.gogo.lang.type.Value


abstract class ASTree : Iterable<ASTree> {

    abstract val size: Int

    abstract val location: String

    abstract operator fun get(i: Int): ASTree

    open fun eval(env: Environment): Value? = throw LangRuntimeException("不支持 eval 函数")

    abstract fun children(): Iterator<ASTree>

    override fun iterator(): Iterator<ASTree> = children()
}