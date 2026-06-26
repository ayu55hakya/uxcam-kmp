rootProject.name = "uxcam-kmp"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        // Resolves the locally-published `com.uxcam.kmp.gradle` convenience plugin (the example
        // shared module applies it). Scoped to our own group so it never shadows other plugins.
        // Run `:uxcam-kmp-gradle-plugin:publishToMavenLocal` once before building the example.
        mavenLocal {
            mavenContent { includeGroupAndSubgroups("com.uxcam") }
        }
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

include(":uxcam")

// Sentry-style convenience Gradle plugin (id `com.uxcam.kmp.gradle`) for Kotlin-source consumers.
// Built here and published to mavenLocal; consumed by the example shared module via this same build.
include(":uxcam-kmp-gradle-plugin")

// Sample app demonstrating the wrapper. Depends on :uxcam as a project (projects.uxcam),
// so no publishToMavenLocal round-trip is needed during development.
// Shared Compose Multiplatform UI + the single source of the app key (UxcamSetup).
// Both the Android app and the iOS app are thin hosts of this module.
include(":example:exampleApp:shared")
include(":example:exampleApp:androidApp")

// NOTE: the clean Compose Multiplatform starter lives at example/composeApp as its own
// standalone Gradle build (own settings.gradle.kts + gradlew), so it is intentionally
// NOT included here.
