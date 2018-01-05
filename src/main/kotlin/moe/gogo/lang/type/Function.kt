package moe.gogo.lang.type


abstract class Function {

    open fun call(params: List<Any?>): Any? = null

    operator fun invoke(params: List<Any?>): Any? = call(params)

    companion object {
        operator fun invoke(func: (List<Any?>) -> Any?): Function {
            return object : Function() {
                override fun call(params: List<Any?>) = func(params)
            }
        }
    }
}