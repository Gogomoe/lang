package moe.gogo.lang.type

import moe.gogo.lang.LangRuntimeException
import moe.gogo.lang.type.Type.Companion

open class Value(val type: Type) {

    private val values: MutableMap<String, Value> = mutableMapOf()

    open operator fun get(key: String): Value? = safeCheck(key) {
        values[key] ?: type.getValue(key)
    }

    open operator fun set(key: String, value: Value): Value? = safeCheck(key) {
        if (!value.type.isSubtypeOf(key.type())) {
            throw LangRuntimeException("类型不匹配 需要${key.type()} 然而是${value.type}")
        }
        values[key] = value
        value
    }

    private inline fun <T> safeCheck(key: String, func: () -> T): T {
        if (key !in type) {
            throw LangRuntimeException("$type has't field named $key")
        }
        return func()
    }

    private fun String.type() = type[this]

    companion object {
        val Unit = object : Value(Type.Unit) {}
    }

}

infix fun Value?.isa(type: Type): Boolean = this?.type?.isSubtypeOf(type) ?: false
infix fun Value?.isan(type: Type): Boolean = this?.type?.isSubtypeOf(type) ?: true
infix fun Value?.nota(type: Type): Boolean = !(this isa type)
infix fun Value?.notan(type: Type): Boolean = !(this isan type)

infix fun Value?.equal(value: Value?): Boolean =
        if (this == null) value == null
        else this["equals"].call(this, listOf(value)).bool()

@Suppress("UNCHECKED_CAST")
fun kotlin.Any.wrap(): Value = when (this) {
    is Number -> Type.Number.create(this)
    is String -> Type.String.create(this)
    is Boolean -> Type.Boolean.create(this)
    is Function<*> -> Type.Function.create(this as FunctionType)
    else -> throw LangRuntimeException("")
}
