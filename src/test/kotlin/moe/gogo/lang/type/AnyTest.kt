package moe.gogo.lang.type

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

class AnyTest : StringSpec() {

    init {
        Type
        "toString"{
            val obj = Any.create()
            obj["toString"].call(obj).string().startsWith("lang.Any") shouldBe true
        }
        "equals"{
            val o1 = Any.create()
            val o2 = Any.create()
            val nil: Value? = null

            o1["equals"].call(o1, listOf(o1)).bool() shouldBe true
            o1["equals"].call(o1, listOf(o2)).bool() shouldBe false
            o1["equals"].call(o1, listOf(nil)).bool() shouldBe false
        }
        "suptype"{
            Any.suptype shouldBe Any
        }
    }

}