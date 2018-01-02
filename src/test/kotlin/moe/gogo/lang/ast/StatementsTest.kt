package moe.gogo.lang.ast

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import moe.gogo.lang.ast.Statements.Companion.statements
import moe.gogo.lang.env
import moe.gogo.lang.eval
import moe.gogo.lang.num

class StatementsTest : StringSpec() {

    init {
        "single statement"{
            statements(listOf(num(3))).eval() shouldBe 3
        }
        "multi statements"{
            val s1 = statements(listOf(num(6)))
            val s2 = statements(listOf(num(5),s1))
            val s3 = statements(listOf(num(4),s2))
            s3 as Statements
            s3.statements.size shouldBe 3
            s3.eval() shouldBe 6
        }
    }

}