package dev.ide.hub.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import dev.ide.hub.Filters
import dev.ide.hub.SearchHit
import dev.ide.hub.model.DependencyInfo
import dev.ide.hub.model.Snippet
import dev.ide.hub.ui.components.CodeBlock
import dev.ide.hub.ui.components.DependencyRow
import dev.ide.hub.ui.components.HubChip
import dev.ide.hub.ui.components.HubEmptyState
import dev.ide.hub.ui.components.SectionTitle
import dev.ide.hub.ui.components.SnippetRow
import dev.ide.hub.ui.highlight.HighlightLang
import dev.ide.hub.ui.highlight.highlightLines
import dev.ide.ui.icons.CaIcons
import kotlinx.coroutines.launch

/** The DevHub shell's primary destinations (bottom navigation). */
enum class HubDest(val label: String, val icon: ImageVector) {
    Home("Home", CaIcons.home),
    Explore("Explore", CaIcons.compass),
    Search("Search", CaIcons.search),
    Categories("Browse", CaIcons.grid),
    Favorites("Favs", CaIcons.heart),
    Settings("Settings", CaIcons.gear),
}

// 6 destinations need room: below this the shell switches to icon-only tabs so the 80%-wide editor
// overlay (≈320dp on phones) keeps its icons aligned and readable.
private val NAV_LABEL_MIN_WIDTH = 340.dp

val ALL_LANGUAGE_FILTERS = listOf("Kotlin", "Java", "Compose", "XML")

fun languageOf(implementationLanguage: String): HighlightLang = when (implementationLanguage.lowercase()) {
    "kotlin" -> HighlightLang.Kotlin
    "java" -> HighlightLang.Java
    "compose" -> HighlightLang.Compose
    "xml" -> HighlightLang.Xml
    "groovy" -> HighlightLang.Groovy
    else -> HighlightLang.Text
}

fun depKts(g: String, a: String, v: String) = "implementation(\"$g:$a:$v\")"
fun depGroovy(g: String, a: String, v: String) = "implementation '$g:$a:$v'"
fun depCatalog(g: String, a: String, v: String) = "$a = { module = \"$g:$a\", version = \"$v\" }"

/**
 * DevHub: the on-device code + dependency reference hub. A self-contained shell with its own bottom
 * navigation; hosts: Home, Explore, Search, Categories, Favorites, Settings + detail screens.
 */
