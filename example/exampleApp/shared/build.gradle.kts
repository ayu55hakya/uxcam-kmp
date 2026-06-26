import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinCocoapods)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    // Convenience plugin: auto-injects the native `pod("UXCam")` below. Resolved from mavenLocal
    // (run `:uxcam-kmp-gradle-plugin:publishToMavenLocal` first). Demonstrates the iOS CocoaPods
    // auto-install for Kotlin-source consumers.
    alias(libs.plugins.uxcamKmpGradle)
}

// commonMain auto-install is disabled because this in-repo sample consumes the wrapper as a project
// dependency (`api(projects.uxcam)` below) — a composite build can't pull its own not-yet-published
// Maven artifact. External consumers leave it enabled to get `com.uxcam.kmp:uxcam` for free. The
// plugin still injects the native `pod("UXCam")` for the iOS framework.
uxcamKmp {
    autoInstall {
        commonMain { enabled.set(false) }
    }
}

// Shared Compose Multiplatform UI for the sample. commonMain holds the whole UI (App()
// + screens) and the single runtime source of the app key (UxcamSetup). Both the Android
// app and the iOS app are thin hosts of this module — App() on Android, MainViewController
// on iOS — so there is exactly one UI and one place the UXCam key lives.
kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    androidLibrary {
        namespace = "com.example.testapp.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
        androidResources {
            enable = true
        }
        withHostTest {
            isIncludeAndroidResources = true
        }
    }

    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        version = "0.0.1"
        summary = "Example shared Compose Multiplatform UI"
        homepage = "https://uxcam.com"
        ios.deploymentTarget = "15.0"
        framework {
            baseName = "Shared"
            isStatic = true
            // Re-export the :uxcam wrapper so the native SwiftUI host (UX.swift) sees the
            // UXCamKMP / UXConfig types through this single `Shared` framework.
            export(projects.uxcam)
        }
        // The native `pod("UXCam")` this module's iOS framework needs (the shared UI calls the
        // :uxcam wrapper, which cinterops the native UXCam pod) is injected automatically by the
        // `com.uxcam.kmp.gradle` plugin applied above — no manual `pod("UXCam")` declaration needed.
    }

    sourceSets {
        commonMain.dependencies {
            // The UXCam KMP wrapper — used by UxcamSetup and the occlusion screens.
            // `api` (not `implementation`) so the framework `export(projects.uxcam)` above
            // can surface UXCamKMP / UXConfig in the generated Obj-C headers for Swift.
            api(projects.uxcam)
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }
        androidMain.dependencies {
            // For the image picker (rememberLauncherForActivityResult) in ImagePicker.android.kt.
            implementation(libs.androidx.activity.compose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}
