package moe.gogo.lang.type

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

class TypeTest : StringSpec() {

    init {
        "name and simple name"{
            val type = Type("lang.test.Type")
            type.name shouldBe "lang.test.Type"
            type.simpleName shouldBe "Type"
            type.packageName shouldBe "lang.test"
        }
        "suptype"{
            val sup = Type("Sup")
            val sub = Type("Sub", sup)

            sup.suptype shouldBe Any
            sub.suptype shouldBe sup

            sub.isSubtypeOf(sup) shouldBe true
            sub.isSuptypeOf(sup) shouldBe false

            sup.isSubtypeOf(sub) shouldBe false
            sup.isSuptypeOf(sub) shouldBe true
        }
        "extend fields"{
            val sup = object : Type("Sup") {
                init {
                    types["a"] = String
                    types["b"] = Int
                    values["a"] = "hello".wrap()
                    values["b"] = 1.wrap()
                }
            }
            val sub = object : Type("Sub", sup) {
                init {
                    values["b"] = 2.wrap()
                }
            }
            ("a" in sub) shouldBe true
            ("b" in sub) shouldBe true
            sub.getValue("a").string() shouldBe "hello"
            sub.getValue("b").int() shouldBe 2
        }
        "create object"{
            val sup = object : Type("Sup") {
                init {
                    types["a"] = String
                    types["b"] = Int
                    values["a"] = "hello".wrap()
                    values["b"] = 1.wrap()
                }
            }

            val obj = sup.create(mapOf(
                    "a" to "test".wrap()
            ))

            obj["a"].string() shouldBe "test"
            obj["b"].int() shouldBe 1
        }
    }

}