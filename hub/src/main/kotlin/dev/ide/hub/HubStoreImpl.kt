package dev.ide.hub

import dev.ide.hub.model.DependencyInfo
import dev.ide.hub.model.HubCatalog
import dev.ide.hub.model.Snippet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * File-backed [HubStore]. The catalog is seeded from the bundled resource on first run, cached in
 * [dir]/catalog.json, and optionally refreshed from a remote URL ([syncUrl]). Favorites and settings are
 * small sibling JSON files, so DevHub is fully functional offline.
 */
class HubStoreImpl(
    private val dir: File,
    private val seedResource: String = "hub-seed-catalog.json",
) : HubStore {

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = false
    }

    private val http = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    @Volatile
    private var cached: HubCatalog? = null

    @Volatile
    private var remote = false

    @Volatile
    private var syncUrlValue: String = DEFAULT_SYNC_URL

    @Volatile
    private var lastSync: Long? = null

    private val favoritesIds: MutableSet<String> = linkedSetOf()

    @Serializable
    private data class Settings(
        val syncUrl: String = DEFAULT_SYNC_URL,
        val lastSync: Long? = null,
    )

    init {
        readSettingsFile().forEach { (k, v) ->
            when (k) {
                "syncUrl" -> syncUrlValue = v
                "lastSync" -> lastSync = v.toLongOrNull()
            }
        }
        readFavoritesFile()
    }

    private fun settingsFile() = File(dir, "settings.json")

    private fun readSettingsFile(): Map<String, String> {
        val f = settingsFile()
        if (!f.exists()) return emptyMap()
        return runCatching {
            val s = json.decodeFromString<Settings>(f.readText())
            mapOf("syncUrl" to s.syncUrl, "lastSync" to (s.lastSync?.toString() ?: ""))
        }.getOrDefault(emptyMap())
    }

    private fun favoritesFile() = File(dir, "favorites.json")

    private fun readFavoritesFile() {
        val f = favoritesFile()
        if (!f.exists()) return
        runCatching { json.decodeFromString<Set<String>>(f.readText()) }
            .getOrDefault(emptySet())
            .forEach { favoritesIds.add(it) }
    }

    private fun catalogFile() = File(dir, "catalog.json")

    override suspend fun catalog(): HubCatalog = withContext(Dispatchers.IO) {
        val existing = cached
        if (existing != null) return@withContext existing
        synchronized(this@HubStoreImpl) {
            cached?.let { return@synchronized it }
            val fromDisk = runCatching {
                val f = catalogFile()
                if (f.exists()) json.decodeFromString<HubCatalog>(f.readText()) else null
            }.getOrNull()
            // A persisted catalog with content wins; a stale *empty* one (e.g. a seed-import failure from an
            // older build) is ignored so a corrected seed can take over again.
            if (fromDisk != null && (fromDisk.snippets.isNotEmpty() || fromDisk.dependencies.isNotEmpty())) {
                cached = fromDisk
                return@synchronized fromDisk
            }
            val seeded = loadSeed()
            if (seeded != null) {
                cached = seeded
                persistCatalog(seeded)
                return@synchronized seeded
            }
            // Nothing to show yet (and never persisted, so a later seed/import can still take over).
            val empty = HubCatalog()
            cached = empty
            empty
        }
    }

    private fun loadSeed(): HubCatalog? = try {
        javaClass.classLoader?.getResourceAsStream(seedResource)?.use { stream ->
            runCatching { json.decodeFromString<HubCatalog>(stream.readBytes().decodeToString()) }.getOrNull()
        }
    } catch (e: Exception) {
        null
    }

    /** Adopt an externally-provided seed (e.g. the UI module's bundled Compose resource) when the classpath
     *  copy is missing/unreadable on a given platform. No-op when the text is unusable. */
    override var lastImportDetail: String? = null

    override fun importSeed(text: String) {
        val (catalog, detail) = try {
            val c = json.decodeFromString<HubCatalog>(text)
            if (c.snippets.isEmpty() && c.dependencies.isEmpty()) {
                null to "decoded to an empty catalog (schema mismatch?): ${text.length} chars in"
            } else {
                c to "decoded ok: ${c.snippets.size} snippets, ${c.dependencies.size} deps (${text.length} chars)"
            }
        } catch (e: Exception) {
            null to "decode threw ${e::class.simpleName}: ${e.message}"
        }
        lastImportDetail = detail
        if (catalog == null) return
        synchronized(this) {
            cached = catalog
            runCatching { persistCatalog(catalog) }
                .getOrElse { lastImportDetail = detail + "; persist failed: ${it.message}" }
        }
    }

    override fun addSnippet(snippet: Snippet, extraDependencies: List<DependencyInfo>) {
        synchronized(this) {
            val current = cached ?: HubCatalog()
            val merged = HubCatalog(
                schema = current.schema,
                meta = current.meta,
                snippets = current.snippets.filterNot { it.id == snippet.id } + snippet,
                dependencies = current.dependencies + extraDependencies.filterNot { dep ->
                    current.dependencies.any { it.groupId == dep.groupId && it.artifactId == dep.artifactId }
                },
            )
            cached = merged
            persistCatalog(merged)
        }
    }

    private fun persistCatalog(catalog: HubCatalog) {
        dir.mkdirs()
        catalogFile().writeText(json.encodeToString(HubCatalog.serializer(), catalog))
    }

    override suspend fun search(query: String, filters: Filters): List<SearchHit> =
        withContext(Dispatchers.Default) {
            val cat = catalog().snippets

            val deps = catalog().dependencies.associateBy {
                DependencyInfo.identifier(it.groupId, it.artifactId)
            }

            val q = query.trim().lowercase()

            cat.asSequence()
                .filter { s -> filters.categories.isEmpty() || s.category in filters.categories }
                .filter { s ->
                    filters.languages.isEmpty() ||
                        s.implementations.any { it.language in filters.languages }
                }
                .mapNotNull { snippet ->
                    if (q.isEmpty()) {
                        return@mapNotNull SearchHit(snippet, "all")
                    }
                    if (titleMatches(snippet, q)) return@mapNotNull SearchHit(snippet, "title")
                    if (tagMatches(snippet, q)) return@mapNotNull SearchHit(snippet, "tags")
                    if (codeMatches(snippet, q)) return@mapNotNull SearchHit(snippet, "code")
                    val depId = snippet.dependencies.firstOrNull { depIdentifier ->
                        deps[depIdentifier]?.let {
                            it.artifactId.lowercase().contains(q) || it.groupId.lowercase().contains(q)
                        } == true
                    }
                    if (depId != null) {
                        return@mapNotNull SearchHit(snippet, "dependency ${deps[depId]?.artifactId ?: depId}")
                    }
                    null
                }
                .toList()
        }

    private fun titleMatches(s: Snippet, q: String): Boolean =
        s.title.lowercase().contains(q) || s.description.lowercase().contains(q)

    private fun tagMatches(s: Snippet, q: String): Boolean =
        s.tags.any { it.lowercase().contains(q) } || s.category.lowercase().contains(q)

    private fun codeMatches(s: Snippet, q: String): Boolean =
        s.implementations.any { im ->
            im.code.lowercase().contains(q) || im.technology.lowercase().contains(q)
        }

    override fun dependency(id: String): DependencyInfo? =
        cached?.dependencies?.firstOrNull {
            DependencyInfo.identifier(it.groupId, it.artifactId) == id
        }

    override fun categories(): List<CategoryCount> {
        val snapshot = cached ?: return emptyList()
        return snapshot.snippets.groupBy { it.category }.entries.sortedBy { it.key.lowercase() }
            .map { CategoryCount(it.key, it.value.size) }
    }

    override fun favorites(): List<Snippet> {
        val snapshot = cached ?: return emptyList()
        return snapshot.snippets.filter { it.id in favoritesIds }
    }

    override fun isFavorite(snippetId: String): Boolean = snippetId in favoritesIds

    override fun toggleFavorite(snippetId: String) {
        if (favoritesIds.add(snippetId)) {
            persistFavorites()
        } else {
            favoritesIds.remove(snippetId)
            persistFavorites()
        }
    }

    private fun persistFavorites() {
        dir.mkdirs()
        favoritesFile().writeText(json.encodeToString(favoritesIds.sorted()))
    }

    override val syncUrl: String get() = syncUrlValue

    override fun setSyncUrl(url: String) {
        syncUrlValue = url.trim()
        persistSettings()
    }

    private fun persistSettings() {
        dir.mkdirs()
        settingsFile().writeText(
            json.encodeToString(Settings(syncUrlValue, lastSync)),
        )
    }

    override fun lastSyncedAt(): Long? = lastSync

    override suspend fun syncNow(): SyncResult = withContext(Dispatchers.IO) {
        val url = syncUrlValue
        if (url.isBlank()) {
            return@withContext SyncResult(false, "No sync URL configured")
        }
        try {
            val request = Request.Builder().url(url).header("Accept", "application/json").build()
            http.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    SyncResult(false, "HTTP ${response.code}")
                } else {
                    val body = response.body?.string()
                    if (body.isNullOrBlank()) {
                        SyncResult(false, "Empty response")
                    } else {
                        val remote = runCatching { json.decodeFromString<HubCatalog>(body) }
                            .getOrElse { return@use SyncResult(false, "Invalid catalog JSON") }
                        if (remote.schema > SCHEMA_VERSION) {
                            SyncResult(false, "Catalog schema ${remote.schema} is newer than this build")
                        } else {
                            mergeRemote(remote)
                            SyncResult(true, "Catalog updated (${remote.snippets.size} snippets)")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            SyncResult(false, e.message ?: "Network error")
        }
    }

    private fun mergeRemote(remoteCatalog: HubCatalog) {
        val currentById = cached?.snippets ?: run {
            val fromDisk = runCatching {
                val f = catalogFile()
                if (f.exists()) json.decodeFromString<HubCatalog>(f.readText()).snippets else emptyList()
            }.getOrDefault(emptyList())
            fromDisk
        }
        val remoteById = remoteCatalog.snippets.associateBy { it.id }
        val currentBy = currentById.associateBy { it.id }
        val merged = (currentBy + remoteById)
        val ordered = currentById.mapNotNull { merged[it.id] } +
            remoteById.values.filter { it.id !in currentBy }

        val catalog = HubCatalog(
            schema = remoteCatalog.schema,
            meta = remoteCatalog.meta,
            snippets = ordered,
            dependencies = if (remoteCatalog.dependencies.isNotEmpty()) remoteCatalog.dependencies else (cached?.dependencies ?: emptyList()),
        )
        cached = catalog
        remote = true
        persistCatalog(catalog)
        lastSync = System.currentTimeMillis()
        persistSettings()
    }

    fun hasRemoteData(): Boolean = remote || lastSync != null

    companion object {
        const val DEFAULT_SYNC_URL = ""
        const val SCHEMA_VERSION = 1
    }
}