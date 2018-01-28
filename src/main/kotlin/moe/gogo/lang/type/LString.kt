package moe.gogo.lang.type

object LString : Type("lang.String") {

    override fun init() {
        values["toString"] = Type.Function { this }
        values["equals"] = Type.Function { list ->
            (list[0] != null && this.string() == list[0].string()).wrap()
        }
    }

    fun create(string: String): Value {
        return NativeString(string)
    }

}

class NativeString(val string: String) : Value(LString)

@Suppress("CAST_NEVER_SUCCEEDS")
fun Value?.string(): String = when (this) {
    null -> "null"
    is NativeString -> string
    else -> this["toString"].call(this).string()
}