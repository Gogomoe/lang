package moe.gogo.lang.ast

import moe.gogo.lang.Environment
import moe.gogo.lang.lexer.Token
import moe.gogo.lang.type.Value
import moe.gogo.lang.type.wrap

class NumberLiteral(override val token: Token) : ASTLeaf(token) {

    private val number: Number = token.numberValue

    override fun eval(env: Environment): Value? = number.wrap()

    override fun toString(): String = "<Number $number>"

}