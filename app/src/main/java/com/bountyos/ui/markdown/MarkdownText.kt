package com.bountyos.ui.markdown

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/*
 * 轻量 Markdown 渲染器。
 *
 * 仅渲染常见语法（标题、段落、粗体、斜体、行内代码、代码块、列表、
 * 引用、链接、水平线），不执行 HTML/JavaScript。链接仅允许 http/https
 * 并通过外部浏览器打开，报告正文始终视为不可信内容。
 */

/** Markdown 块级结构。 */
internal sealed interface MarkdownBlock {
    data class Heading(val level: Int, val text: String) : MarkdownBlock
    data class Paragraph(val text: String) : MarkdownBlock
    data class CodeBlock(val code: String) : MarkdownBlock
    data class BulletList(val items: List<String>) : MarkdownBlock
    data class OrderedList(val items: List<String>) : MarkdownBlock
    data class Quote(val text: String) : MarkdownBlock
    data object Divider : MarkdownBlock
}

/** 将 Markdown 源文本解析为块级结构。 */
internal object MarkdownParser {

    fun parse(content: String): List<MarkdownBlock> {
        val lines = content.lines()
        val blocks = mutableListOf<MarkdownBlock>()
        var index = 0
        while (index < lines.size) {
            val line = lines[index]
            when {
                line.isBlank() -> index++

                line.trimStart().startsWith("```") -> {
                    val code = mutableListOf<String>()
                    index++
                    while (index < lines.size && !lines[index].trimStart().startsWith("```")) {
                        code += lines[index]
                        index++
                    }
                    index++ // 跳过结束围栏
                    blocks += MarkdownBlock.CodeBlock(code.joinToString("\n"))
                }

                line.startsWith("#### ") -> { blocks += MarkdownBlock.Heading(4, line.removePrefix("#### ")); index++ }
                line.startsWith("### ") -> { blocks += MarkdownBlock.Heading(3, line.removePrefix("### ")); index++ }
                line.startsWith("## ") -> { blocks += MarkdownBlock.Heading(2, line.removePrefix("## ")); index++ }
                line.startsWith("# ") -> { blocks += MarkdownBlock.Heading(1, line.removePrefix("# ")); index++ }

                isQuoteLine(line) -> {
                    val quote = mutableListOf<String>()
                    while (index < lines.size && isQuoteLine(lines[index])) {
                        quote += lines[index].removePrefix(">").trimStart()
                        index++
                    }
                    blocks += MarkdownBlock.Quote(quote.joinToString(" "))
                }

                isBulletLine(line) -> {
                    val items = mutableListOf<String>()
                    while (index < lines.size && isBulletLine(lines[index])) {
                        items += lines[index].removePrefix("- ").removePrefix("* ")
                        index++
                    }
                    blocks += MarkdownBlock.BulletList(items)
                }

                isOrderedLine(line) -> {
                    val items = mutableListOf<String>()
                    while (index < lines.size && isOrderedLine(lines[index])) {
                        items += lines[index].substringAfter(". ").trimStart()
                        index++
                    }
                    blocks += MarkdownBlock.OrderedList(items)
                }

                isDividerLine(line) -> {
                    blocks += MarkdownBlock.Divider
                    index++
                }

                else -> {
                    val paragraph = mutableListOf<String>()
                    while (index < lines.size && lines[index].isNotBlank() && !isBlockStart(lines[index])) {
                        paragraph += lines[index]
                        index++
                    }
                    blocks += MarkdownBlock.Paragraph(paragraph.joinToString(" "))
                }
            }
        }
        return blocks
    }

    private fun isQuoteLine(line: String): Boolean =
        line.startsWith("> ") || line == ">"

    private fun isBulletLine(line: String): Boolean =
        line.startsWith("- ") || line.startsWith("* ")

    private fun isOrderedLine(line: String): Boolean =
        line.matches(Regex("""\d+\.\s.*"""))

    private fun isDividerLine(line: String): Boolean =
        line.matches(Regex("""\s*([-*_]\s*){3,}"""))

    private fun isBlockStart(line: String): Boolean =
        line.startsWith("#") || line.trimStart().startsWith("```") ||
            isQuoteLine(line) || isBulletLine(line) || isOrderedLine(line) || isDividerLine(line)
}

/**
 * 渲染 Markdown 文本为 Compose 内容。
 */
