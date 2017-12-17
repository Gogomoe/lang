package moe.gogo.lang.ast

import moe.gogo.lang.lexer.Token

class Identifier(override val token: Token) : ASTLeaf(token) {

    val id = token.text

    override fun toString(): String = "<Name $id>"

}