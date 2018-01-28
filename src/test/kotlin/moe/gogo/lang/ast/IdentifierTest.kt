package moe.gogo.lang.ast

import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldThrow
import io.kotlintest.specs.StringSpec
import moe.gogo.lang.LangRuntimeException
import moe.gogo.lang.env
import moe.gogo.lang.id
import moe.gogo.lang.type.int
import moe.gogo.lang.type.wrap

class IdentifierTest : StringSpec() {

    init {

        "eval"{
            val env = env()
            env.putNew("a", 10.wrap())
            fun ASTree.eval() = this.eval(env)

            id("a").eval().int() shouldBe 10

            shouldThrow<LangRuntimeException> {
                id("b").eval()
            }
        }
    }

}