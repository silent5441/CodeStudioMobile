package dev.ide.hub.ui.highlight

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle

/**
 * A tiny regex-free line scanner for the DevHub code cards. It tokenizes a snippet line by line and
 * produces per-line [AnnotatedString]s with a [HighlightColors] palette. Deliberately simple: it is a
 * snippets viewer, not the editor's JDT/IntelliJ engines.
 */
enum class HighlightLang { Kotlin, Java, Compose, Xml, Groovy, Text }

enum class TokenKind { Comment, String, Keyword, Annotation, Number, Tag, Attr, Plain }

internal data class Tok(
    val text: String,
    val kind: TokenKind,
)

internal interface Scanner {
    fun scan(line: String): List<Tok>
}

fun highlightLines(
    code: String,
    lang: HighlightLang = HighlightLang.Kotlin,
    colors: HighlightColors = HighlightColors(),
): List<AnnotatedString> {
    val scanner = when (lang) {
        HighlightLang.Xml -> XmlScanner
        HighlightLang.Text -> PlainScanner
        else -> KotlinLikeScanner
    }
    return code.lineSequence().map { line -> annotate(line, scanner.scan(line), colors) }.toList()
}

/** Muted, scheme-friendly defaults; callers can also override with their own scheme colors. */
data class HighlightColors(
    val keyword: Color = Color(0xFF9C27B0),
    val string: Color = Color(0xFF2E7D32),
    val comment: Color = Color(0xFF757575),
    val annotation: Color = Color(0xFFE65100),
    val number: Color = Color(0xFF0277BD),
    val tag: Color = Color(0xFF1565C0),
    val attr: Color = Color(0xFF00838F),
    val plain: Color = Color.Unspecified,
) {
    fun styleFor(kind: TokenKind, isDark: Boolean): SpanStyle {
        val tint = when (kind) {
            TokenKind.Keyword -> if (isDark) Color(0xFFCE93D8) else keyword
            TokenKind.String -> if (isDark) Color(0xFF81C784) else string
            TokenKind.Comment -> if (isDark) Color(0xFF9E9E9E) else comment
            TokenKind.Annotation -> if (isDark) Color(0xFFFFB74D) else annotation
            TokenKind.Number -> if (isDark) Color(0xFF64B5F6) else number
            TokenKind.Tag -> if (isDark) Color(0xFF90CAF9) else tag
            TokenKind.Attr -> if (isDark) Color(0xFF4DD0E1) else attr
            TokenKind.Plain -> plain
        }
        return SpanStyle(if (tint == Color.Unspecified) androidx.compose.ui.graphics.Color.Unspecified else tint)
    }
}

private fun annotate(line: String, tokens: List<Tok>, colors: HighlightColors): AnnotatedString {
    if (tokens.isEmpty()) return AnnotatedString(line)

    val sb = StringBuilder(line.length)
    val ranges = mutableListOf<AnnotatedString.Range<SpanStyle>>()
    // A rough darkness hint from the platform scheme is overkill here; use a neutral default palette
    // (callers may theme): keep plain default, so pass isDark=false and let explicit colors shine.
    var cursor = 0
    for (t in tokens) {
        // Skip empty tokens; everything else must line up in order; guarded find handles scanner drift.
        if (t.text.isEmpty()) continue
        val idx = line.indexOf(t.text, startIndex = cursor)
        val start = if (idx == -1) sb.length else {
            if (idx > cursor) sb.append(line, cursor, idx)
            sb.length
        }
        sb.append(t.text)
        if (t.kind == TokenKind.Plain) {
            cursor = if (idx == -1) sb.length else idx + t.text.length
        } else {
            ranges += AnnotatedString.Range(colors.styleFor(t.kind, isDark = false), start, start + t.text.length)
            cursor = if (idx == -1) sb.length else idx + t.text.length
        }
    }
    if (cursor < line.length) sb.append(line, cursor, line.length)
    return AnnotatedString(sb.toString(), spanStyles = ranges)
}

private object PlainScanner : Scanner {
    override fun scan(line: String): List<Tok> = emptyList()
}

/** Kotlin / Java / Compose / Groovy share one scanner: comments, strings, annotations, keywords, numbers. */
private object KotlinLikeScanner : Scanner {

    private val KEYWORDS = setOf(
        "fun", "val", "var", "if", "else", "when", "for", "while", "do", "return", "break",
        "continue", "class", "object", "interface", "data", "sealed", "enum", "open", "abstract",
        "final", "override", "private", "public", "internal", "protected", "import", "package",
        "suspend", "companion", "init", "this", "super", "null", "true", "false", "is", "in",
        "as", "try", "catch", "finally", "throw", "by", "get", "set", "typealias", "const",
        "lateinit", "out", "inline", "reified", "where", "void", "int", "long", "boolean",
        "float", "double", "char", "byte", "short", "new", "extends", "implements", "static",
        "public", "synchronized", "volatile", "transient", "instanceof", "native", "strictfp",
    )

