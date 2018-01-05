package moe.gogo.lang.ast

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import moe.gogo.lang.ast.FunctionCallPostfix.Companion.functionCallPostfix
import moe.gogo.lang.id
import moe.gogo.lang.num

class FunctionCallPostfixTest : StringSpec() {

    init {
        "build"{
            val l = listOf(
                    id("("),
                    ExpList.expList(listOf(num(1), id(","),
                            num(2), id(","),
                            num(3))),
                    id(")"))
            (functionCallPostfix(l) as FunctionCallPostfix).params.size shouldBe 3
        }
    }

}