package moe.gogo.lang.ast

import moe.gogo.lang.ast.IfStatement.ElseIfStatement
import moe.gogo.lang.ast.IfStatement.ElseStatement

fun factor(list: List<ASTree>): ASTree = when {
    list[0] is Identifier && (list[0] as Identifier).id == "(" -> list[1]
    else -> list[0]
}

fun expOrBlock(list: List<ASTree>): ASTree = when {
    list[0] is Identifier && (list[0] as Identifier).id == "{" -> list[1]
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
    list[0] is Identifier && (list[0] as Identifier).id == "if" -> {
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