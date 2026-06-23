import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinCocoapods)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

// Shared Compose Multiplatform UI for the starter. commonMain holds the whole UI (App()
// + screens) and the single UXCam config (UxcamSetup). The Android app and the iOS app are
// thin hosts of this module — App() on Android via MainActivity, MainViewController() on iOS.
kotlin {
    androidLibrary {
        namespace = "com.example.starter.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }

    iosArm64()
    iosSimulatorArm64()

    // The iOS framework is produced via the Kotlin CocoaPods plugin so the SwiftUI app can
    // consume it (and the native UXCam pod) through a Podfile — the standard practice in the
    // UXCam wrapper and the reference KMP SDKs. The Kotlin-side UXCam API comes from the
    // mavenLocal `com.uxcam.kmp:uxcam` dependency below; the native UXCam Obj-C symbols stay
    // undefined in this static framework and resolve at app link via the app's `pod 'UXCam'`.
    cocoapods {
        version = "1.0.0"
        summary = "Shared Compose Multiplatform UI for the UXCam starter"
        homepage = "https://uxcam.com"
        ios.deploymentTarget = "15.0"
        framework {
            // Matches `import ComposeApp` in the iOS app's ContentView.swift.
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            // The UXCam KMP wrapper — used by UxcamSetup to start a session. Pulls the
            // native Android SDK (com.uxcam:uxcam-debug) transitively on Android.
            implementation(libs.uxcam.kmp)
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
