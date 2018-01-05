package moe.gogo.lang.ast

class FunctionCallPostfix(internal val params: List<ASTree>) : ASTList(params) {

    companion object {
        fun functionCallPostfix(list: List<ASTree>): ASTree {
            return FunctionCallPostfix((list[1] as ExpList).exps)
        }
    }

}