package moe.gogo.lang.ast

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import moe.gogo.lang.env
import moe.gogo.lang.eval
import moe.gogo.lang.num

class StatementTest : StringSpec() {

    init {
        "eval"{
            Statement(listOf(num(78))).eval() shouldBe 78
        }
    }

}