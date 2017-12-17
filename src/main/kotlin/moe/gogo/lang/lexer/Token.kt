package moe.gogo.lang.lexer

import java.lang.UnsupportedOperationException


sealed class Token(val lineNumber: Int, val name: String) {

    open val intValue: Int
        get() = throw UnsupportedOperationException("不支持转换为数字")

    open val text: String
        get() = throw UnsupportedOperationException("不支持字符串值")

    open val isIdentifier: Boolean = false

    open val isNumber: Boolean = false

    open val isString: Boolean = false

    open val isComment: Boolean = false

    companion object {
        @JvmField
        val EOL = "\\n"
    }

    object EOF : Token(-1, "EOF") {
        override val text: String = "EOF"
    }

    override fun toString(): String = "<$text>"

    internal class NumToken(lineNo: Int, id: String, value: Int) : Token(lineNo, id) {
        override val intValue: Int = value
        override val isNumber: Boolean = true
        override val text: String = value.toString()
    }

    internal class IdToken(lineNo: Int, id: String, value: String) : Token(lineNo, id) {
        override val isIdentifier: Boolean = true
        override val text: String = value
    }

    internal class StrToken(lineNo: Int, id: String, value: String) : Token(lineNo, id) {
        override val isString: Boolean = true
        override val text: String = value
    }

    internal class CommentToken(lineNo: Int, id: String, value: String) : Token(lineNo, id) {
        override val isComment: Boolean = true
        override val text: String = value
    }
}
