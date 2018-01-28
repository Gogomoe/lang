package moe.gogo.lang.type

import io.kotlintest.matchers.beGreaterThan
import io.kotlintest.matchers.beLessThan
import io.kotlintest.matchers.should
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

class LNumberTest : StringSpec() {

    init {
        Type
        "equals"{
            val equals = LNumber.getValue("equals")
            equals.call(1.5.wrap(), listOf(1.5.wrap())).bool() shouldBe true
            equals.call(1.5.wrap(), listOf(1.4999.wrap())).bool() shouldBe false
            equals.call(2.0.wrap(), listOf(2.wrap())).bool() shouldBe true
        }
        "toString"{
            val toString = LNumber.getValue("toString")
            toString.call(12345.wrap()).string() shouldBe "12345"
            toString.call(82.459.wrap()).string() shouldBe "82.459"
        }
        "arithmetic"{
            val plus = LNumber.getValue("plus")
            val minus = LNumber.getValue("minus")
            val times = LNumber.getValue("times")
            val div = LNumber.getValue("div")
            val mod = LNumber.getValue("mod")

            plus.call(4.5.wrap(), listOf(2.wrap())).num() shouldBe 6.5
            minus.call(4.5.wrap(), listOf(2.wrap())).num() shouldBe 2.5
            times.call(4.5.wrap(), listOf(2.wrap())).num() shouldBe 9.0
            div.call(4.5.wrap(), listOf(2.wrap())).num() shouldBe 2.25
            mod.call(4.5.wrap(), listOf(2.wrap())).num() shouldBe 0.5

            times.call(8.wrap(), listOf(2.wrap())).num() shouldBe 16
            div.call(7.wrap(), listOf(3.wrap())).num() shouldBe 2
        }
        "compareTo"{
            val compare = LNumber.getValue("compareTo")
            compare.call(2.5.wrap(), listOf(1.wrap())).int() should beGreaterThan(0)
            compare.call(7.0.wrap(), listOf(7.wrap())).int() shouldBe 0
            compare.call((-5.0).wrap(), listOf(18.6.wrap())).int() shouldBe beLessThan(0)
        }
    }

}