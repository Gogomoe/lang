package moe.gogo.lang

import moe.gogo.lang.ast.DeclareStmnt
import moe.gogo.lang.ast.ExpList.Companion.expList
import moe.gogo.lang.ast.FunctionCallPostfix.Companion.functionCallPostfix
import moe.gogo.lang.ast.IfStatement.Companion.elseIfOrElseBuilder
import moe.gogo.lang.ast.IfStatement.Companion.ifBuilder
import moe.gogo.lang.ast.IfStatement.Companion.subIfOrElseBlockBuilder
import moe.gogo.lang.ast.NumberLiteral
import moe.gogo.lang.ast.Postfixes.Companion.postfixes
import moe.gogo.lang.ast.Statement
import moe.gogo.lang.ast.Statements.Companion.statements
import moe.gogo.lang.ast.StringLiteral
import moe.gogo.lang.ast.expOrBlock
import moe.gogo.lang.ast.factor
import moe.gogo.lang.ast.ifExpBuilder
import moe.gogo.lang.ast.ifExpElseIfOrElse
import moe.gogo.lang.ast.postfixesParser
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
import moe.gogo.lang.type.LFunction
import moe.gogo.lang.type.Type
import moe.gogo.lang.type.Type.Companion
import moe.gogo.lang.type.Value
import moe.gogo.lang.type.int
import moe.gogo.lang.type.string
import moe.gogo.lang.type.wrap
import kotlin.reflect.jvm.internal.ReflectProperties.Val

fun createLexicon(): Lexicon {
    val lexicon = Lexicon()
    lexicon.defineString("var")
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
    lexicon.defineArgument("id", Regex("""[\p{Lu}\p{Ll}\p{Lt}\p{Lm}\p{Lo}\p{Nl}][\p{Lu}\p{Ll}\p{Lt}\p{Lm}\p{Lo}\p{Nl}\p{Mn}\p{Mc}\p{Nd}\p{Pc}\p{Cf}]*"""), IdToken::class)

    return lexicon
}


private fun builderParsers(): ParserRegister {
    val productions = buildProductions()
    val register = ParserRegister(productions)
    register.defineBuilder("number", ::NumberLiteral)
    register.defineBuilder("string", ::StringLiteral)

    register.defineBuilder("Factor", ::factor)
    register.defineBuilder("FactorWithoutIf", ::factor)

    val expr = buildExprParser(register, productions)
    register.register("Exp", expr)
    val exprWithoutIf = buildExprParser(register, productions, "FactorWithoutIf")
    register.register("ExpWithoutIf", exprWithoutIf)

    register.defineBuilder("Postfixes", ::postfixes)
    register.defineBuilder("FunctionCall", ::functionCallPostfix)

    register.register("Postfixes", postfixesParser)

    register.defineBuilder("ExpList", ::expList)

    register.defineBuilder("Declare", ::DeclareStmnt)

    register.defineBuilder("If", ::ifBuilder)
    register.defineBuilder("SubIfOrElseBlock", ::subIfOrElseBlockBuilder)
    register.defineBuilder("ElseIfOrElse", ::elseIfOrElseBuilder)
    register.defineBuilder("IfExp", ::ifExpBuilder)
    register.defineBuilder("IfExp_ElseIfOrElse", ::ifExpElseIfOrElse)

    register.defineBuilder("ExpWithoutIfOrBlock", ::expOrBlock)

    register.defineBuilder("Statement", ::Statement)
    register.defineBuilder("Statements", ::statements)


    return register
}

fun buildExprParser(register: ParserRegister, productions: ProductionRegister, factorName: String = "Factor"): Parser =
        ExpressionParser(register, productions, factorName).also {
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

    /**
     * Expression
     */

    register.register("Exp -> Factor")
    register.register("ExpWithoutIf -> FactorWithoutIf")

    register.register("Factor -> Primary Postfixes")
    register.register("Factor -> - Primary Postfixes")
    register.register("Factor -> ! Primary Postfixes")
    register.register("Factor -> ( Exp ) Postfixes")

    register.register("FactorWithoutIf -> PrimaryWithoutIf Postfixes")
    register.register("FactorWithoutIf -> - PrimaryWithoutIf Postfixes")
    register.register("FactorWithoutIf -> ! PrimaryWithoutIf Postfixes")
    register.register("FactorWithoutIf -> ( Exp ) Postfixes")

    register.register("PrimaryWithoutIf -> number")
    register.register("PrimaryWithoutIf -> string")
    register.register("PrimaryWithoutIf -> id")

    register.register("Primary -> PrimaryWithoutIf")
    register.register("Primary -> IfExp")

    /**
     * Postfix
     */

    register.register("Postfixes -> ε")
    register.register("Postfixes -> Postfix Postfixes")

    register.register("Postfix -> FunctionCall")

    /**
     * LFunction
     */

    register.register("FunctionCall -> ( ExpList )")

    /**
     * ExpList
     */

    register.register("ExpList -> ε")
    register.register("ExpList -> Exp ExpListTail")
    register.register("ExpListTail -> ε")
    register.register("ExpListTail -> , Exp ExpListTail")

    /**
     * Declare and Assign
     */

    register.register("Declare -> var id AssignOrNull")
    register.register("AssignOrNull -> ε")
    register.register("AssignOrNull -> = Exp")

    /**
     * If
     */

    register.register("IfExp -> if ( Exp ) ExpWithoutIfOrBlock else IfExp_ElseIfOrElse")
    register.register("IfExp_ElseIfOrElse -> ExpWithoutIfOrBlock")
    register.register("IfExp_ElseIfOrElse -> if ( Exp ) ExpWithoutIfOrBlock else IfExp_ElseIfOrElse")

    register.register("If -> if ( Exp ) ExpWithoutIfOrBlock ElseIfOrElse")
    register.register("ElseIfOrElse -> ε")
    register.register("ElseIfOrElse -> else SubIfOrElseBlock")
    register.register("SubIfOrElseBlock -> ExpWithoutIfOrBlock")
    register.register("SubIfOrElseBlock -> if ( Exp ) ExpWithoutIfOrBlock ElseIfOrElse")

    /**
     * Blocks
     */

    register.register("ExpWithoutIfOrBlock -> ExpWithoutIf")
    register.register("ExpWithoutIfOrBlock -> { Statements }")

    /**
     * Statements
     */

    register.register("Statements -> Statement Statements")
    register.register("Statements -> ε")

    register.register("Statement -> Declare")
    register.register("Statement -> Exp")

    return register
}


fun main(args: Array<String>) {
    val parsers = builderParsers()
    val p = parsers.findParser("Statements")!!
    val lexer = Lexer("""
        |var a = 4
        |var b = 6
        |var c = 5
        |print(3 * - if(a > b && a > c) a else if(b > c) b else c)
        |print(abs(-9))
        |""".trimMargin().reader(), createLexicon())
    val tree = p.parse(lexer)
    val env = BasicEnvironment()
    env.putNew("abs", Type.Function {
        Math.abs(it[0].int()).wrap()
    })
    env.putNew("print", Type.Function {
        println(it.joinToString(", ", transform = Value?::string))
        Value.Unit
    })
    println(tree)
    println("--------result--------")
    println(tree.eval(env).string())
}
