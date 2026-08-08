plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

// hub — the DevHub data layer: snippet + dependency catalogs, offline JSON cache, favorites, and
// remote sync. Pure JVM so it runs on desktop and ART alike. The UI lives in :hub-ui; this module
// carries no Compose dependency and no knowledge of the IDE shell.

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.okhttp)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.kotlinx.coroutines.test)
}
