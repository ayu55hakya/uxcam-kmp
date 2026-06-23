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
        // The native debug Android SDK (com.uxcam:uxcam-debug, pulled transitively by the
        // wrapper) is resolved from ~/.m2. The wrapper itself (com.uxcam.kmp:uxcam) is NOT
        // resolved here — it is built from source via the composite build below.
        mavenLocal {
            mavenContent { includeGroupAndSubgroups("com.uxcam") }
        }
        // Fallback for the native UXCam Android SDK (not on Maven Central).
        maven("https://sdk.uxcam.com/android/") {
            mavenContent { includeGroup("com.uxcam") }
        }
    }
}

// Build the UXCam KMP wrapper from source instead of consuming a published artifact.
// Gradle substitutes the `com.uxcam.kmp:uxcam` dependency (declared in shared/build.gradle.kts)
// with the root build's `:uxcam` project automatically. This means no publishToMavenLocal /
// cache-refresh dance during local development, and — crucially — the iOS framework always
// links the wrapper's real iOS `actual` (no stale-artifact partial-linkage stubs that crash
// at runtime with IrLinkageError). Only `:uxcam` and its dependencies are configured from the
// included build; the sibling example app is not.
includeBuild("../..")

// Clean Compose Multiplatform starter — a standalone Gradle build, fully independent of
// the main example app. Split into a KMP `shared` library + a thin Android application
// host because AGP 9 forbids combining com.android.application with the Kotlin
// Multiplatform plugin in one module.
include(":shared")
include(":androidApp")
