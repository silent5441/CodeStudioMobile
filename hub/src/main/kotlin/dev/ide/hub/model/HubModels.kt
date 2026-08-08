package dev.ide.hub.model

import kotlinx.serialization.Serializable

/** One snippet implementation, e.g. the Button snippet done in Compose vs XML. */
@Serializable
data class SnippetImplementation(
    val language: String,
    val technology: String,
    val code: String,
    val preview: String = "",
)

/**
 * A DevHub snippet. `dependencies` references ids in the catalog's dependency table
 * (groupId:artifactId), so dependency info updates remotely without touching the snippets.
 */
@Serializable
data class Snippet(
    val id: String,
    val title: String,
    val description: String = "",
    val category: String = "",
    val tags: List<String> = emptyList(),
    val dependencies: List<String> = emptyList(),
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    val implementations: List<SnippetImplementation> = emptyList(),
)

/** A dependency entry usable directly in a Gradle build. */
@Serializable
data class DependencyInfo(
    val id: String = "",
    val groupId: String,
    val artifactId: String,
    val latestVersion: String,
    val versions: List<String> = emptyList(),
    val repository: String = "",
    val license: String = "",
    val releaseDate: String = "",
    val updatedAt: Long = 0L,
) {
    companion object {
        fun identifier(groupId: String, artifactId: String) = "$groupId:$artifactId"
    }
}

/** The full remote-catalog envelope: snippets + the dependency table + metadata. */
@Serializable
data class HubCatalog(
    val schema: Int = 1,
    val meta: CatalogMeta = CatalogMeta(),
    val snippets: List<Snippet> = emptyList(),
    val dependencies: List<DependencyInfo> = emptyList(),
)

@Serializable
data class CatalogMeta(
    val title: String = "Code Studio Hub",
    val minAppVersion: Int = 0,
    val source: String = "",
)