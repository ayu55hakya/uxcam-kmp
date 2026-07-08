import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinCocoapods)
    alias(libs.plugins.androidMultiplatformLibrary)
    `maven-publish`
}

// Coordinate used by consumers. Distinct from the native SDK's `com.uxcam:uxcam`
// so the two never collide. The Kotlin Multiplatform plugin auto-creates the
// publications (one per target + shared metadata) from this group/version when
// `maven-publish` is applied — `publishToMavenLocal` writes them to ~/.m2.
// Single source of truth for the released version: `uxcamKmpVersion` in the root
// gradle.properties. The plugin module and the version the plugin installs into consumers
// read the same value, so a bump there moves everything together.
val uxcamKmpVersion = providers.gradleProperty("uxcamKmpVersion").get()

group = "com.uxcam.kmp"
version = uxcamKmpVersion

kotlin {
    // `expect`/`actual` objects are stable enough to use; this flag suppresses the
    // Beta advisory warning the compiler emits for them.
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    iosArm64()
    iosSimulatorArm64()
    // Intel-Mac simulator. The native UXCam SDK's simulator slice is a fat arm64+x86_64 binary,
    // so the cinterop binds against the x86_64 architecture it already ships.
    iosX64()

    // UXCam ships no native SDK for desktop or web, but consumers with 4-target KMP apps call the
    // wrapper from commonMain, so these targets must resolve. They bind to the no-op `noopMain`
    // actuals (wired in sourceSets below) — every call compiles and does nothing.
    jvm()

    // Desktop-native targets: no native SDK either, but a consumer KMP repo declaring them must
    // still resolve this dependency from commonMain — without these variants the plugin's
    // auto-added dependency fails their whole build at resolution. Safe alongside the CocoaPods
    // plugin (it only touches Apple families). macOS/tvOS/watchOS need the cinterop moved off
    // pod("UXCam") first — the UXCam pod is iOS-only, so declaring those families here would
    // break the CocoaPods integration.
    linuxX64()
    mingwX64()

    // Legacy JS/IR (JavaScript) target. Distinct from wasmJs below: a consumer that declares
    // `js(IR)` needs a matching `js` artifact to resolve the wrapper from commonMain, and wasmJs
    // does NOT satisfy that. Binds to the same no-op actuals as the other non-native targets.
    js(IR) {
        browser()
        nodejs()
    }

    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    // The iOS actuals (iosMain/UXCam.ios.kt) bind to the native iOS UXCam SDK via the
    // CocoaPods plugin. The `UXCam` pod is consumed from the published CocoaPods release
    // (the iOS analogue of the published Android SDK). The plugin generates the cinterop
    // binding (Kotlin package `cocoapods.UXCam`) and a `uxcam.podspec` for this module
    // that the SwiftUI sample consumes.
    cocoapods {
        version = uxcamKmpVersion
        summary = "UXCam" +
                " KMP wrapper"
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

    // Adding the manual noop dependsOn edges below disables KGP's automatic default hierarchy, so
    // re-apply it explicitly — otherwise iosMain/androidMain lose their edge to commonMain and the
    // expect/actual matcher fails ("Expected ... has no actual declaration for Native").
    applyDefaultHierarchyTemplate()

    sourceSets {
        // Intermediate no-op source set shared by the targets UXCam has no native SDK for —
        // they get the no-op UXCamKMP actual from here.
        val noopMain by creating { dependsOn(commonMain.get()) }
        val jvmMain by getting { dependsOn(noopMain) }
        val jsMain by getting { dependsOn(noopMain) }
        val wasmJsMain by getting { dependsOn(noopMain) }
        val linuxMain by getting { dependsOn(noopMain) }
        val mingwMain by getting { dependsOn(noopMain) }

        // commonMain is stdlib-only. The Compose occlusion Modifier (and its compose-runtime/ui
        // dependencies) lives in :uxcam-compose so non-Compose consumers stay Compose-free.
        androidMain.dependencies {
            implementation(libs.uxcam.android)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

