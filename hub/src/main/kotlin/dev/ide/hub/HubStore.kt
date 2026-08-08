package dev.ide.hub

import dev.ide.hub.model.DependencyInfo
import dev.ide.hub.model.HubCatalog
import dev.ide.hub.model.Snippet

/** A category with the number of snippets in it (for the Categories grid). */
data class CategoryCount(
    val name: String,
    val snippetCount: Int,
)

/** A language/technology filter set used by global search. */
data class Filters(
    val languages: Set<String> = emptySet(),  // kotlin, java, compose, xml
    val categories: Set<String> = emptySet(), // UI, Networking, Database, ...
)

/** Result of a global search, with what was matched for highlighting/beyond-matching UX. */
data class SearchHit(
    val snippet: Snippet,
    val matchedIn: String,
)

/** Outcome of one remote-sync attempt. */
data class SyncResult(
    val success: Boolean,
    val message: String,
    val applied: Int = 0,
)

/**
 * The DevHub data store: serves the merged snippet + dependency catalog (seeded offline, refreshed from
 * a remote catalog URL when online), persists favorites locally, and searches everything. Host-agnostic:
 * implementers receive a base directory (Android: app files dir; desktop: user home).
 */
interface HubStore {
    /** The current catalog: offline seed merged with anything pulled from the remote sync URL. */
    suspend fun catalog(): HubCatalog

    /** Global search across title, description, code, tags, category, technology and dependency names. */
    suspend fun search(query: String, filters: Filters): List<SearchHit>

    fun dependency(id: String): DependencyInfo?

    /** Every snippet category, with an item count (for the Categories grid). */
    fun categories(): List<CategoryCount>

    /** Snippets marked as favorite in the preferred order. */
    fun favorites(): List<Snippet>

    fun isFavorite(snippetId: String): Boolean

    fun toggleFavorite(snippetId: String)

    /** The remote catalog URL (user-editable; empty disables sync). */
    val syncUrl: String

    fun setSyncUrl(url: String)

    /** Timestamp of the last successful sync, or null if never synced. */
    fun lastSyncedAt(): Long?

    /** Pull the remote catalog and merge it into the local cache. Returns true when remote data is present. */
    suspend fun syncNow(): SyncResult

    /** Adopt an externally-provided catalog (seed fallback), persisting it for offline use. */
    fun importSeed(text: String)
}