package moe.gogo.lang.type

object LUnit : Type("lang.Unit", Any) {

    override fun init() {
        values["toString"] = Type.Function { "Unit".wrap() }
    }

}