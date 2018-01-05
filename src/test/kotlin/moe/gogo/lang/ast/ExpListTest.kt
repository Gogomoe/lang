package moe.gogo.lang.ast

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import moe.gogo.lang.ast.ExpList.Companion.expList
import moe.gogo.lang.id
import moe.gogo.lang.num

class ExpListTest : StringSpec() {

    init {
        "build"{
            val exp = expList(listOf(num(1), id(","),
                    num(2), id(","),
                    num(3))) as ExpList

            exp.exps.size shouldBe 3

            (expList(emptyList()) as ExpList).exps.size shouldBe 0
        }
    }

}