package moe.gogo.lang

import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldThrow
import io.kotlintest.specs.StringSpec
import moe.gogo.lang.type.int
import moe.gogo.lang.type.wrap

class BasicEnvironmentTest : StringSpec() {

    init {
        "put and get var"{
            val e = BasicEnvironment()
            "put new var"{
                e.putNew("a", 10.wrap())
                e["a"].int() shouldBe 10
            }
            "change var"{
                e["a"] = 20.wrap()
                e["a"].int() shouldBe 20
            }
            "set undefined var"{
                shouldThrow<LangRuntimeException> {
                    e["b"] = 30.wrap()
                    Unit
                }
            }
            "put new defined var"{
                shouldThrow<LangRuntimeException> {
                    e.putNew("a", 30.wrap())
                }
            }
        }
        "var on chain"{
            val f = BasicEnvironment()
            val s = f.subEnv()

            f.putNew("a", 10.wrap())

            f.putNew("b", 10.wrap())
            s.putNew("b", 20.wrap())

            "get var on chain"{
                s["a"].int() shouldBe 10
                s["b"].int() shouldBe 20
            }

            "set var on chain"{
                s["a"] = 15.wrap()
                s["a"].int() shouldBe 15
                f["a"].int() shouldBe 15

                s["b"] = 25.wrap()
                s["b"].int() shouldBe 25
                f["b"].int() shouldBe 10
            }
        }
    }

}