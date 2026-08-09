package dev.ide.ui.editor

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/** [parseSnippetTemplate]: the DevHub template → simplified text + [dev.ide.ui.backend.UiSnippet]. */
class SnippetTemplateParserTest {

    @Test
    fun plainTextHasNoSnippet() {
        val p = parseSnippetTemplate("fun main = Unit")
        assertEquals("fun main = Unit", p.text)
        assertNull(p.snippet)
    }

    @Test
    fun tabStopsAreOffsetsInOrder() {
        val p = parseSnippetTemplate("foo(\$1, \$2)")
        assertNotNull(p.snippet)
        assertEquals("foo(, )", p.text)
        assertEquals(2, p.snippet!!.stops.size)
        assertEquals(1, p.snippet!!.stops[0].index)
        assertEquals(listOf(UiTextRange(4, 4)), p.snippet!!.stops[0].ranges)
        assertEquals(2, p.snippet!!.stops[1].index)
        assertEquals(7, p.snippet!!.finalCaretOffset)
    }

    @Test
    fun braceWithDefaultFillsTheDefaultAndStops() {
        val p = parseSnippetTemplate("LazyColumn(\${1:items}) { }")
        assertEquals("LazyColumn(items) { }", p.text)
        val stop = p.snippet!!.stops.single()
        assertEquals(1, stop.index)
        assertEquals(UiTextRange(11, 16), stop.ranges.single())
    }

    @Test
    fun choicesKeepFirstAndTheList() {
        val p = parseSnippetTemplate("when (x) \${1|a,b,c|}")
        assertEquals("when (x) a", p.text)
        val stop = p.snippet!!.stops.single()
        assertEquals(listOf("a", "b", "c"), stop.choices)
        assertEquals(UiTextRange(9, 10), stop.ranges.single())
    }

    @Test
    fun repeatedStopIsLinked() {
        val p = parseSnippetTemplate("\${1:a} == \${1:a}")
        assertEquals(1, p.snippet!!.stops.size)
        assertEquals(
            listOf(UiTextRange(0, 1), UiTextRange(5, 6)),
            p.snippet!!.stops[0].ranges,
        )
    }

    @Test
    fun endMarkerSetsFinalCaret() {
        val p = parseSnippetTemplate("a\$END\$b")
        assertEquals("ab", p.text)
        assertEquals(1, p.snippet!!.finalCaretOffset)
    }

    @Test
    fun escapesSurvive() {
        val p = parseSnippetTemplate("cost = \$\$5 \\\$ \\{")
        assertEquals("cost = \$5 \$ {", p.text)
        assertNull(p.snippet)
    }

    @Test
    fun unresolvedVariablesFallBackToLiterals() {
        val p = parseSnippetTemplate("\${NAME} \$FILE\$")
        assertEquals("NAME FILE", p.text)
        assertNull(p.snippet)
    }

    @Test
    fun stopsFollowFirstAppearanceOrder() {
        val p = parseSnippetTemplate("\$2 }\$1")
        assertEquals(" }", p.text)
        assertEquals(listOf(2, 1), p.snippet!!.stops.map { it.index })
    }

    @Test
    fun zeroMarkerLandsFinalCaret() {
        val p = parseSnippetTemplate("x\$0 tail")
        assertEquals("x tail", p.text)
        assertEquals(1, p.snippet!!.finalCaretOffset)
    }
}