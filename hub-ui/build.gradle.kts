import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// hub-ui — the DevHub UI: a self-contained Compose Multiplatform feature (home/explore/search/
// categories/favorites/settings + snippet & dependency detail) driven entirely by the :hub data layer.
// Mirrors agent-ui's desktop (JVM) + Android KMP setup; commonMain is shared.

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kmp.library)
    alias(libs.plugins.compose)
    alias(libs.plugins.kotlin.compose)
}

kotlin {
    jvm("desktop") {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    android {
        namespace = "dev.ide.hub.ui"
        compileSdk = 36
        minSdk = 24
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":hub"))
            // CaIcons (ide-ui-api) — shared icon set without pulling the IDE shell.
            implementation(project(":ide-ui-api"))
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}