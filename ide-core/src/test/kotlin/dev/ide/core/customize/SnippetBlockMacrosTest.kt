package dev.ide.core.customize

import dev.ide.ui.backend.UiSnippetBlock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertTrue

class SnippetBlockMacrosTest {

    private fun block(trigger: String, name: String = trigger, template: String = "\$0") =
        UiSnippetBlock(name = name, trigger = trigger, template = template, description = "")

    @Test
    fun emptyBlocksAreApassThrough() {
        val base = listOf(MacroDef("sout", "S.out()"))
        assertEquals(base, mergeSnippetBlocks(base, emptyList()))
    }

    @Test
    fun appendedBlocksBecomeMacrosWithTheirTrigger() {
        val merged = mergeSnippetBlocks(emptyList(), listOf(block("lzl", "lazyColumn", "\$1, content=\$2")))
        assertEquals(1, merged.size)
        assertEquals("lzl", merged[0].abbreviation)
        assertEquals("\$1, content=\$2", merged[0].template)
        assertEquals("lazyColumn", merged[0].description)
        assertEquals(false, merged[0].builtIn)
        assertEquals(emptyList(), merged[0].languages)
    }

    @Test
    fun userMacroWinsOnAbbreviationCollision() {
        val base = listOf(MacroDef("lzl", "user's"))
        val merged = mergeSnippetBlocks(base, listOf(block("lzl", "catalog")))
        assertEquals(1, merged.size)
        assertEquals("user's", merged[0].template)
    }

    @Test
    fun blankTriggerOrTemplateIsSkipped() {
        val merged = mergeSnippetBlocks(emptyList(), listOf(block("", template = "x"), block("ok", template = "  ")))
        assertTrue(merged.isEmpty())
    }

    @Test
    fun catalogBlocksCarryTheirLanguageGates() {
        val merged = mergeSnippetBlocks(emptyList(), listOf(block("b", template = "$x").copy(languages = listOf("kotlin"))))
        assertEquals(listOf("kotlin"), merged[0].languages)
    }
}