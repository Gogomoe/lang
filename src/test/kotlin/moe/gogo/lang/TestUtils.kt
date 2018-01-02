package moe.gogo.lang

import moe.gogo.lang.ast.ASTree
import moe.gogo.lang.ast.Identifier
import moe.gogo.lang.ast.NumberLiteral
import moe.gogo.lang.lexer.Token.IdToken
import moe.gogo.lang.lexer.Token.NumToken

fun env(): Environment = BasicEnvironment()

fun num(value: Int) = NumberLiteral(NumToken(0, "number", value))
fun id(value: String) = Identifier(IdToken(0, "id", value))

fun ASTree.eval() = this.eval(env())