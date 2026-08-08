package dev.ide.hub.ui

import org.jetbrains.compose.resources.Res
import org.jetbrains.compose.resources.readBytes

/**
 * The bundled DevHub seed catalog, shipped as a Compose resource of :hub-ui. Compose resources are packaged
 * reliably on both the Android and desktop targets (which the :hub JVM module's classpath resource is not
 * always), so this is the fallback the store imports when its classpath copy is missing. Read lazily.
 */
object DevHubSeed {
    suspend fun text(): String? = runCatching {
        Res.readBytes("files/hub-seed-catalog.json").decodeToString()
    }.getOrNull()
}