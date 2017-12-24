package moe.gogo.lang.ast

import moe.gogo.lang.Environment
import moe.gogo.lang.lexer.Token

class StringLiteral(t: Token) : ASTLeaf(t) {

    val string: String = token.text

    override fun eval(env: Environment): Any? = string

    override fun toString(): String = "<String $string>"

}