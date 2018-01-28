package moe.gogo.lang.type

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

class LBooleanTest : StringSpec() {

    init {
        Type
        "toString"{
            val t = LBoolean.create(true)
            val f = LBoolean.create(false)

            t["toString"].call(t).string() shouldBe "true"
            f["toString"].call(f).string() shouldBe "false"
        }
        "equals"{
            val t = LBoolean.create(true)
            val t2 = LBoolean.create(true)
            val f = LBoolean.create(false)

            t["equals"].call(t, listOf(t2)).bool() shouldBe true
            t["equals"].call(t, listOf(f)).bool() shouldBe false
        }
        "bool"{
            val t = LBoolean.create(true)
            val f = LBoolean.create(false)

            t.bool() shouldBe true
            f.bool() shouldBe false

            LString.create("    ").bool() shouldBe false
            LString.create("not all blank").bool() shouldBe true

            (null as Value?).bool() shouldBe false
            Any.create().bool() shouldBe true
        }
    }

}