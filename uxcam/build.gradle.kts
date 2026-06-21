import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinCocoapods)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    `maven-publish`
}

// Coordinate used by consumers. Distinct from the native SDK's `com.uxcam:uxcam`
// so the two never collide. The Kotlin Multiplatform plugin auto-creates the
// publications (one per target + shared metadata) from this group/version when
// `maven-publish` is applied — `publishToMavenLocal` writes them to ~/.m2.
group = "com.uxcam.kmp"
version = "0.0.1"

kotlin {
    // `expect`/`actual` objects are stable enough to use; this flag suppresses the
    // Beta advisory warning the compiler emits for them.
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    iosArm64()
    iosSimulatorArm64()

    // The iOS actuals (iosMain/UXCam.ios.kt) bind to the native iOS UXCam SDK via the
    // CocoaPods plugin. The `UXCam` pod is consumed from a locally-built xcframework
    // (uxcam/localpods/UXCam, gitignored) — built from ~/Documents/ios-framework, the
    // iOS analogue of the Android local-debug SDK. The plugin generates the cinterop
    // binding (Kotlin package `cocoapods.UXCam`) and a `uxcam.podspec` for this module
    // that the SwiftUI sample consumes.
    cocoapods {
        version = "0.0.1"
        summary = "UXCam KMP wrapper"
        homepage = "https://uxcam.com"
        ios.deploymentTarget = "12.0"
        framework {
            baseName = "UXCamKMP"
            isStatic = true
        }
        pod("UXCam") {
            source = path(file("localpods/UXCam"))
            moduleName = "UXCam"
        }
    }

    androidLibrary {
        namespace = "com.uxcam.kmp"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }

    sourceSets {
        commonMain.dependencies {
            // Compose is needed for the occlusion Modifier API (Modifier.uxcamOcclude).
            implementation(libs.compose.runtime)
            implementation(libs.compose.ui)
        }
        androidMain.dependencies {
            // The real native Android SDK — only visible to the Android target.
            // It self-initializes its context via its own UXCamContentProvider,
            // so no startup/context plumbing is needed here.
            implementation(libs.uxcam.android)
            // Compose occlusion helper (UXCamKt.occludeSensitiveComposable).
            implementation(libs.uxcam.ktx.android)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