@Composable
fun MarkdownText(
    content: String,
    modifier: Modifier = Modifier,
) {
    val blocks = remember(content) { MarkdownParser.parse(content) }
    Column(modifier = modifier) {
        blocks.forEach { block ->
            when (block) {
                is MarkdownBlock.Heading -> MarkdownHeading(block)
                is MarkdownBlock.Paragraph -> MarkdownParagraph(block.text)
                is MarkdownBlock.CodeBlock -> MarkdownCodeBlock(block.code)
                is MarkdownBlock.BulletList -> block.items.forEach { MarkdownListItem(it) }
                is MarkdownBlock.OrderedList -> block.items.forEachIndexed { i, it -> MarkdownListItem(it, index = i + 1) }
                is MarkdownBlock.Quote -> MarkdownQuote(block.text)
                MarkdownBlock.Divider -> MarkdownDivider()
            }
        }
    }
}

@Composable
private fun MarkdownHeading(block: MarkdownBlock.Heading) {
    val style = when (block.level) {
        1 -> MaterialTheme.typography.headlineSmall
        2 -> MaterialTheme.typography.titleLarge
        3 -> MaterialTheme.typography.titleMedium
        else -> MaterialTheme.typography.titleMedium
    }
    Text(
        text = block.text,
        style = style,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
    )
}

@Composable
private fun MarkdownParagraph(text: String) {
    val context = LocalContext.current
    val annotated = inlineAnnotated(text, context)
    Text(
        text = annotated,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(vertical = 4.dp),
    )
}

@Composable
private fun MarkdownListItem(text: String, index: Int? = null) {
    val context = LocalContext.current
    val annotated = inlineAnnotated(text, context)
    Row(modifier = Modifier.padding(start = 12.dp, top = 2.dp, bottom = 2.dp)) {
        Text(
            text = index?.let { "$it. " } ?: "\u2022 ",
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = annotated,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun MarkdownCodeBlock(code: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp),
            )
            .padding(12.dp),
    ) {
        Text(
            text = code,
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp,
        )
    }
}

@Composable
private fun MarkdownQuote(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(vertical = 4.dp),
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .fillMaxHeight()
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(2.dp),
                ),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 12.dp),
        )
    }
}

@Composable
private fun MarkdownDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(color = MaterialTheme.colorScheme.onSurfaceVariant)
            .padding(vertical = 0.5.dp),
    )
}

/** 将行内 Markdown（粗体/斜体/代码/链接）解析为带样式的 [AnnotatedString]。 */
@Composable
private fun inlineAnnotated(text: String, context: Context): AnnotatedString {
    val colorScheme = MaterialTheme.colorScheme
    val primary = colorScheme.primary
    val surfaceVariant = colorScheme.surfaceVariant

    val linkListener = LinkInteractionListener { link ->
        if (link is LinkAnnotation.Url) openSafeUrl(context, link.url)
    }

    val pattern = Regex("""(\*\*.*?\*\*|__.*?__|`[^`]*`|\[.*?]\(.*?\))""")
    return buildAnnotatedString {
        var cursor = 0
        for (match in pattern.findAll(text)) {
            append(text.substring(cursor, match.range.first))
            val token = match.value
            when {
                token.startsWith("**") || token.startsWith("__") -> {
                    val inner = token.removeSurrounding("**").removeSurrounding("__")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(inner) }
                }
                token.startsWith("`") -> {
                    withStyle(
                        SpanStyle(
                            fontFamily = FontFamily.Monospace,
                            background = surfaceVariant,
                        )
                    ) { append(token.removeSurrounding("`")) }
                }
                token.startsWith("[") -> {
                    val link = Regex("""\[(.*?)]\((.*?)\)""").matchEntire(token)
                    if (link != null) {
                        val label = link.groupValues[1]
                        val url = link.groupValues[2]
                        withLink(
                            LinkAnnotation.Url(
                                url = url,
                                styles = TextLinkStyles(
                                    style = SpanStyle(
                                        color = primary,
                                        textDecoration = TextDecoration.Underline,
                                    )
                                ),
                                linkInteractionListener = linkListener,
                            )
                        ) { append(label) }
                    } else {
                        append(token)
                    }
                }
                else -> append(token)
            }
            cursor = match.range.last + 1
        }
        append(text.substring(cursor))
    }
}

private fun openSafeUrl(context: Context, url: String) {
    val uri = runCatching { Uri.parse(url) }.getOrNull() ?: return
    if (uri.scheme == "https" || uri.scheme == "http") {
        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
    }
}