    override fun scan(line: String): List<Tok> {
        val tokens = mutableListOf<Tok>()
        var i = 0
        val n = line.length
        while (i < n) {
            val c = line[i]
            when {
                // line comment swallows the rest of the line
                c == '/' && i + 1 < n && line[i + 1] == '/' -> {
                    tokens.add(Tok(line.substring(i), TokenKind.Comment))
                    return tokens
                }
                c.isLetter() || c == '_' -> {
                    val start = i
                    while (i < n && (line[i].isLetterOrDigit() || line[i] == '_')) i++
                    val word = line.substring(start, i)
                    tokens.add(Tok(word, if (word in KEYWORDS) TokenKind.Keyword else TokenKind.Plain))
                }
                c == '"' -> {
                    val start = i
                    i++
                    while (i < n) {
                        if (line[i] == '\\' && i + 1 < n) { i += 2; continue }
                        if (line[i] == '"') { i++; break }
                        i++
                    }
                    tokens.add(Tok(line.substring(start, i), TokenKind.String))
                }
                c == '\'' -> {
                    val start = i
                    i++
                    while (i < n) {
                        if (line[i] == '\\' && i + 1 < n) { i += 2; continue }
                        if (line[i] == '\'') { i++; break }
                        i++
                    }
                    tokens.add(Tok(line.substring(start, i), TokenKind.String))
                }
                c == '@' && i + 1 < n && line[i + 1].isLetter() -> {
                    val start = i
                    i++
                    while (i < n && (line[i].isLetterOrDigit() || line[i] in "._")) i++
                    tokens.add(Tok(line.substring(start, i), TokenKind.Annotation))
                }
                c.isDigit() -> {
                    val start = i
                    while (i < n && (line[i].isLetterOrDigit() || line[i] == '.' || line[i] in "xX_")) i++
                    tokens.add(Tok(line.substring(start, i), TokenKind.Number))
                }
                else -> {
                    val start = i
                    i++
                    tokens.add(Tok(line.substring(start, i), TokenKind.Plain))
                }
            }
        }
        return tokens
    }
}

/** XML: comments, tags (names inside <…>), attribute names and their string values. */
private object XmlScanner : Scanner {
    override fun scan(line: String): List<Tok> {
        val tokens = mutableListOf<Tok>()
        var i = 0
        val n = line.length
        while (i < n) {
            val c = line[i]
            when {
                c == '<' && i + 4 < n && line.startsWith("<!--", i) -> {
                    val end = line.indexOf("-->", i + 4)
                    val stop = if (end == -1) n else end + 3
                    tokens.add(Tok(line.substring(i, stop), TokenKind.Comment))
                    i = stop
                }
                c == '<' -> {
                    val start = i
                    i++
                    // tag name up to whitespace, '/', or '>'
                    while (i < n && line[i] !in " >\\t/") i++
                    tokens.add(Tok(line.substring(start, i), TokenKind.Tag))
                }
                c == '>' || c == '/' -> {
                    val start = i
                    if (c == '/' && i + 1 < n && line[i + 1] == '>') {
                        i += 2
                        tokens.add(Tok(line.substring(start, i), TokenKind.Plain))
                    } else {
                        i++
                        tokens.add(Tok(line.substring(start, i), TokenKind.Plain))
                    }
                }
                c == '"' || c == '\'' -> {
                    val start = i
                    val quote = c
                    i++
                    while (i < n && line[i] != quote) i++
                    if (i < n) i++
                    tokens.add(Tok(line.substring(start, i), TokenKind.String))
                }
                c.isLetter() || c == '_' -> {
                    val start = i
                    while (i < n && (line[i].isLetterOrDigit() || line[i] == '_' || line[i] == ':' || line[i] == '.')) i++
                    val word = line.substring(start, i)
                    // attribute if immediately followed by '=' (possibly spaces)
                    val after = i
                    var j = after
                    while (j < n && line[j] == ' ') j++
                    if (j < n && line[j] == '=') tokens.add(Tok(word, TokenKind.Attr)) else tokens.add(Tok(word, TokenKind.Plain))
                }
                else -> {
                    val start = i
                    i++
                    tokens.add(Tok(line.substring(start, i), TokenKind.Plain))
                }
            }
        }
        return tokens
    }
}