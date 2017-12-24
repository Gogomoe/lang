package moe.gogo.lang

interface Environment {

    operator fun get(key: String): Any?

    operator fun set(key: String, value: Any?)

    operator fun contains(key: String): Boolean

    fun putNew(key: String, value: Any?)

    fun subEnv(): Environment

}