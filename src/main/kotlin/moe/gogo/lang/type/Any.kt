package moe.gogo.lang.type

object Any : Type("lang.Any", null) {

    override val suptype: Type = Any

    override fun init() {
        types["toString"] = Type.Function
        values["toString"] = Type.Function {
            if (this == null) {
                "null".wrap()
            } else {
                "${this.type.name}@${this.hashCode()}".wrap()
            }
        }
        types["equals"] = Type.Function
        values["equals"] = Type.Function { list ->
            Type.Boolean.create(this == list[0])
        }
    }

    override fun getValue(key: String): Value? = values[key]

    override operator fun contains(key: String): Boolean = types.containsKey(key)

    override fun isSubtypeOf(type: Type): Boolean = type == Any

}