package dev.ide.ui.editor

import dev.ide.ui.backend.UiSnippet
import dev.ide.ui.backend.UiSnippetStop
import dev.ide.ui.backend.UiTextRange

/**
 * Parses a DevHub block's template into the text to paste at the caret plus the [UiSnippet] the editor's
 * [SnippetSession] steps through. The grammar is a practical subset of the engine's snippet syntax
 * (see [dev.ide.lang.template.Snippet]): `$1`/`${1}` tab stops, `${1:default}` (default text inserted, the
 * stop selects it), `${1|a,b,c|}` (the first choice inserted, the full list kept for a choices popup), `$0`
 * and `$END$` the final caret, `$$` a literal `$`, `\$`/`\{`/`\}`/`\\` literal escapes. Unresolved variables
 * (`$NAME` / `${NAME}` / `${NAME:default}`) are kept as literal text (default value preferred), matching the
 * engine's fallback for an unresolvable variable.
 *
 * A repeated stop index is linked: one stop holding several ranges, so the editor mirrors edits across every
 * copy. The result is ordered by first appearance; when the template has no stops at all, [ParsedSnippet.
 * snippet] is null and the caller just pastes [ParsedSnippet.text] as plain text.
 */
data class ParsedSnippet(val text: String, val snippet: UiSnippet?)

fun parseSnippetTemplate(template: String): ParsedSnippet {
    val text = StringBuilder(template.length)
    val groups = LinkedHashMap<Int, MutableList<UiTextRange>>()
    val choices = HashMap<Int, List<String>>()
    var finalCaret = -1

    fun addStop(index: Int, start: Int, end: Int) {
        groups.getOrPut(index) { ArrayList(1) }.add(UiTextRange(start, end))
    }

    var i = 0
    while (i < template.length) {
        val c = template[i]
        if (c == '\\' && i + 1 < template.length && template[i + 1] in "\\\${}") {
            text.append(template[i + 1])
            i += 2
            continue
        }
        if (c != '$') {
            text.append(c)
            i++
            continue
        }
        // `$$` → a literal `$`.
        if (i + 1 < template.length && template[i + 1] == '$') {
            text.append('$')
            i += 2
            continue
        }
        val brace = i + 1 < template.length && template[i + 1] == '{'
        val bodyStart = if (brace) i + 2 else i + 1
        if (brace) {
            val close = template.indexOf('}', bodyStart)
            if (close < 0) {
                // Unbalanced `{` — keep the `$` literal and move on.
                text.append('$')
                i++
                continue
            }
            val body = template.substring(bodyStart, close)
            val indexName = body.substringBefore(':')
            // The tail after the index — the `|a,b,c|` CHOICES form is carried whole (it has no colon).
            val extra: String? = when {
                ':' in body -> body.substringAfter(':')
                body.firstOrNull() == '|' -> body
                else -> null
            }
            val index = indexName.toIntOrNull()
            if (index == null) {
                // Unresolved variable `${NAME}` / `${NAME:default}` → literal (default when present).
                text.append(extra ?: indexName)
                i = close + 1
                continue
            }
            val startIn = text.length
            if (index == 0) {
                finalCaret = startIn
                i = close + 1
                continue
            }
            if (extra != null && index > 0) {
                if (extra.startsWith("|") && extra.endsWith("|")) {
                    val parts = extra.substring(1, extra.length - 1).split('|')
                    text.append(parts.firstOrNull() ?: "")
                    choices[index] = parts
                } else {
                    text.append(extra)
                }
            }
            addStop(index, startIn, text.length)
            i = close + 1
            continue
        }
        // Bare form: scan `$NAME` / `$NAME$`.
        var j = i + 1
        while (j < template.length && (template[j].isLetterOrDigit() || template[j] == '_')) j++
        if (j == i + 1) {
            // A lone `$` (not escaping anything) → literal.
            text.append('$')
            i++
            continue
        }
        val name = template.substring(i + 1, j)
        val delimited = j < template.length && template[j] == '$'
        if (name == "END") {
            finalCaret = text.length
            i = if (delimited) j + 1 else j
            continue
        }
        val index = name.toIntOrNull()
        if (index != null) {
            if (index == 0) finalCaret = text.length
            else addStop(index, text.length, text.length)
            i = if (delimited) j + 1 else j
            continue
        }
        // An unresolved bare variable: `$NAME$` → the bare name; `$NAME` stays as an (almost certainly
        // intentional) literal.
        if (delimited) text.append(name) else {
            text.append('$').append(name)
        }
        i = if (delimited) j + 1 else j
    }

    val final = finalCaret.takeIf { it >= 0 } ?: text.length
    val stops = groups.entries.map { (index, ranges) ->
        UiSnippetStop(index = index, ranges = ranges, choices = choices[index] ?: emptyList())
    }
    val snippet = if (stops.isEmpty() && finalCaret < 0) null else UiSnippet(stops, finalCaret)
    return ParsedSnippet(text.toString(), snippet)
}