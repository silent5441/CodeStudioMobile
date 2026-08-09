package dev.ide.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.ide.ui.backend.UiSnippetBlock
import dev.ide.ui.generated.resources.Res
import dev.ide.ui.generated.resources.edblocks_open
import dev.ide.ui.generated.resources.edblocks_paste_hint
import dev.ide.ui.icons.CaIcons
import dev.ide.ui.theme.Ide
import org.jetbrains.compose.resources.stringResource

/**
 * The DevHub Blocks palette: a bottom sheet over the editor listing the blocks for the active file's
 * language (see [blockMatchesLanguage]); tapping one pastes its template at the caret (the editor's
 * snippet machinery takes over — Tab walks the placeholders). Dismisses on a tap of the scrim.
 */
@Composable
fun BlockPaletteSheet(
    blocks: List<UiSnippetBlock>,
    onPick: (UiSnippetBlock) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxSize()) {
        // Scrim — tap anywhere outside the sheet closes it; no ripple, like the app's other overlay scrims.
        Box(
            Modifier.fillMaxSize().background(Ide.colors.scrim)
                .clickable(remember { MutableInteractionSource() }, indication = null, onClick = onDismiss)
        )
        Surface(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            color = MaterialTheme.colorScheme.surfaceContainer,
            shadowElevation = 12.dp,
        ) {
            Column(Modifier.heightIn(max = 420.dp).padding(bottom = 16.dp)) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        CaIcons.box,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        stringResource(Res.string.edblocks_open),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(start = 10.dp),
                    )
                    Text(
                        stringResource(Res.string.edblocks_paste_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 10.dp),
                    )
                }
                LazyColumn(
                    Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    items(blocks, key = { "${it.trigger}:${it.name}" }) { block ->
                        BlockRow(block, onClick = { onPick(block) })
                    }
                }
            }
        }
    }
}

@Composable
private fun BlockRow(block: UiSnippetBlock, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(interactionSource = null, indication = null, onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = block.trigger,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.size(width = 64.dp, height = 20.dp),
            maxLines = 1,
        )
        Column(Modifier.weight(1f)) {
            Text(
                text = block.name,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (block.description.isNotBlank()) {
                Text(
                    text = block.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

/** The blocks that apply to a file: its language ids gate this (blocks with no language apply everywhere). */
fun blocksForPath(
    path: String?,
    source: ((String?) -> List<UiSnippetBlock>)?,
): List<UiSnippetBlock> {
    if (source == null) return emptyList()
    val langs = languageIdsOf(path ?: "")
    return source(null)
        .filter { it.trigger.isNotBlank() }
        .filter { b -> langs.isEmpty() || b.languages.isEmpty() || b.languages.any { it in langs } }
        .sortedBy { it.trigger.lowercase() }
}

internal fun languageIdsOf(path: String): List<String> = when (path.substringAfterLast('.', "").lowercase()) {
    "kt", "kts" -> listOf("kotlin")
    "java" -> listOf("java")
    "xml" -> listOf("xml")
    else -> emptyList()
}