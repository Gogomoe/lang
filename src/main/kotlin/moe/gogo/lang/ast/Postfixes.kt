package moe.gogo.lang.ast


class Postfixes(list: List<ASTree>) : ASTList(list) {

    internal val postfixes = list

    override fun toString(): String {
        return postfixes.joinToString("")
    }

    companion object {
        fun postfixes(list: List<ASTree>): ASTree {
            if (list.isEmpty()) {
                return Postfixes(emptyList())
            }
            if (list.size == 1) {
                return Postfixes(list)
            }
            val post = list[1] as Postfixes
            return Postfixes(listOf(list[0], *post.postfixes.toTypedArray()))
        }
    }

}