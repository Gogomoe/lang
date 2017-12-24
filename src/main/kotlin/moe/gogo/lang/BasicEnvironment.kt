package moe.gogo.lang

class BasicEnvironment(private val chain: Environment? = null) : Environment {

    private val values = mutableMapOf<String, Any?>()

    override fun get(key: String): Any? =
            when {
                values.containsKey(key) -> values[key]
                chain != null -> chain[key]
                else -> throw LangRuntimeException("变量 $key 不存在")
            }

    override fun set(key: String, value: Any?) =
            when {
                values.containsKey(key) -> values[key] = value
                chain != null -> chain.set(key, value)
                else -> throw LangRuntimeException("变量 $key 不存在")
            }

    override fun contains(key: String): Boolean = values.contains(key) || chain?.contains(key) ?: false

    override fun putNew(key: String, value: Any?) =
            when {
                values.containsKey(key) -> throw LangRuntimeException("变量 $key 已存在")
                else -> values[key] = value
            }

    override fun subEnv(): Environment = BasicEnvironment(this)


}