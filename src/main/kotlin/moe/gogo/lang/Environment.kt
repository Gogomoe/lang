package moe.gogo.lang

import moe.gogo.lang.type.Value

interface Environment {

    operator fun get(key: String): Value?

    operator fun set(key: String, value: Value?)

    operator fun contains(key: String): Boolean

    fun putNew(key: String, value: Value?)

    fun subEnv(): Environment

}