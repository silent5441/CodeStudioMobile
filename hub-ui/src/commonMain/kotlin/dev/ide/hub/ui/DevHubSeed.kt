package dev.ide.hub.ui

/**
 * The bundled DevHub seed catalog, compiled into this module as [EMBEDDED_CATALOG_JSON]. A compile-time
 * constant is the only copy guaranteed to survive every Android/compose packaging path (classpath resources
 * and compose-resource assets have both been seen missing on device), so it is the store's import fallback.
 */
object DevHubSeed {
    /** The guaranteed catalog copy. The compose-resource asset is skipped entirely: it has been known to be
     *  dropped/broken on some Android packaging, and the compiled-in constant cannot be. */
    suspend fun text(): String? = EMBEDDED_CATALOG_JSON
}