rootProject.name = "KmpTemplate"
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
        mavenLocal {
            mavenContent { includeGroupAndSubgroups("com.uxcam") }
        }
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
        mavenLocal {
            mavenContent { includeGroupAndSubgroups("com.uxcam") }
        }
    }
}

// A KMP `shared` library (Compose Multiplatform UI) + a thin Android application host.
// They are separate modules because AGP 9 forbids combining com.android.application with
// the Kotlin Multiplatform plugin in a single module. The iOS app consumes `shared` as a pod.
include(":shared")
include(":androidApp")
