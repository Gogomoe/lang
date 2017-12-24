package moe.gogo.lang

import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldThrow
import io.kotlintest.specs.StringSpec

class BasicEnvironmentTest : StringSpec() {

    init {
        "put and get var"{
            val e = BasicEnvironment()
            "put new var"{
                e.putNew("a", 10)
                e["a"] shouldBe 10
            }
            "change var"{
                e["a"] = 20
                e["a"] shouldBe 20
            }
            "set undefined var"{
                shouldThrow<LangRuntimeException> {
                    e["b"] = 30
                    Unit
                }
            }
            "put new defined var"{
                shouldThrow<LangRuntimeException> {
                    e.putNew("a", 30)
                }
            }
        }
        "var on chain"{
            val f = BasicEnvironment()
            val s = f.subEnv()

            f.putNew("a", 10)

            f.putNew("b", 10)
            s.putNew("b", 20)

            "get var on chain"{
                s["a"] shouldBe 10
                s["b"] shouldBe 20
            }

            "set var on chain"{
                s["a"] = 15
                s["a"] shouldBe 15
                f["a"] shouldBe 15

                s["b"] = 25
                s["b"] shouldBe 25
                f["b"] shouldBe 10
            }
        }
    }

}