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

// Sample app demonstrating the wrapper. Depends on :uxcam as a project (projects.uxcam),
// so no publishToMavenLocal round-trip is needed during development.
// Shared Compose Multiplatform UI + the single source of the app key (UxcamSetup).
// Both the Android app and the iOS app are thin hosts of this module.
include(":example:exampleApp:shared")
include(":example:exampleApp:androidApp")

// NOTE: the clean Compose Multiplatform starter lives at example/composeApp as its own
// standalone Gradle build (own settings.gradle.kts + gradlew), so it is intentionally
// NOT included here.
