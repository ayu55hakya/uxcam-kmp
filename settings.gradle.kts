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
        // Local debug build of the native SDK (com.uxcam:uxcam-debug etc.), published
        // to ~/.m2 via the android-sdk repo's publishDebugToMavenLocal. Checked first
        // so the local -debug artifacts resolve before the remote repo.
        mavenLocal {
            mavenContent { includeGroupAndSubgroups("com.uxcam") }
        }
        // Native UXCam Android SDK releases are published here, not on Maven Central.
        maven("https://sdk.uxcam.com/android/") {
            mavenContent { includeGroup("com.uxcam") }
        }
    }
}

include(":uxcam")
