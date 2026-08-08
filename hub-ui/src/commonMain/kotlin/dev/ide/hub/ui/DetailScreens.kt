package dev.ide.hub.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import dev.ide.hub.model.DependencyInfo
import dev.ide.hub.model.Snippet
import dev.ide.hub.model.SnippetImplementation
import dev.ide.hub.ui.components.CodeBlock
import dev.ide.hub.ui.components.DependencyRow
import dev.ide.hub.ui.components.HubChip
import dev.ide.hub.ui.components.SectionTitle
import dev.ide.hub.ui.highlight.highlightLines
import dev.ide.ui.icons.CaIcons

/**
 * Snippet detail: title + description, an implementation selector (Kotlin/Java/Compose/XML), the
 * highlighted code with a primary Copy action, favorite/share, and the snippet's dependencies.
 */
@Composable
fun SnippetDetailScreen(
    snippet: Snippet,
    state: DevHubState,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onOpenDependency: (DependencyInfo) -> Unit,
    onShareText: ((String) -> Unit)? = null,
    onAddToProject: ((Snippet, SnippetImplementation) -> Unit)? = null,
) {
    val clipboard = LocalClipboardManager.current
    var selectedImpl by remember { mutableStateOf(0) }
    var copied by remember { mutableStateOf(false) }
    var added by remember { mutableStateOf(false) }

    val impls = snippet.implementations
    val current = impls.getOrNull(selectedImpl) ?: impls.firstOrNull()

    Column(modifier.fillMaxSize()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
        ) {
            Text(
                if (snippet.category.isNotEmpty()) snippet.category else "",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 8.dp),
            )
            Spacer(Modifier.weight(1f))
            Icon(
                imageVector = if (state.isFavorite(snippet.id)) CaIcons.heartFill else CaIcons.heart,
                contentDescription = "Favorite",
                tint = if (state.isFavorite(snippet.id)) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .clickable { state.toggleFavorite(snippet.id) }
                    .padding(8.dp),
            )
            Icon(
                imageVector = CaIcons.share,
                contentDescription = "Share",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .let {
                        if (onShareText != null && current != null) {
                            it.clickable { renderShare(snippet, current, onShareText) }
                        } else it
                    }
                    .padding(8.dp),
            )
            Icon(
                imageVector = CaIcons.chevronLeft,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .clickable(onClick = onBack)
                    .padding(8.dp),
            )
        }
        HorizontalDivider()
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Text(snippet.title, style = MaterialTheme.typography.headlineSmall)
                if (snippet.description.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        snippet.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            if (impls.size > 1) {
                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                    ) {
                        impls.forEachIndexed { index, impl ->
                            HubChip(
                                label = impl.language,
                                selected = index == selectedImpl,
                                onClick = { selectedImpl = index; copied = false },
                            )
                        }
                    }
                }
            }
            current?.let { impl ->
                item {
                    CodeBlock(
                        code = impl.code,
                        langLabel = when {
                            impl.technology.isNotBlank() -> "${impl.language} · ${impl.technology}"
                            else -> impl.language
                        },
                        highlighted = highlightLines(impl.code, languageOf(impl.language)),
                        copied = copied,
                        onCopy = {
                            clipboard.setText(AnnotatedString(impl.code))
                            copied = true
                        },
                    )
                }
                if (impl.preview.isNotBlank()) {
                    item {
                        Text("Preview", style = MaterialTheme.typography.titleSmall)
                        Text(
                            impl.preview,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
                }
                if (onAddToProject != null) {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextButton(
                                enabled = !added,
                                onClick = {
                                    onAddToProject(snippet, impl)
                                    added = true
                                },
                            ) {
                                Text(if (added) "Added to project" else "Add to project")
                            }
                            if (added) {
                                Text(
                                    "Open it in the editor to use it",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            } else {
                                Text(
                                    "Creates a file with this code in the current project",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            }
            if (snippet.tags.isNotEmpty()) {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        snippet.tags.forEach { tag -> HubChip(tag) }
                    }
                }
            }
            if (snippet.dependencies.isNotEmpty()) {
                item { SectionTitle("Dependencies") }
                items(snippet.dependencies) { depId ->
                    val dep = state.dependency(depId)
                    if (dep != null) {
                        DependencyRow(
                            groupId = dep.groupId,
                            artifactId = dep.artifactId,
                            version = dep.latestVersion,
                            onCopy = {
                                clipboard.setText(AnnotatedString(depKts(dep.groupId, dep.artifactId, dep.latestVersion)))
                            },
                            onClick = { onOpenDependency(dep) },
                            modifier = Modifier.padding(vertical = 2.dp),
                        )
                    } else {
                        Text(
                            depId,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

private fun renderShare(snippet: Snippet, impl: dev.ide.hub.model.SnippetImplementation, onShare: (String) -> Unit) {
    onShare(buildAnnotatedString {
        append(snippet.title)
        append("\n\n")
        append(impl.code)
    }.text)
}

/** Dependency detail: catalog metadata + ready-to-paste declarations in all three Gradle dialects. */
@Composable
fun DependencyDetailScreen(
    dep: DependencyInfo,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
) {
    val clipboard = LocalClipboardManager.current
    var copied by remember { mutableStateOf<String?>(null) }

    fun copy(label: String, text: String) {
        clipboard.setText(AnnotatedString(text))
        copied = label
    }

    Column(modifier.fillMaxSize()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
        ) {
            Text(
                dep.groupId,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 8.dp),
            )
            Spacer(Modifier.weight(1f))
            Icon(
                imageVector = CaIcons.chevronLeft,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .clickable(onClick = onBack)
                    .padding(8.dp),
            )
        }
        HorizontalDivider()
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Text(dep.artifactId, style = MaterialTheme.typography.headlineSmall)
                Text(
                    "${dep.groupId}:${dep.artifactId}:${dep.latestVersion}",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary),
                )
            }

            item { SectionTitle("Declarations") }
            item { DeclarationRow(
                label = "Gradle Kotlin DSL",
                code = depKts(dep.groupId, dep.artifactId, dep.latestVersion),
                copied = copied,
                onCopy = { copy("Gradle Kotlin DSL", depKts(dep.groupId, dep.artifactId, dep.latestVersion)) },
            ) }
            item { DeclarationRow(
                label = "Gradle Groovy",
                code = depGroovy(dep.groupId, dep.artifactId, dep.latestVersion),
                copied = copied,
                onCopy = { copy("Gradle Groovy", depGroovy(dep.groupId, dep.artifactId, dep.latestVersion)) },
            ) }
            item { DeclarationRow(
                label = "Version catalog",
                code = depCatalog(dep.groupId, dep.artifactId, dep.latestVersion),
                copied = copied,
                onCopy = { copy("Version catalog", depCatalog(dep.groupId, dep.artifactId, dep.latestVersion)) },
            ) }

            if (dep.versions.isNotEmpty()) {
                item { SectionTitle("Versions") }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        dep.versions.forEach { v ->
                            HubChip(
                                label = v,
                                selected = v == dep.latestVersion,
                                onClick = {
                                    clipboard.setText(AnnotatedString(depKts(dep.groupId, dep.artifactId, v)))
                                },
                            )
                        }
                    }
                }
            }

            if (dep.repository.isNotBlank() || dep.license.isNotBlank() || dep.releaseDate.isNotBlank()) {
                item { SectionTitle("Metadata") }
                item {
                    Column {
                        if (dep.repository.isNotBlank()) {
                            MetaLine("Repository", dep.repository)
                        }
                        if (dep.license.isNotBlank()) {
                            MetaLine("License", dep.license)
                        }
                        if (dep.releaseDate.isNotBlank()) {
                            MetaLine("Release date", dep.releaseDate)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DeclarationRow(
    label: String,
    code: String,
    copied: String?,
    onCopy: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(label, style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1f).padding(12.dp))
            Text(
                if (copied == label) "Copied" else "Copy",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable(onClick = onCopy).padding(12.dp),
            )
        }
        Text(
            code,
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
            ),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
        )
    }
}

@Composable
private fun MetaLine(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(96.dp),
        )
        Text(value, style = MaterialTheme.typography.bodySmall)
    }
}