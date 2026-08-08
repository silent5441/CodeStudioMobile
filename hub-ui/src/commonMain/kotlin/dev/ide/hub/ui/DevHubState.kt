package dev.ide.hub.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.ide.hub.Filters
import dev.ide.hub.HubStore
import dev.ide.hub.SearchHit
import dev.ide.hub.model.DependencyInfo
import dev.ide.hub.model.HubCatalog
import dev.ide.hub.model.Snippet
import dev.ide.hub.model.SnippetImplementation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * The DevHub UI state holder. Hosts a [HubStore], remembers the loaded [HubCatalog] plus load/error
 * states, and funnels favorite/sync actions so every screen sees the same data. When the store's own
 * offline seed is missing (a platform packaging gap), [seedProvider] supplies the bundled catalog text
 * and [load] imports it before the first read.
 */
class DevHubState(
    private val store: HubStore,
    private val seedProvider: (suspend () -> String?)? = null,
) {

    var catalog by mutableStateOf<HubCatalog?>(null)
        private set

    var loading by mutableStateOf(true)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    /** Bumped on every favorite change; screens that display favorites key off it. */
    var favoritesEpoch by mutableStateOf(0L)
        private set

    var syncing by mutableStateOf(false)
        private set

    var lastSyncMessage by mutableStateOf<String?>(null)
        private set

    /** Human-readable detail of the latest load attempt (which fallback ran / why it stayed empty). */
    var loadDetail by mutableStateOf<String?>(null)
        private set

    suspend fun load() {
        loading = true
        error = null
        try {
            var c = withContext(Dispatchers.IO) { store.catalog() }
            if (c.snippets.isEmpty() && c.dependencies.isEmpty()) {
                val seed = seedProvider?.invoke()
                if (seed != null) {
                    withContext(Dispatchers.IO) { store.importSeed(seed) }
                    c = withContext(Dispatchers.IO) { store.catalog() }
                }
                loadDetail = when {
                    c.snippets.isNotEmpty() || c.dependencies.isNotEmpty() ->
                        "(re-seeded from the bundled catalog: ${c.snippets.size} snippets, ${c.dependencies.size} deps)"
                    seed != null ->
                        "still empty after importing the bundled catalog seed [${store.lastImportDetail ?: "import never ran"}]"
                    else -> "no bundled seed available to import"
                }
            }
            catalog = c
        } catch (e: Exception) {
            error = e.message ?: "Failed to load catalog"
            loadDetail = "load() threw: ${e::class.simpleName}"
        } finally {
            loading = false
        }
    }

    /** Pull the remote catalog; on success the catalog state is refreshed in place. */
    suspend fun syncNow() {
        syncing = true
        lastSyncMessage = null
        try {
            val result = store.syncNow()
            lastSyncMessage = if (result.success) result.message else "Sync failed: ${result.message}"
            if (result.success) load()
        } catch (e: Exception) {
            lastSyncMessage = "Sync failed: ${e.message ?: "error"}"
        } finally {
            syncing = false
        }
    }

    fun isFavorite(id: String) = store.isFavorite(id)

    fun toggleFavorite(id: String) {
        store.toggleFavorite(id)
        favoritesEpoch++
    }

    fun favorites() = store.favorites()

    fun dependency(id: String) = store.dependency(id)

    fun categories() = store.categories()

    val syncUrl: String get() = store.syncUrl

    fun setSyncUrl(url: String) = store.setSyncUrl(url)

    fun lastSyncedAt(): Long? = store.lastSyncedAt()

    suspend fun search(query: String, filters: Filters): List<SearchHit> = store.search(query, filters)

    /** Merge a snippet authored on-device into the catalog (new dependency optional) and refresh the UI.
     *  Returns the assigned snippet id, or null when the merge failed. */
    suspend fun addLocalSnippet(
        title: String,
        description: String,
        category: String,
        tags: List<String>,
        language: String,
        technology: String,
        code: String,
        dependency: String,
    ): Boolean {
        val depIds = mutableListOf<String>()
        val extraDeps = mutableListOf<DependencyInfo>()
        val dep = dependency.trim()
        if (dep.isNotBlank()) {
            val parts = dep.split(":", limit = 3)
            if (parts.size >= 2) {
                val (g, a) = parts
                val v = parts.getOrElse(2) { "latest" }
                depIds += "$g:$a"
                extraDeps += DependencyInfo(
                    groupId = g.trim(),
                    artifactId = a.trim(),
                    latestVersion = v.trim(),
                    versions = listOf(v.trim()),
                )
            }
        }
        val slug = title.trim().lowercase()
            .replace(Regex("[^a-z0-9]+"), "-")
            .trim('-')
            .ifBlank { "snippet" }
        val id = "$slug-${System.currentTimeMillis() % 1000}"
        val snippet = Snippet(
            id = id,
            title = title.trim(),
            description = description.trim(),
            category = category.trim().ifBlank { "General" },
            tags = tags.map { it.trim() }.filter { it.isNotEmpty() },
            dependencies = depIds,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            implementations = listOf(
                SnippetImplementation(
                    language = language,
                    technology = technology.ifBlank { language.replaceFirstChar { it.uppercase() } },
                    code = code.trim(),
                ),
            ),
        )
        return runCatching {
            withContext(Dispatchers.IO) {
                store.addSnippet(snippet, extraDeps)
                catalog = store.catalog()
            }
            true
        }.getOrDefault(false)
    }
}