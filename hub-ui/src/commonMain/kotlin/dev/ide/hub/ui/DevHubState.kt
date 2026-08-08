package dev.ide.hub.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.ide.hub.Filters
import dev.ide.hub.HubStore
import dev.ide.hub.SearchHit
import dev.ide.hub.model.HubCatalog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * The DevHub UI state holder. Hosts a [HubStore], remembers the loaded [HubCatalog] plus load/error
 * states, and funnels favorite/sync actions so every screen sees the same data.
 */
class DevHubState(private val store: HubStore) {

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

    suspend fun load() {
        loading = true
        error = null
        try {
            catalog = withContext(Dispatchers.IO) { store.catalog() }
        } catch (e: Exception) {
            error = e.message ?: "Failed to load catalog"
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
}