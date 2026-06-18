rootProject.name = "Testapp"
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
        // From ~/.m2: the uxcam-kmp wrapper (com.uxcam.kmp) AND the local debug native
        // SDK it currently depends on (com.uxcam:*-debug). "com.uxcam" covers both, since
        // com.uxcam.kmp is a subgroup of it. Run `./gradlew publishToMavenLocal` in
        // ../uxcam-kmp after each change.
        // For a released native SDK instead, narrow this to "com.uxcam.kmp" and add:
        //   maven("https://sdk.uxcam.com/android/") { mavenContent { includeGroup("com.uxcam") } }
        mavenLocal {
            mavenContent { includeGroupAndSubgroups("com.uxcam") }
        }
    }
}

include(":androidApp")
include(":shared")