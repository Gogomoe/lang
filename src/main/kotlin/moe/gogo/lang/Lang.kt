package moe.gogo.lang

import moe.gogo.lang.ast.IfStatement.Companion.elseIfOrElseBuilder
import moe.gogo.lang.ast.IfStatement.Companion.ifBuilder
import moe.gogo.lang.ast.IfStatement.Companion.subIfOrElseBlockBuilder
import moe.gogo.lang.ast.NumberLiteral
import moe.gogo.lang.ast.StringLiteral
import moe.gogo.lang.ast.expOrBlock
import moe.gogo.lang.ast.factor
import moe.gogo.lang.ast.ifExpBuilder
import moe.gogo.lang.ast.ifExpElseIfOrElse
import moe.gogo.lang.lexer.Lexer
import moe.gogo.lang.lexer.Lexicon
import moe.gogo.lang.lexer.Token.CommentToken
import moe.gogo.lang.lexer.Token.IdToken
import moe.gogo.lang.lexer.Token.NumToken
import moe.gogo.lang.lexer.Token.StrToken
import moe.gogo.lang.parser.ExpressionParser
import moe.gogo.lang.parser.ExpressionParser.Association.LEFT
import moe.gogo.lang.parser.ExpressionParser.Association.RIGHT
import moe.gogo.lang.parser.Parser
import moe.gogo.lang.parser.ParserRegister
import moe.gogo.lang.parser.ProductionRegister

fun createLexicon(): Lexicon {
    val lexicon = Lexicon()
    lexicon.defineString("if")
    lexicon.defineString("else")
    lexicon.defineString("for")

    lexicon.defineString("&&")
    lexicon.defineString("||")
    lexicon.defineString("!")
    lexicon.defineString("<=")
    lexicon.defineString("<")
    lexicon.defineString(">=")
    lexicon.defineString(">")
    lexicon.defineString("==")
    lexicon.defineString("=")
    lexicon.defineString("+")
    lexicon.defineString("-")
    lexicon.defineString("*")
    lexicon.defineString("/")
    lexicon.defineString("(")
    lexicon.defineString(")")
    lexicon.defineString("[")
    lexicon.defineString("]")
    lexicon.defineString("{")
    lexicon.defineString("}")
    lexicon.defineString(".")
    lexicon.defineString(",")
    lexicon.defineString(":")
    lexicon.defineString(";")

    lexicon.defineMultiWords("comment", Regex("""//"""), Regex("""\n"""), CommentToken::class)
    lexicon.defineMultiWords("comment", Regex("""/\*"""), Regex("""\*/"""), CommentToken::class)

    lexicon.defineArgument("number", Regex("""\d+"""), NumToken::class)
    lexicon.defineArgument("string", Regex("""".*?[^\\](\\\\)*""""), StrToken::class)
    lexicon.defineArgument("name", Regex("""[\p{Lu}\p{Ll}\p{Lt}\p{Lm}\p{Lo}\p{Nl}][\p{Lu}\p{Ll}\p{Lt}\p{Lm}\p{Lo}\p{Nl}\p{Mn}\p{Mc}\p{Nd}\p{Pc}\p{Cf}]*"""), IdToken::class)

    return lexicon
}


private fun builderParsers(): ParserRegister {
    val productions = buildProductions()
    val register = ParserRegister(productions)
    register.defineBuilder("number", ::NumberLiteral)
    register.defineBuilder("string", ::StringLiteral)

    register.defineBuilder("If", ::ifBuilder)
    register.defineBuilder("SubIfOrElseBlock", ::subIfOrElseBlockBuilder)
    register.defineBuilder("ElseIfOrElse", ::elseIfOrElseBuilder)
    register.defineBuilder("IfExp", ::ifExpBuilder)
    register.defineBuilder("IfExp_ElseIfOrElse", ::ifExpElseIfOrElse)

    register.defineBuilder("ExpOrBlock", ::expOrBlock)
    register.defineBuilder("Factor", ::factor)

    val expr = buildExprParser(register, productions)
    register.register("Exp", expr)
    return register
}

fun buildExprParser(register: ParserRegister, productions: ProductionRegister): Parser =
        ExpressionParser(register, productions).also {
            it.defineOperator("=", 1, RIGHT)
            it.defineOperator("+=", 2, RIGHT)
            it.defineOperator("-=", 2, RIGHT)
            it.defineOperator("*=", 2, RIGHT)
            it.defineOperator("/=", 2, RIGHT)
            it.defineOperator("%=", 2, RIGHT)
            it.defineOperator("&&", 3, LEFT)
            it.defineOperator("||", 3, LEFT)
            it.defineOperator("==", 4, LEFT)
            it.defineOperator("!=", 4, LEFT)
            it.defineOperator(">=", 4, LEFT)
            it.defineOperator("<=", 4, LEFT)
            it.defineOperator(">", 4, LEFT)
            it.defineOperator("<", 4, LEFT)
            it.defineOperator("+", 5, LEFT)
            it.defineOperator("-", 5, LEFT)
            it.defineOperator("%", 6, LEFT)
            it.defineOperator("*", 7, LEFT)
            it.defineOperator("/", 7, LEFT)
        }

private fun buildProductions(): ProductionRegister {
    val register = ProductionRegister()
    register.register("Exp -> Factor")

    register.register("Factor -> Primary")
    register.register("Factor -> - Primary")
    register.register("Factor -> ! Primary")
    register.register("Factor -> ( Exp )")

    register.register("Primary -> number")
    register.register("Primary -> string")
    register.register("Primary -> id")
    register.register("Primary -> IfExp")

    register.register("IfExp -> if ( Exp ) ExpOrBlock else IfExp_ElseIfOrElse")
    register.register("IfExp_ElseIfOrElse -> ExpOrBlock")
    register.register("IfExp_ElseIfOrElse -> if ( Exp ) ExpOrBlock else IfExp_ElseIfOrElse")

    register.register("If -> if ( Exp ) ExpOrBlock ElseIfOrElse")
    register.register("ElseIfOrElse -> ε")
    register.register("ElseIfOrElse -> else SubIfOrElseBlock")
    register.register("SubIfOrElseBlock -> ExpOrBlock")
    register.register("SubIfOrElseBlock -> if ( Exp ) ExpOrBlock ElseIfOrElse")

    register.register("ExpOrBlock -> Exp")
    register.register("ExpOrBlock -> { Statements }")

    register.register("Statements -> Statement Statements")
    register.register("Statements -> ε")
    register.register("Statement -> Exp")
    return register
}


fun main(args: Array<String>) {
    // TODO 修复解析表的Warning
    val parsers = builderParsers()
    val p = parsers.findParser("Exp")!!
    val lexer = Lexer("3 * if(a > b && a > c) a else if(b > c) b else c".trimMargin().reader(), createLexicon())
    val tree = p.parse(lexer)
    val env = BasicEnvironment()
    env.putNew("a", 4)
    env.putNew("b", 6)
    env.putNew("c", 5)
    println(tree.eval(env)) //TODO delete it
}


fun Any?.toBool() = when (this) {
    is Boolean -> this
    is Number -> this.toInt() == 0
    is String -> this.isNotBlank()
    else -> this != null
}