import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinCocoapods)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    id("com.uxcam.kmp.gradle") version "0.2.4"
}

// Shared Compose Multiplatform UI. commonMain holds the whole UI (App() + the expect/actual
// Platform info). The Android app and the iOS app are thin hosts of this module — App() on
// Android via MainActivity, MainViewController() on iOS.
kotlin {
    androidLibrary {
        namespace = "com.example.template.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }

    iosArm64()
    iosSimulatorArm64()

    // The iOS framework is produced via the Kotlin CocoaPods plugin so the SwiftUI app can
    // consume it through a Podfile — the standard practice for KMP + Compose Multiplatform.
    cocoapods {
        version = "1.0.0"
        summary = "Shared Compose Multiplatform UI for the KMP template"
        homepage = "https://example.com"
        ios.deploymentTarget = "15.0"
        framework {
            // Matches `import ComposeApp` in the iOS app's ContentView.swift.
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }
        androidMain.dependencies {
            // For the image picker (rememberLauncherForActivityResult) in ImagePicker.android.kt,
            // used by the Session Controls screen to background the app.
            implementation(libs.androidx.activity.compose)
            // Preview tooling is Android/JVM-only — keep it out of commonMain so the
            // iOS native targets don't try (and fail) to resolve a non-existent variant.
            implementation(libs.compose.uiToolingPreview)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
