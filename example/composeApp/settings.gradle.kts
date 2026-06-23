rootProject.name = "composeApp"
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
        // The UXCam KMP wrapper (com.uxcam.kmp:uxcam) and the native debug Android SDK
        // (com.uxcam:uxcam-debug, pulled transitively) are resolved from ~/.m2 — publish
        // them once with `./gradlew :uxcam:publishToMavenLocal` from the repo root.
        // includeGroupAndSubgroups("com.uxcam") covers both com.uxcam and com.uxcam.kmp.
        mavenLocal {
            mavenContent { includeGroupAndSubgroups("com.uxcam") }
        }
        // Fallback for the native UXCam Android SDK (not on Maven Central).
        maven("https://sdk.uxcam.com/android/") {
            mavenContent { includeGroup("com.uxcam") }
        }
    }
}

// Clean Compose Multiplatform starter — a standalone Gradle build, fully independent of
// the main example app. Split into a KMP `shared` library + a thin Android application
// host because AGP 9 forbids combining com.android.application with the Kotlin
// Multiplatform plugin in one module.
include(":shared")
include(":androidApp")
