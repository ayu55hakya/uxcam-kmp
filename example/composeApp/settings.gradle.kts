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
    }
}

// Clean Compose Multiplatform starter — a standalone Gradle build, fully independent of
// the main example app. Split into a KMP `shared` library + a thin Android application
// host because AGP 9 forbids combining com.android.application with the Kotlin
// Multiplatform plugin in one module.
include(":shared")
include(":androidApp")
