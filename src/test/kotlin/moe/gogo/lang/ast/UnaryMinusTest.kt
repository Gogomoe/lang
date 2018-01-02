package moe.gogo.lang.ast

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import moe.gogo.lang.env
import moe.gogo.lang.eval
import moe.gogo.lang.id
import moe.gogo.lang.num

class UnaryMinusTest : StringSpec() {

    init {
        "eval"{
            UnaryMinus(listOf(id("-"), num(5))).eval() shouldBe -5
        }
    }

}