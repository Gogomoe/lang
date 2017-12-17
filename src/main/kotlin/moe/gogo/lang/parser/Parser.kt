package moe.gogo.lang.parser

import moe.gogo.lang.ast.ASTList
import moe.gogo.lang.ast.ASTree
import moe.gogo.lang.lexer.Lexer

abstract class Parser {

    open fun parse(lexer: Lexer): ASTree {
        val list = parseList(lexer)
        return if (list.size == 1) {
            list.first()
        } else {
            ASTList(list)
        }
    }

    abstract fun parseList(lexer: Lexer): List<ASTree>
}