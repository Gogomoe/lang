package moe.gogo.lang.type

import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldNotBe
import io.kotlintest.matchers.shouldThrow
import io.kotlintest.specs.StringSpec
import moe.gogo.lang.LangRuntimeException

class ValueTest : StringSpec() {

    init {
        "get and set value"{
            val type = object : Type("Type") {
                init {
                    types["a"] = String
                }
            }
            val obj = Value(type)
            obj["a"] = "hehe".wrap()
            obj["a"].string() shouldBe "hehe"

            shouldThrow<LangRuntimeException> { obj["b"] }
        }
        "get value from type"{
            val type = Type("Type")
            val obj = Value(type)
            obj["toString"] shouldNotBe null
        }
    }

}