@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun DevHubScreen(
    state: DevHubState,
    modifier: Modifier = Modifier,
    onClose: () -> Unit,
    onOpenSnippet: (Snippet) -> Unit,
    onOpenDependency: (DependencyInfo) -> Unit,
    onShareText: ((String) -> Unit)? = null,
) {
    var dest by remember { mutableStateOf(HubDest.Home) }
    var categoryFilter by remember { mutableStateOf<String?>(null) }
    // Hidden local-authoring dashboard: long-press the hub title to open the Add Snippet form.
    var showAddSnippet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { state.load() }

    Column(modifier.fillMaxSize()) {
        // Screen header: the hub is a full-screen destination, so a consistent bar with the close affordance.
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
        ) {
            Icon(
                imageVector = CaIcons.chevronLeft,
                contentDescription = "Close",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .clickable(onClick = onClose)
                    .padding(8.dp),
            )
            Text(
                "Developer Hub",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.combinedClickable(
                    onClick = {},
                    onLongClick = { showAddSnippet = true },
                ).padding(horizontal = 8.dp, vertical = 4.dp),
            )
            Spacer(Modifier.weight(1f))
            // The active destination, so the header reads as part of the shell (not a second app).
            Text(
                dest.label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 12.dp),
            )
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        Box(Modifier.weight(1f)) {
            val catalog = state.catalog
            when {
                showAddSnippet -> AddSnippetContent(
                    state = state,
                    onCancel = { showAddSnippet = false },
                    onSaved = { showAddSnippet = false; dest = HubDest.Home },
                )
                state.loading && catalog == null -> Box(Modifier.fillMaxSize()) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
                state.error != null && catalog == null -> HubEmptyState(
                    icon = CaIcons.error,
                    title = "Couldn't load the hub",
                    message = state.error ?: "",
                    modifier = Modifier.align(Alignment.Center),
                )
                catalog == null -> HubEmptyState(
                    icon = CaIcons.box,
                    title = "Nothing here yet",
                    message = state.loadDetail
                        ?: "The catalog is empty. Check your sync URL in Settings.",
                    modifier = Modifier.align(Alignment.Center),
                )
                catalog.snippets.isEmpty() && catalog.dependencies.isEmpty() -> HubEmptyState(
                    icon = CaIcons.box,
                    title = "Empty catalog",
                    message = state.loadDetail ?: "The bundled catalog didn't make it into this build.",
                    modifier = Modifier.align(Alignment.Center),
                )
                else -> when (dest) {
                    HubDest.Home -> HomeContent(
                        state = state,
                        snippets = catalog.snippets,
                        onOpenSnippet = onOpenSnippet,
                        onSearch = { dest = HubDest.Search },
                        onOpenCategory = { categoryFilter = it; dest = HubDest.Explore },
                    )
                    HubDest.Explore -> ExploreContent(
                        state = state,
                        category = categoryFilter,
                        onClearCategory = { categoryFilter = null },
                        onOpenSnippet = onOpenSnippet,
                    )
                    HubDest.Search -> SearchContent(
                        state = state,
                        onOpenSnippet = onOpenSnippet,
                    )
                    HubDest.Categories -> CategoriesContent(
                        state = state,
                        onOpenCategory = { categoryFilter = it; dest = HubDest.Explore },
                    )
                    HubDest.Favorites -> FavoritesContent(
                        state = state,
                        onOpenSnippet = onOpenSnippet,
                    )
                    HubDest.Settings -> SettingsContent(
                        state = state,
                        scope = scope,
                    )
                }
            }
        }
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val showLabels = maxWidth >= NAV_LABEL_MIN_WIDTH
            NavigationBar {
                HubDest.entries.forEach { item ->
                    NavigationBarItem(
                        selected = dest == item,
                        onClick = { dest = item },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = if (showLabels) ({ Text(item.label) }) else null,
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeContent(
    state: DevHubState,
    snippets: List<Snippet>,
    onOpenSnippet: (Snippet) -> Unit,
    onSearch: () -> Unit,
    onOpenCategory: (String) -> Unit,
) {
    val featured = snippets.take(5)
    val recent = snippets.sortedByDescending { it.updatedAt }.take(8)
    val categories = state.categories()

    LazyColumn(
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        item {
            SearchBar(onClick = onSearch)
        }
        item {
            SectionTitle("Categories")
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(horizontal = 16.dp),
            ) {
                categories.take(4).forEach { c ->
                    HubChip(label = c.name, onClick = { onOpenCategory(c.name) })
                }
            }
        }
        item { SectionTitle("Featured") }
        items(featured, key = { it.id }) { s ->
            SnippetRow(
                title = s.title,
                description = s.description,
                languages = s.implementations.map { it.language }.distinct(),
                isFavorite = state.isFavorite(s.id),
                onClick = { onOpenSnippet(s) },
                onFavorite = { state.toggleFavorite(s.id) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            )
        }
        item { SectionTitle("Recent additions") }
        items(recent, key = { it.id }) { s ->
            SnippetRow(
                title = s.title,
                description = s.description,
                languages = s.implementations.map { it.language }.distinct(),
                isFavorite = state.isFavorite(s.id),
                onClick = { onOpenSnippet(s) },
                onFavorite = { state.toggleFavorite(s.id) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            )
        }
    }
}

@Composable
private fun SearchBar(onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable(onClick = onClick),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            Icon(CaIcons.search, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.size(10.dp))
            Text(
                "Search snippets, code, dependencies…",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun ExploreContent(
    state: DevHubState,
    category: String?,
    onClearCategory: () -> Unit,
    onOpenSnippet: (Snippet) -> Unit,
) {
    val catalog = state.catalog ?: return
    val visible = if (category != null) catalog.snippets.filter { it.category == category }
    else catalog.snippets

    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    category ?: "All snippets",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )
                if (category != null) {
                    TextButton(onClick = onClearCategory) { Text("Clear") }
                }
            }
            Text(
                "${visible.size} snippet${if (visible.size == 1) "" else "s"}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        items(visible, key = { it.id }) { s ->
            SnippetRow(
                title = s.title,
                description = s.description,
                languages = s.implementations.map { it.language }.distinct(),
                isFavorite = state.isFavorite(s.id),
                onClick = { onOpenSnippet(s) },
                onFavorite = { state.toggleFavorite(s.id) },
                modifier = Modifier.padding(vertical = 4.dp),
            )
        }
    }
}

@Composable
private fun SearchContent(
    state: DevHubState,
    onOpenSnippet: (Snippet) -> Unit,
) {
    var query by remember { mutableStateOf("") }
    var selectedLangs by remember { mutableStateOf(setOf<String>()) }
    var selectedCats by remember { mutableStateOf(setOf<String>()) }
    var results by remember { mutableStateOf<List<SearchHit>>(emptyList()) }
    var searched by remember { mutableStateOf(false) }
    var searching by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val categories = state.categories()

    Column(Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("Search functions, buttons, layouts, dependency ids…") },
            leadingIcon = { Icon(CaIcons.search, null) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        )
        Text(
            "Matches titles, code (function/widget/layout names), tags and dependencies — e.g. \"FilledButton\", \"fadeIn\", \"Retrofit\".",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 0.dp),
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
        ) {
            ALL_LANGUAGE_FILTERS.forEach { lang ->
                HubChip(
                    label = lang,
                    selected = lang in selectedLangs,
                    onClick = {
                        selectedLangs = if (lang in selectedLangs) selectedLangs - lang
                        else selectedLangs + lang
                    },
                )
            }
        }
        if (categories.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                categories.take(8).forEach { c ->
                    HubChip(
                        label = c.name,
                        selected = c.name in selectedCats,
                        onClick = {
                            selectedCats = if (c.name in selectedCats) selectedCats - c.name
                            else selectedCats + c.name
                        },
                    )
                }
            }
        }
        HorizontalDivider()
        LaunchedEffect(query, selectedLangs, selectedCats) {
            searching = true
            val hits = state.search(
                query,
                Filters(languages = selectedLangs, categories = selectedCats),
            )
            results = hits
            searching = false
            searched = true
        }
        Box(Modifier.weight(1f)) {
            when {
                searching -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                results.isEmpty() -> HubEmptyState(
                    icon = CaIcons.search,
                    title = if (query.isBlank() && selectedLangs.isEmpty() && selectedCats.isEmpty())
                        "Search the hub" else "No matches",
                    message = if (query.isBlank() && selectedLangs.isEmpty() && selectedCats.isEmpty())
                        "Type to search titles, code, tags and dependencies."
                    else "Try different keywords or filters.",
                    modifier = Modifier.align(Alignment.Center),
                )
                else -> LazyColumn(contentPadding = PaddingValues(16.dp)) {
                    items(results, key = { it.snippet.id }) { hit ->
                        SnippetRow(
                            title = hit.snippet.title,
                            description = hit.snippet.description,
                            languages = hit.snippet.implementations.map { it.language }.distinct(),
                            isFavorite = state.isFavorite(hit.snippet.id),
                            // The verification aid: why this snippet matched (title/code/tags/dependency).
                            matchHint = "matched in ${hit.matchedIn}",
                            onClick = { onOpenSnippet(hit.snippet) },
                            onFavorite = { state.toggleFavorite(hit.snippet.id) },
                            modifier = Modifier.padding(vertical = 4.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoriesContent(
    state: DevHubState,
    onOpenCategory: (String) -> Unit,
) {
    val categories = state.categories()
    if (categories.isEmpty()) {
        HubEmptyState(
            icon = CaIcons.grid,
            title = "No categories",
            message = "The catalog defines no snippet categories yet.",
        )
        return
    }
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(categories) { c ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                ),
                modifier = Modifier.clickable { onOpenCategory(c.name) },
            ) {
                Column(Modifier.padding(16.dp)) {
                    Icon(CaIcons.grid, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(8.dp))
                    Text(c.name, style = MaterialTheme.typography.titleSmall)
                    Text(
                        "${c.snippetCount} snippets",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoritesContent(
    state: DevHubState,
    onOpenSnippet: (Snippet) -> Unit,
) {
    val favs = remember(state.favoritesEpoch) { state.favorites() }
    if (favs.isEmpty()) {
        HubEmptyState(
            icon = CaIcons.heart,
            title = "No favorites yet",
            message = "Tap the heart on any snippet to pin it here — available offline.",
        )
        return
    }
    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        items(favs, key = { it.id }) { s ->
            SnippetRow(
                title = s.title,
                description = s.description,
                languages = s.implementations.map { it.language }.distinct(),
                isFavorite = true,
                onClick = { onOpenSnippet(s) },
                onFavorite = { state.toggleFavorite(s.id) },
                modifier = Modifier.padding(vertical = 4.dp),
            )
        }
    }
}

@Composable
private fun SettingsContent(
    state: DevHubState,
    scope: kotlinx.coroutines.CoroutineScope,
) {
    var url by remember { mutableStateOf(state.syncUrl) }

    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        item {
            Text("DevHub", style = MaterialTheme.typography.titleLarge)
            Text(
                "On-device snippets & dependency reference. Data is cached locally and stays useful offline.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        item {
            SectionTitle("Remote catalog")
        }
        item {
            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                label = { Text("Sync URL (JSON)") },
                supportingText = {
                    Text("Point this at a hosted devhub-catalog.json; no app update needed for new content.")
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = { state.setSyncUrl(url) }) { Text("Save URL") }
                TextButton(
                    onClick = { scope.launch { state.syncNow() } },
                    enabled = !state.syncing,
                ) { Text(if (state.syncing) "Syncing…" else "Sync now") }
            }
        }
        state.lastSyncMessage?.let { msg ->
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        msg,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp),
                    )
                }
            }
        }
        item {
            SectionTitle("Offline data")
        }
        item {
            InfoRow("Catalog", state.catalog?.snippets?.size?.let { "$it snippets" } ?: "loading…")
            InfoRow("Dependencies", state.catalog?.dependencies?.size?.let { "$it entries" } ?: "—")
            InfoRow("Last sync", state.lastSyncedAt()?.let { relativeTime(it) } ?: "never")
        }
        item {
            SectionTitle("About")
        }
        item {
            InfoRow("Project", "Code Studio Mobile (DevHub)")
            InfoRow("Version", "0.1.0")
            InfoRow("License", "GPL-3.0-or-later")
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        Text(value, style = MaterialTheme.typography.bodySmall)
    }
}

private fun relativeTime(epochMillis: Long): String {
    val diff = System.currentTimeMillis() - epochMillis
    return when {
        diff < 60_000 -> "just now"
        diff < 3_600_000 -> "${diff / 60_000} min ago"
        diff < 86_400_000 -> "${diff / 3_600_000} h ago"
        else -> "${diff / 86_400_000} d ago"
    }
}