package moe.gogo.lang.ast

import moe.gogo.lang.Environment
import moe.gogo.lang.lexer.Token
import moe.gogo.lang.type.Value

class Identifier(override val token: Token) : ASTLeaf(token) {

    val id = token.text

    override fun eval(env: Environment): Value? = env[id]

    override fun toString(): String = "<Name $id>"

}