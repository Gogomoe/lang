package moe.gogo.lang.ast

import moe.gogo.lang.Environment
import moe.gogo.lang.lexer.Token
import moe.gogo.lang.type.Value
import moe.gogo.lang.type.wrap

class StringLiteral(t: Token) : ASTLeaf(t) {

    val string: String = token.text

    override fun eval(env: Environment): Value? = string.wrap()

    override fun toString(): String = "<String $string>"

}