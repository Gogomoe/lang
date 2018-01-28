package moe.gogo.lang.ast

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import moe.gogo.lang.Environment
import moe.gogo.lang.eval
import moe.gogo.lang.type.NativeFunction
import moe.gogo.lang.type.Type
import moe.gogo.lang.type.Value
import moe.gogo.lang.type.string

class FunctionCallTest : StringSpec() {

    init {
        "eval"{
            val func = object : ASTList(emptyList()) {
                override fun eval(env: Environment): Value? = NativeFunction { list: List<Value?> ->
                    Type.String.create("Hello" + list[0].string())
                }
            }
            val param = object : ASTList(emptyList()) {
                override fun eval(env: Environment): Value? = Type.String.create("World")
            }
            FunctionCall(func, listOf(param)).eval().string() shouldBe "HelloWorld"
        }
    }

}