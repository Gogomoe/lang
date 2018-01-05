package moe.gogo.lang.ast

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import moe.gogo.lang.type.Function
import moe.gogo.lang.Environment
import moe.gogo.lang.eval

class FunctionCallTest : StringSpec() {

    init {
        "eval"{
            val func = object : ASTList(emptyList()) {
                override fun eval(env: Environment): Any? = Function { list: List<Any?> ->
                    "Hello" + list[0]
                }
            }
            val param = object : ASTList(emptyList()) {
                override fun eval(env: Environment): Any? = "World"
            }
            FunctionCall(func, listOf(param)).eval() shouldBe "HelloWorld"
        }
    }

}