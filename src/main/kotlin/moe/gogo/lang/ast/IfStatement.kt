package moe.gogo.lang.ast

import moe.gogo.lang.Environment
import moe.gogo.lang.toBool


class IfStatement(private val condition: ASTree,
                  private val accept: ASTree,
                  private val elses: List<ElseIfStatement> = emptyList(),
                  private val reject: ASTree? = null)
    : ASTList(listOfNotNull(condition, accept, *elses.toTypedArray(), reject)) {

    override fun eval(env: Environment): Any? {

        fun ASTree.accepted(): Boolean = this.eval(env).toBool()

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
        val elses = if (reject != null) "else" + reject else ""
        return "if ($condition) { $accept }\n$elseIfs$elses"
    }

    class ElseIfStatement(val condition: ASTree,
                          val accept: ASTree,
                          val elses: List<ElseIfStatement> = emptyList(),
                          val reject: ASTree? = null)
        : ASTList(listOfNotNull(condition, accept, *elses.toTypedArray(), reject)) {

        override fun eval(env: Environment): Any? = condition.eval(env)
    }

    class ElseStatement(private val block: ASTree) : ASTList(listOf(block)) {

        override fun eval(env: Environment): Any? = block.eval(env)

        override fun toString(): String {
            return block.toString()
        }

    }

    companion object {
        fun ifBuilder(list: List<ASTree>): ASTree {
            val exp = list[2]
            val blk = list[4]
            val elses = list.getOrNull(5)
            return if (elses != null) {
                if (elses is ElseIfStatement) {
                    IfStatement(exp, blk, listOf(*elses.elses.toTypedArray(), elses), elses.reject)
                } else {
                    IfStatement(exp, blk, emptyList(), elses)
                }
            } else {
                IfStatement(exp, blk)
            }
        }

        fun elseIfOrElseBuilder(list: List<ASTree>): ASTree {
            val i = list[0]
            return if (i is Identifier && i.id == "else") {
                list[1]
            } else {
                ASTList(emptyList())
            }
        }

        fun subIfOrElseBlockBuilder(list: List<ASTree>): ASTree {
            val i = list[0]
            return if (i is Identifier && i.id == "if") {
                val exp = list[2]
                val blk = list[4]
                val elses = list.getOrNull(5)
                if (elses != null) {
                    if (elses is ElseIfStatement) {
                        ElseIfStatement(exp, blk, listOf(*elses.elses.toTypedArray(), elses), elses.reject)
                    } else {
                        ElseIfStatement(exp, blk, emptyList(), elses)
                    }
                } else {
                    ElseIfStatement(exp, blk)
                }
            } else {
                ElseStatement(list[0])
            }
        }

    }

}