package dev.ide.core.customize

import dev.ide.ui.backend.UiSnippetBlock

/**
 * Merges the host's DevHub code blocks into the user live-template macro list: a block becomes a plain
 * statement macro whose abbreviation is its trigger. Blocks gated to other languages by the caller; here
 * only collisions are decided — an abbreviation the user's own macros already define wins (the catalog
 * never shadows a user-authored macro, and the user can still disable/override it through the normal
 * customization files). Blank trigger/template blocks are skipped.
 */
fun mergeSnippetBlocks(
    base: List<MacroDef>,
    blocks: List<UiSnippetBlock>,
): List<MacroDef> {
    if (blocks.isEmpty()) return base
    val taken = HashMap<String, Boolean>(base.size * 2)
    for (m in base) taken[m.abbreviation] = true
    val out = ArrayList<MacroDef>(base.size + blocks.size)
    out.addAll(base)
    for (b in blocks) {
        val abbrev = b.trigger.trim()
        if (abbrev.isEmpty() || b.template.isBlank()) continue
        if (taken.containsKey(abbrev)) continue
        taken[abbrev] = true
        out.add(
            MacroDef(
                abbreviation = abbrev,
                template = b.template,
                description = b.description.ifBlank { b.name },
                languages = b.languages,
            )
        )
    }
    return out
}