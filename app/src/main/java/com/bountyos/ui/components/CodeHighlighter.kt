package com.bountyos.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.bountyos.domain.model.ScriptLanguage

/**
 * 轻量多语言代码语法高亮器。
 *
 * 针对 Ruby / Python / Bash / JavaScript 的通用子集做词法高亮：行注释、
 * 字符串、数字与关键字。逐字符扫描，避免字符串/注释内部被错误着色。
 * 不引入第三方高亮库，保持依赖最小。
 */
internal object CodeHighlighter {

    private val RUBY_KEYWORDS = setOf(
        "def", "end", "if", "elsif", "else", "unless", "while", "for", "do",
        "class", "module", "require", "include", "return", "puts", "print",
        "begin", "rescue", "ensure", "case", "when", "then", "yield", "self",
        "break", "next", "raise", "lambda", "proc", "attr_accessor",
        "and", "or", "not", "nil", "true", "false", "in", "new",
    )

    private val PYTHON_KEYWORDS = setOf(
        "def", "return", "if", "elif", "else", "for", "while", "import",
        "from", "class", "try", "except", "finally", "with", "as", "pass",
        "break", "continue", "lambda", "yield", "print", "and", "or", "not",
        "in", "is", "None", "True", "False", "global", "nonlocal", "raise",
        "del", "assert", "async", "await",
    )

    private val BASH_KEYWORDS = setOf(
        "if", "then", "else", "elif", "fi", "for", "while", "do", "done",
        "case", "esac", "function", "in", "echo", "exit", "return", "local",
        "export", "read", "set", "unset", "declare", "break", "continue",
        "source", "printf",
    )

    private val JS_KEYWORDS = setOf(
        "function", "return", "if", "else", "for", "while", "const", "let",
        "var", "class", "import", "export", "from", "new", "try", "catch",
        "finally", "throw", "switch", "case", "default", "break", "continue",
        "typeof", "instanceof", "in", "of", "this", "null", "undefined",
        "true", "false", "async", "await", "console",
    )

    private fun keywordsFor(language: ScriptLanguage): Set<String> = when (language) {
        ScriptLanguage.RUBY -> RUBY_KEYWORDS
        ScriptLanguage.PYTHON -> PYTHON_KEYWORDS
        ScriptLanguage.BASH -> BASH_KEYWORDS
        ScriptLanguage.JAVASCRIPT -> JS_KEYWORDS
    }

    fun highlight(
        code: String,
        language: ScriptLanguage,
        keyword: Color,
        string: Color,
        comment: Color,
        number: Color,
    ): AnnotatedString {
        val keywords = keywordsFor(language)
        return buildAnnotatedString {
            var i = 0
            while (i < code.length) {
                val c = code[i]
                when {
                    // 行注释：Ruby/Python/Bash 的 #，JS/C 的 //
                    c == '#' || (c == '/' && i + 1 < code.length && code[i + 1] == '/') -> {
                        val start = i
                        while (i < code.length && code[i] != '\n') i++
                        withStyle(SpanStyle(color = comment)) { append(code.substring(start, i)) }
                    }
                    // 字符串
                    c == '"' || c == '\'' || c == '`' -> {
                        val start = i
                        i++
                        while (i < code.length && code[i] != c) {
                            if (code[i] == '\\') i++
                            i++
                        }
                        if (i < code.length) i++
                        withStyle(SpanStyle(color = string)) { append(code.substring(start, i)) }
                    }
                    // 数字
                    c.isDigit() -> {
                        val start = i
                        while (i < code.length && (code[i].isDigit() || code[i] == '.')) i++
                        withStyle(SpanStyle(color = number)) { append(code.substring(start, i)) }
                    }
                    // 标识符 / 关键字
                    c.isLetter() || c == '_' -> {
                        val start = i
                        while (i < code.length && (code[i].isLetterOrDigit() || code[i] == '_')) i++
                        val word = code.substring(start, i)
                        if (word in keywords) {
                            withStyle(SpanStyle(color = keyword)) { append(word) }
                        } else {
                            append(word)
                        }
                    }
                    else -> {
                        append(c)
                        i++
                    }
                }
            }
        }
    }
}

/** 根据当前主题与语言对代码做高亮。 */
@Composable
fun highlightedCode(
    code: String,
    language: ScriptLanguage = ScriptLanguage.RUBY,
): AnnotatedString {
    val scheme = MaterialTheme.colorScheme
    return CodeHighlighter.highlight(
        code = code,
        language = language,
        keyword = scheme.primary,
        string = Color(0xFF58A6FF),
        comment = scheme.onSurfaceVariant,
        number = scheme.secondary,
    )
}
