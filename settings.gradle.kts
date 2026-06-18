rootProject.name = "uxcam-kmp"

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
        // Native UXCam Android SDK is published here, not on Maven Central.
        maven("https://sdk.uxcam.com/android/") {
            mavenContent { includeGroup("com.uxcam") }
        }
    }
}

include(":uxcam")
