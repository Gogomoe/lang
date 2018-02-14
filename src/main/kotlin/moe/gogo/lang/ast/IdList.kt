package moe.gogo.lang.ast

import moe.gogo.lang.Environment
import moe.gogo.lang.type.Value


class IdList(val ids: List<Identifier>) : ASTList(ids) {

    override fun eval(env: Environment): Value? = Value.Unit

    override fun toString(): String {
        return ids.map { it.id }.joinToString(", ")
    }


    companion object {
        fun idList(list: List<ASTree>): IdList =
                IdList(list.filterIndexed { index, asTree -> index % 2 == 0 } as List<Identifier>)
    }

}