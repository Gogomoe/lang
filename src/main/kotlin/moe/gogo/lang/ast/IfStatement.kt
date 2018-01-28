package moe.gogo.lang.ast

import moe.gogo.lang.Environment
import moe.gogo.lang.type.Value
import moe.gogo.lang.type.bool


class IfStatement internal constructor(internal val condition: ASTree,
                                       internal val accept: ASTree,
                                       internal val elses: List<ElseIfStatement> = emptyList(),
                                       internal val reject: ASTree? = null)
    : ASTList(listOfNotNull(condition, accept, *elses.toTypedArray(), reject)) {

    override fun eval(env: Environment): Value? {

        fun ASTree.accepted(): Boolean = this.eval(env).bool()

        if (condition.accepted()) {
            return accept.eval(env)
        }
        elses.forEach {
            if (it.condition.accepted()) {
                return it.accept.eval(env)
            }
        }
        return reject?.eval(env)
    }

    override fun toString(): String {
        val elseIfs = StringBuilder()
        elses.forEach { it ->
            elseIfs.append("else if (${it.condition}) { ${it.accept} }\n")
        }
        val elses = if (reject != null) "else { $reject }" else ""
        return "if ($condition) { $accept }\n$elseIfs$elses"
    }

    /**
     * 构建 If 树时生成的 ElseIfs 树
     */
    internal class ElseIfStatement(val condition: ASTree,
                                   val accept: ASTree,
                                   val elses: List<ElseIfStatement> = emptyList(),
                                   val reject: ASTree? = null)
        : ASTList(listOfNotNull(condition, accept, *elses.toTypedArray(), reject))

    internal class ElseStatement(private val block: ASTree) : ASTList(listOf(block)) {

        override fun eval(env: Environment): Value? = block.eval(env)

        override fun toString(): String {
            return block.toString()
        }

    }

    companion object {
        fun ifBuilder(list: List<ASTree>): ASTree {
            // If -> if ( Exp ) ExpWithoutIfOrBlock ElseIfOrElse

            val exp = list[2]
            val blk = list[4]

            // 没有 ElseIf 和 Else 的情况直接构建
            val elses = list.getOrNull(5) ?: return IfStatement(exp, blk)

            return if (elses is ElseIfStatement) {
                IfStatement(exp, blk, listOf(*elses.elses.toTypedArray(), elses), elses.reject)
            } else {
                // 没有 ElseIf 只有 Else
                IfStatement(exp, blk, emptyList(), elses)
            }

        }

        fun elseIfOrElseBuilder(list: List<ASTree>): ASTree {
            // ElseIfOrElse -> ε
            // ElseIfOrElse -> else SubIfOrElseBlock

            val i = list[0]
            return if (i is Identifier && i.id == "else") {
                list[1]
            } else {
                ASTList(emptyList())
            }
        }

        fun subIfOrElseBlockBuilder(list: List<ASTree>): ASTree {
            // SubIfOrElseBlock -> ExpWithoutIfOrBlock
            // SubIfOrElseBlock -> if ( Exp ) ExpWithoutIfOrBlock ElseIfOrElse

            val i = list[0]

            // 是 Else 语句的情况
            val isElse = i !is Identifier || i.id != "if"
            if (isElse) {
                return ElseStatement(list[0])
            }
            val exp = list[2]
            val blk = list[4]
            // 当前语句是 ElseIf 且后面没有 ElseIf 或 Else
            val elses = list.getOrNull(5) ?: return ElseIfStatement(exp, blk)

            return if (elses is ElseIfStatement) {
                ElseIfStatement(exp, blk, listOf(*elses.elses.toTypedArray(), elses), elses.reject)
            } else {
                // 后一个语句是 Else
                ElseIfStatement(exp, blk, emptyList(), elses)
            }
        }

    }

}