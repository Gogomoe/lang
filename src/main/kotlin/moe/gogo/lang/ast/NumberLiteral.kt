package moe.gogo.lang.ast

import moe.gogo.lang.Environment
import moe.gogo.lang.lexer.Token

class NumberLiteral(override val token: Token) : ASTLeaf(token) {

    val number: Int = token.intValue

    override fun eval(env: Environment): Any? = number

    override fun toString(): String = "<Number $number>"

}