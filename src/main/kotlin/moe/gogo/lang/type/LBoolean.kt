package moe.gogo.lang.type

object LBoolean : Type("lang.Boolean") {

    override fun init() {
        values["toString"] = Type.Function {
            (this as NativeBoolean?)?.boolean.toString().wrap()
        }
        values["equals"] = Type.Function { list ->
            ((this as NativeBoolean?)?.boolean == (list[0] as NativeBoolean?)?.boolean).wrap()
        }
    }

    fun create(boolean: Boolean): Value {
        return NativeBoolean(boolean)
    }

}

class NativeBoolean(val boolean: Boolean) : Value(LBoolean)

fun Value?.bool() = when (this) {
    is NativeBoolean -> boolean
    is NativeString -> string.isNotBlank()
    is NativeDouble, is NativeInt -> this.num().toDouble() != 0.0
    else -> this != null
}