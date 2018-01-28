package moe.gogo.lang.type

typealias FunctionType = Value?.(List<Value?>) -> Value?

object LFunction : Type("lang.Function") {

    fun create(func: FunctionType): Value {
        return NativeFunction(func)
    }

    operator fun invoke(func: FunctionType): Value = create(func)
}


abstract class NativeFunction : Value(LFunction) {

    open fun call(self: Value? = null, params: List<Value?>): Value? = null

    companion object {
        operator fun invoke(func: FunctionType): NativeFunction {
            return object : NativeFunction() {
                override fun call(self: Value?, params: List<Value?>): Value? =
                        BoundFunction(self, func).call(self, params)
            }
        }
    }

}

class BoundFunction(val self: Value?, val func: FunctionType) : NativeFunction() {

    override fun call(self: Value?, params: List<Value?>): Value? = self.func(params)

}

fun Value?.call(self: Value? = null, params: List<Value?> = emptyList()): Value? =
        (this as NativeFunction).call(self, params)
