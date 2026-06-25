import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinCocoapods)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kmmbridgeGithub)
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
    // CocoaPods plugin. The `UXCam` pod is consumed from the published CocoaPods release
    // (the iOS analogue of the published Android SDK). The plugin generates the cinterop
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
            version = "3.8.3"        // pulls from the CocoaPods spec repo
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
            implementation(libs.uxcam.android)
            implementation(libs.uxcam.ktx.android)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

// KMMBridge: publishes the iOS XCFramework so consumers install via SPM or CocoaPods and get
// the native UXCam SDK resolved transitively — no manual `pod 'UXCam'` / version on their side.
//
//  - Binary host: GitHub Releases on the uxcam org repo (githubReleaseArtifacts).
//  - SPM: useCustomPackageFile=true — KMMBridge only rewrites the url/checksum variables inside
//    the marker block of the repo-root Package.swift; that file declares the uxcam-ios-sdk SPM
//    dependency + a deps target (binaryTargets can't carry dependencies). See ../Package.swift.
//  - CocoaPods: the generated podspec auto-includes `spec.dependency 'UXCam', '3.8.3'` from the
//    `pod("UXCam")` declaration in the cocoapods { } block above — nothing extra needed here.
//
// Publishing runs in CI: requires env GITHUB_PUBLISH_TOKEN (and GITHUB_PUBLISH_USER), a macOS
// runner with the Swift/CocoaPods toolchain, and `origin` pointing at github.com/uxcam/uxcam-kmp.
// Bump `version` above per release; KMMBridge tags the repo and creates the matching GH release.
kmmbridge {
    frameworkName.set("UXCamKMP")

    gitHubReleaseArtifacts(repository = "uxcam/uxcam-kmp")

    spm(useCustomPackageFile = true, swiftToolVersion = "5.9") {
        iOS { v("12") }
    }

    // Reuses this repo as the CocoaPods spec repo (per project decision). Swap for a dedicated
    // spec repo, or use cocoapodsTrunk() to publish to the public CocoaPods trunk, if preferred.
    cocoapods("https://github.com/uxcam/uxcam-kmp.git")
}
