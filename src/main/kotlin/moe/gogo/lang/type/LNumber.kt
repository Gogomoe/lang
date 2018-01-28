package moe.gogo.lang.type

import moe.gogo.lang.LangRuntimeException

object LNumber : Type("lang.Number") {

    override fun init() {

        values["toString"] = Type.Function {
            "${this.num()}".wrap()
        }
        values["equals"] = Type.Function { list ->
            (if (list[0] isa LNumber)
                this.num().toDouble() == list[0].num().toDouble()
            else
                false).wrap()
        }

        types["plus"] = Type.Function
        values["plus"] = Type.Function { list ->
            @Suppress("IMPLICIT_CAST_TO_ANY")
            (if (list[0] isa LDouble || this isa LDouble)
                this.num().toDouble() + list[0].num().toDouble()
            else
                this.num().toInt() + list[0].num().toInt()).wrap()
        }

        types["minus"] = Type.Function
        values["minus"] = Type.Function { list ->
            @Suppress("IMPLICIT_CAST_TO_ANY")
            (if (list[0] isa LDouble || this isa LDouble)
                this.num().toDouble() - list[0].num().toDouble()
            else
                this.num().toInt() - list[0].num().toInt()).wrap()
        }

        types["times"] = Type.Function
        values["times"] = Type.Function { list ->
            @Suppress("IMPLICIT_CAST_TO_ANY")
            (if (list[0] isa LDouble || this isa LDouble)
                this.num().toDouble() * list[0].num().toDouble()
            else
                this.num().toInt() * list[0].num().toInt()).wrap()
        }

        types["div"] = Type.Function
        values["div"] = Type.Function { list ->
            @Suppress("IMPLICIT_CAST_TO_ANY")
            (if (list[0] isa LDouble || this isa LDouble)
                this.num().toDouble() / list[0].num().toDouble()
            else
                this.num().toInt() / list[0].num().toInt()).wrap()
        }

        types["mod"] = Type.Function
        values["mod"] = Type.Function { list ->
            @Suppress("IMPLICIT_CAST_TO_ANY")
            (if (list[0] isa LDouble || this isa LDouble)
                this.num().toDouble() % list[0].num().toDouble()
            else
                this.num().toInt() % list[0].num().toInt()).wrap()
        }

        types["compareTo"] = Type.Function
        values["compareTo"] = Type.Function { list ->
            val d = this.num().toDouble() - list[0].num().toDouble()
            when {
                d > 0 -> 1
                d == 0.0 -> 0
                else -> -1
            }.wrap()
        }
    }

    fun create(num: Number): Value = when (num) {
        is Int, is Long -> LInt.create(num.toInt())
        is Double, is Float -> LDouble.create(num.toDouble())
        else -> throw LangRuntimeException("数字类型错误")
    }

}

object LInt : Type("Int", LNumber) {

    fun create(num: Int): Value = NativeInt(num)

}

class NativeInt(val int: Int) : Value(LInt)

fun Value?.int() = (this as NativeInt).int

object LDouble : Type("Double", LNumber) {

    fun create(num: Double): Value = NativeDouble(num)

}

class NativeDouble(val double: Double) : Value(LDouble)

fun Value?.double() = (this as NativeDouble).double

fun Value?.num(): Number = when {
    this == null -> throw LangRuntimeException("数字为null")
    this isa LInt -> this.int()
    this isa LDouble -> this.double()
    else -> throw LangRuntimeException("数字类型错误")
}
