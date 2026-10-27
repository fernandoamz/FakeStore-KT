package com.example.fakestore.feature.products.llm

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

/**
 * Lightweight markdown for assistant bubbles:
 * **bold**, *italic*, bullet/numbered lists, paragraphs.
 */
fun parseAssistantMarkdown(markdown: String): AnnotatedString {
    val normalized = markdown
        .replace("\r\n", "\n")
        .trim()

    return buildAnnotatedString {
        val lines = normalized.lines()
        lines.forEachIndexed { index, rawLine ->
            val line = rawLine.trimEnd()
            when {
                line.isBlank() -> {
                    if (index < lines.lastIndex) append("\n")
                }
                line.startsWith("### ") -> {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        appendInlineMarkdown(line.removePrefix("### ").trim())
                    }
                }
                line.startsWith("## ") -> {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        appendInlineMarkdown(line.removePrefix("## ").trim())
                    }
                }
                line.startsWith("# ") -> {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        appendInlineMarkdown(line.removePrefix("# ").trim())
                    }
                }
                line.startsWith("- ") || line.startsWith("* ") -> {
                    append("• ")
                    appendInlineMarkdown(line.drop(2).trim())
                }
                NUMBERED_LIST.matchEntire(line) != null -> {
                    val match = NUMBERED_LIST.matchEntire(line)!!
                    append("${match.groupValues[1]}. ")
                    appendInlineMarkdown(match.groupValues[2].trim())
                }
                else -> appendInlineMarkdown(line)
            }
            if (index < lines.lastIndex) append("\n")
        }
    }
}

private val NUMBERED_LIST = Regex("""^(\d+)\.\s+(.*)$""")
private val INLINE_TOKEN = Regex("""(\*\*[^*]+\*\*|\*[^*]+\*|_[^_]+_|`[^`]+`)""")

private fun AnnotatedString.Builder.appendInlineMarkdown(text: String) {
    var cursor = 0
    INLINE_TOKEN.findAll(text).forEach { match ->
        if (match.range.first > cursor) {
            append(text.substring(cursor, match.range.first))
        }
        val token = match.value
        when {
            token.startsWith("**") && token.endsWith("**") && token.length >= 4 -> {
                withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                    append(token.removeSurrounding("**"))
                }
            }
            token.startsWith("*") && token.endsWith("*") && token.length >= 2 -> {
                withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                    append(token.removeSurrounding("*"))
                }
            }
            token.startsWith("_") && token.endsWith("_") && token.length >= 2 -> {
                withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                    append(token.removeSurrounding("_"))
                }
            }
            token.startsWith("`") && token.endsWith("`") && token.length >= 2 -> {
                withStyle(SpanStyle(fontWeight = FontWeight.Medium)) {
                    append(token.removeSurrounding("`"))
                }
            }
            else -> append(token)
        }
        cursor = match.range.last + 1
    }
    if (cursor < text.length) {
        append(text.substring(cursor))
    }
}
