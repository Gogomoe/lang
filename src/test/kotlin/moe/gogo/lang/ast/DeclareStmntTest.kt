package moe.gogo.lang.ast

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import moe.gogo.lang.env
import moe.gogo.lang.id
import moe.gogo.lang.num
import moe.gogo.lang.type.int

class DeclareStmntTest : StringSpec() {

    init {
        "eval"{
            val e1 = env()
            val e2 = env()
            DeclareStmnt(listOf(id("var"), id("a"))).eval(e1)
            DeclareStmnt(listOf(id("var"), id("b"), id("="), num(10))).eval(e2)

            e1.contains("a") shouldBe true
            e2["b"].int() shouldBe 10
        }
    }

}