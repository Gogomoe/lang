package moe.gogo.lang.ast

import moe.gogo.lang.Environment
import moe.gogo.lang.type.Type
import moe.gogo.lang.type.Value

class DeclareFunction(list: List<ASTree>) : ASTList(list) {

    private val name = list[1] as Identifier
    private val idList = list[3] as IdList
    private val stmnts = list[6]

    override fun eval(env: Environment): Value? {
        val func = Type.Function { args ->
            val sub = env.subEnv()
            idList.ids.forEachIndexed { index, idTree ->
                sub.putNew(idTree.id, args[index])
            }
            stmnts.eval(sub)
        }
        env.putNew(name.id, func)
        return func
    }

    override fun toString(): String = "function ${name.id}($idList) {$stmnts}"
}