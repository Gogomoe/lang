package moe.gogo.lang.ast

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import moe.gogo.lang.env
import moe.gogo.lang.id
import moe.gogo.lang.num

class BinaryExpTest : StringSpec() {

    init {
        "eval"{
            BinaryExp(listOf(num(10), id("+"), num(15))).eval() shouldBe 25
            BinaryExp(listOf(num(10), id("-"), num(15))).eval() shouldBe -5
            BinaryExp(listOf(num(10), id("*"), num(15))).eval() shouldBe 150
            BinaryExp(listOf(num(80), id("/"), num(5))).eval() shouldBe 16
        }
    }

    private val env = env()
    private fun ASTree.eval() = this.eval(env)
}