package moe.gogo.lang.type

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

class LStringTest : StringSpec() {

    init {
        Type
        "toString"{
            val toString = LString.getValue("toString")
            toString.call("hello".wrap()).string() shouldBe "hello".wrap().string()
        }
        "equals"{
            val equals = LString.getValue("equals")
            equals.call("abc".wrap(), listOf("abc".wrap())).bool() shouldBe true
        }
    }

}