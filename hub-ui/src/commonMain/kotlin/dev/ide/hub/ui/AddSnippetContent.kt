package dev.ide.hub.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.ide.hub.ui.components.HubChip
import kotlinx.coroutines.launch

/**
 * The hidden local authoring dashboard ("DevPushHub", offline edition): long-press the hub header title
 * to reach it. Snippets authored here are merged straight into the local catalog and appear in DevHub
 * immediately (Home/Explore/Search). A Firebase-backed push with auth comes later.
 */
@Composable
fun AddSnippetContent(
    state: DevHubState,
    onCancel: () -> Unit,
    onSaved: () -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var tagsText by remember { mutableStateOf("") }
    var dependencyText by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var language by remember { mutableStateOf("kotlin") }
    var blockMode by remember { mutableStateOf(false) }
    var trigger by remember { mutableStateOf("") }
    var saving by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val languages = listOf("kotlin", "compose", "java", "xml")

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            TextButton(onClick = onCancel, enabled = !saving) { Text("Cancel") }
            Spacer(Modifier.weight(1f))
            Text(if (blockMode) "Add Block" else "Add Snippet", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.weight(1f))
            TextButton(
                onClick = {
                    saving = true
                    scope.launch {
                        val ok = if (blockMode) {
                            state.addLocalBlock(
                                title = title,
                                description = description,
                                language = language,
                                trigger = trigger,
                                template = code,
                            )
                        } else {
                            state.addLocalSnippet(
                                title = title,
                                description = description,
                                category = category,
                                tags = tagsText.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                                language = language,
                                technology = language.replaceFirstChar { it.uppercase() },
                                code = code,
                                dependency = dependencyText,
                            )
                        }
                        if (ok) onSaved()
                        else saving = false
                    }
                },
                enabled = title.isNotBlank() && code.isNotBlank() && !saving,
            ) { Text(if (saving) "Saving…" else "Save") }
        }
        Column(
            Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Category (e.g. UI, Networking)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                languages.forEach { lang ->
                    HubChip(label = lang.replaceFirstChar { it.uppercase() }, selected = language == lang) {
                        language = lang
                    }
                }
            }
            if (blockMode) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    HubChip(label = "Block", selected = blockMode) { blockMode = true }
                    HubChip(label = "Snippet", selected = !blockMode) { blockMode = false }
                    Text(
                        "Live-template block: pick it in the editor → pastes at the caret",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f),
                    )
                }
                OutlinedTextField(
                    value = trigger,
                    onValueChange = { trigger = it },
                    label = { Text("Trigger (completion abbreviation, e.g. lc)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    "Template markers: \$1, \$2 … tab stops · \${1:default} · \$0/\$END\$ final caret · \$\$ literal \$",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                Text(
                    "To make a block (pastes at the cursor in the editor) use the Block toggle below the language row",
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = tagsText,
                onValueChange = { tagsText = it },
                label = { Text("Tags (comma separated)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = dependencyText,
                onValueChange = { dependencyText = it },
                label = { Text("Dependency g:a:v (optional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = code,
                onValueChange = { code = it },
                label = { Text("Code *") },
                minLines = 8,
                textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                modifier = Modifier.fillMaxWidth().height(220.dp),
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}
