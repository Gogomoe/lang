package moe.gogo.lang.ast

import moe.gogo.lang.lexer.Token

class NumberLiteral(override val token: Token) : ASTLeaf(token) {

    val number: Int = token.intValue

    override fun toString(): String = "<Number $number>"

}