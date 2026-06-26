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

// Sample app demonstrating the wrapper: the `kmp-template` Compose Multiplatform starter.
// It is its own standalone Gradle build (own settings.gradle.kts + gradlew) that consumes the
// wrapper exactly like a real consumer would — the published convenience plugin
// (`com.uxcam.kmp.gradle`) and the `com.uxcam:*` artifacts, both resolved from mavenLocal.
//
// We pull it in as a COMPOSITE BUILD rather than `include(...)` so its nested settings.gradle.kts
// (pluginManagement + repositories) is honored. This makes its `:androidApp` show up as a runnable
// Android module when you open `uxcam-kmp` in Android Studio.
//
// PREREQUISITE: publish the wrapper to mavenLocal once before syncing, so the sample can resolve it:
//   ./gradlew :uxcam:publishToMavenLocal :uxcam-kmp-gradle-plugin:publishToMavenLocal
includeBuild("example/kmp-template")
