package moe.gogo.lang.ast

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import moe.gogo.lang.env
import moe.gogo.lang.eval
import moe.gogo.lang.id
import moe.gogo.lang.num
import moe.gogo.lang.type.int

class UnaryMinusTest : StringSpec() {

    init {
        "eval"{
            UnaryMinus(listOf(id("-"), num(5))).eval().int() shouldBe -5
        }
    }

}