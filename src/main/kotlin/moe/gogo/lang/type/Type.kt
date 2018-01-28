package moe.gogo.lang.type

import moe.gogo.lang.LangRuntimeException

open class Type(val name: String, suptypeOrNull: Type? = Any) {

    val simpleName: String

    val packageName: String

    open val suptype: Type  by lazy { suptypeOrNull!! }

    init {
        val pos = name.lastIndexOf('.')
        if (pos == -1) {
            simpleName = name
            packageName = ""
        } else {
            simpleName = name.substring(pos + 1)
            packageName = name.substring(0, pos)
        }
    }

    open fun init() {}

    protected val types: MutableMap<String, Type> = mutableMapOf()

    protected val values: MutableMap<String, Value> = mutableMapOf()

    operator fun get(key: String): Type = types[key] ?: throw LangRuntimeException("$this has't field named $key")

    open fun getUnsafe(key: String): Type? = types[key]

    open fun getValue(key: String): Value? = values[key] ?: suptype.getValue(key)

    open operator fun contains(key: String): Boolean = types.containsKey(key) || key in suptype

    open fun isSubtypeOf(type: Type): Boolean = when (type) {
        this -> true
        Any -> true
        else -> suptype.isSubtypeOf(type)
    }

    open fun isSuptypeOf(type: Type): Boolean = type.isSubtypeOf(this)

    open fun create(params: Map<String, Value> = emptyMap()) = object : Value(this@Type) {
        init {
            params.forEach { k, v ->
                this[k] = v
            }
        }
    }

    companion object {
        val String = LString
        val Boolean = LBoolean
        val Int = LInt
        val Double = LDouble
        val Number = LNumber
        val Function = LFunction
        val Unit = LUnit

        init {
            Any.init()
            String.init()
            Boolean.init()
            Int.init()
            Double.init()
            Number.init()
            Function.init()
            Unit.init()
        }
    }

    override fun toString(): String = name

}
