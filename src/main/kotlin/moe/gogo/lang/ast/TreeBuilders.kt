package moe.gogo.lang.ast

import moe.gogo.lang.LangRuntimeException
import moe.gogo.lang.ast.IfStatement.ElseIfStatement
import moe.gogo.lang.ast.IfStatement.ElseStatement
import moe.gogo.lang.lexer.Lexer
import moe.gogo.lang.parser.Parser
import moe.gogo.lang.parser.ParserRegister
import java.util.ArrayDeque

fun factor(list: List<ASTree>): ASTree {
    var orgin = when {
        list[0].isId("(") -> list[1]
        list[0].isId("-") -> list[1]
        list[0].isId("!") -> list[1]
        else -> list[0]
    }
    val postfixes = when {
        list[0].isId("(") -> list.getOrNull(3)
        list[0].isId("-") -> list.getOrNull(2)
        list[0].isId("!") -> list.getOrNull(2)
        else -> list.getOrNull(1)
    }
    if (postfixes != null) {
        orgin = buildPostfix(orgin, postfixes as Postfixes)
    }
    return when {
        list[0].isId("-") -> UnaryMinus(listOf(list[0], orgin))
        list[0].isId("!") -> UnaryNot(listOf(list[0], orgin))
        else -> orgin
    }
}

fun buildPostfix(orgin: ASTree, postfixes: Postfixes): ASTree {
    val list = ArrayDeque(postfixes.postfixes)
    list.addFirst(orgin)
    return list.reduce { acc, asTree ->
        when (asTree) {
            is FunctionCallPostfix -> FunctionCall(acc, asTree.params)
            else -> throw LangRuntimeException("${asTree::class.simpleName} 不支持")
        }
    }
}

val postfixesParser: ParserRegister.(Lexer) -> Parser = { lexer ->
    if (lexer.peek().name == "(") {
        this.findDefaultParser("Postfixes -> Postfix Postfixes")!!
    } else {
        this.findDefaultParser("Postfixes -> ε")!!
    }

}

fun expOrBlock(list: List<ASTree>): ASTree = when {
    list[0].isId("{") -> list[1]
    else -> list[0]
}

fun ifExpBuilder(list: List<ASTree>): ASTree {
    val con = list[2]
    val accept = list[4]
    val elses = list[6]
    return if (elses is ElseStatement) {
        IfStatement(con, accept, emptyList(), elses)
    } else {
        elses as ElseIfStatement
        IfStatement(con, accept, listOf(*elses.elses.toTypedArray(), elses), elses.reject)
    }
}

fun ifExpElseIfOrElse(list: List<ASTree>): ASTree = when {
    list[0].isId("if") -> {
        val con = list[2]
        val accept = list[4]
        val elses = list[6]
        if (elses is ElseStatement) {
            ElseIfStatement(con, accept, emptyList(), elses)
        } else {
            elses as ElseIfStatement
            ElseIfStatement(con, accept, listOf(*elses.elses.toTypedArray(), elses), elses.reject)
        }
    }
    else -> ElseStatement(list[0])
}

private fun ASTree.isId(id: String) = this is Identifier && this.id == id
