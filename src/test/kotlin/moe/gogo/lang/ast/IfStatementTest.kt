package moe.gogo.lang.ast

import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldThrowAny
import io.kotlintest.specs.StringSpec
import moe.gogo.lang.ast.IfStatement.Companion.elseIfOrElseBuilder
import moe.gogo.lang.ast.IfStatement.Companion.ifBuilder
import moe.gogo.lang.ast.IfStatement.Companion.subIfOrElseBlockBuilder
import moe.gogo.lang.ast.IfStatement.ElseIfStatement
import moe.gogo.lang.eval
import moe.gogo.lang.id
import moe.gogo.lang.num
import moe.gogo.lang.type.bool
import moe.gogo.lang.type.int

class IfStatementTest : StringSpec() {

    init {
        "if without else if or else"{
            val tree = ifBuilder(listOf(
                    id("if"), id("("), num(0), id(")"),
                    num(99)
            ))
            tree as IfStatement
            tree.elses.size shouldBe 0
            tree.reject shouldBe null
            tree.condition.eval().bool() shouldBe false
            tree.accept.eval().int() shouldBe 99
        }
        "elseIfOrElseBuilder"{
            shouldThrowAny {
                elseIfOrElseBuilder(emptyList()).eval()
            }
            elseIfOrElseBuilder(listOf(id("else"), num(5))).eval().int() shouldBe 5
        }
        "else block"{
            subIfOrElseBlockBuilder(listOf(num(7))).eval().int() shouldBe 7
        }
        "else if without else or else if"{
            val tree = subIfOrElseBlockBuilder(listOf(
                    id("if"), id("("), num(0), id(")"),
                    num(10)
            ))
            tree as ElseIfStatement
            tree.elses.size shouldBe 0
            tree.reject shouldBe null
        }
        "else if with else"{
            val tree = subIfOrElseBlockBuilder(listOf(
                    id("if"), id("("), num(0), id(")"),
                    num(10), num(15)
            ))
            tree as ElseIfStatement
            tree.elses.size shouldBe 0
            tree.reject!!.eval().int() shouldBe 15
        }
        "else if with else if"{
            val subElseIf1 = subIfOrElseBlockBuilder(listOf(
                    id("if"), id("("), num(0), id(")"),
                    num(10), num(1001)
            ))
            val subElseIf2 = subIfOrElseBlockBuilder(listOf(
                    id("if"), id("("), num(0), id(")"),
                    num(10), subElseIf1
            ))
            val tree = subIfOrElseBlockBuilder(listOf(
                    id("if"), id("("), num(0), id(")"),
                    num(10), subElseIf2
            ))
            tree as ElseIfStatement
            tree.elses.size shouldBe 2
            tree.reject!!.eval().int() shouldBe 1001
        }
    }

}