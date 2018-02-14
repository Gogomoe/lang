package moe.gogo.lang.ast

import io.kotlintest.matchers.beEmpty
import io.kotlintest.matchers.should
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec
import moe.gogo.lang.ast.IdList.Companion.idList
import moe.gogo.lang.id

class IdListTest : StringSpec() {

    init {
        "create"{
            idList(listOf()).ids should beEmpty()
            idList(listOf(id("name"))).ids.first().id shouldBe "name"
            idList(listOf(id("a"), id(","),
                    id("b"), id(","),
                    id("c"))).ids.map { it.id } shouldBe listOf("a", "b", "c")

        }
    }

}