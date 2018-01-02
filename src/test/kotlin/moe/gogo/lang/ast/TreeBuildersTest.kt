package moe.gogo.lang.ast

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import moe.gogo.lang.eval
import moe.gogo.lang.id
import moe.gogo.lang.num

class TreeBuildersTest : StringSpec() {

    init {
        "factor"{
            factor(listOf(id("("), num(15), id(")"))).eval() shouldBe 15
            factor(listOf(id("-"), num(8))).eval() shouldBe -8
            factor(listOf(id("!"), num(6))).eval() shouldBe false
        }
    }

}