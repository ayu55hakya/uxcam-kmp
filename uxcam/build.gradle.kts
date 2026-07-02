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
        // Intermediate no-op source set shared by the targets UXCam has no native SDK for.
        // jvm (desktop) and wasmJs (web) get the no-op UXCamKMP/Occlusion actuals from here.
        val noopMain by creating { dependsOn(commonMain.get()) }
        val jvmMain by getting { dependsOn(noopMain) }
        val jsMain by getting { dependsOn(noopMain) }
        val wasmJsMain by getting { dependsOn(noopMain) }

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

// KMMBridge: publishes the iOS XCFramework so consumers install via SPM and get the native
// UXCam SDK resolved transitively — no manual `pod 'UXCam'` / version on their side.
//
// SPM-only by design. CocoaPods is in maintenance mode (no active development since 2024) and
// SPM is Apple's actively-maintained, Xcode-native manager, so it's the primary distribution.
// CocoaPods publishing can be re-added later (a `cocoapods(<specRepoUrl>)` line below) with no
// permanence cost — unlike CocoaPods trunk, nothing here is a one-way door.
//
// NOTE: this is distinct from the kotlin `cocoapods { }` block above — that uses the
// kotlinCocoapods plugin at BUILD time to cinterop-bind the native UXCam SDK and must stay;
// it has nothing to do with how the wrapper is DISTRIBUTED.
//
//  - Binary host: GitHub Releases on the repo named below (gitHubReleaseArtifacts). Currently
//    ayu55hakya/uxcam-kmp (matches `origin`); move to uxcam/uxcam-kmp later by changing the
//    repository param + pushing there. Must match `origin` so the tag push and the Release land
//    in the same repo the default GITHUB_TOKEN can write to.
//  - SPM: useCustomPackageFile=true — KMMBridge only rewrites the url/checksum variables inside
//    the marker block of the repo-root Package.swift; that file declares the uxcam-ios-sdk SPM
//    dependency + a deps target (binaryTargets can't carry dependencies). See ../Package.swift.
//
// Publishing (runs in CI on a macOS runner with the Swift toolchain). The publish task is
// GATED and inert during normal builds/IDE sync — it only registers when ENABLE_PUBLISHING=true.
// Both inputs below must reach Gradle as PROJECT PROPERTIES, not plain env vars: the token is
// read via `project.property("GITHUB_PUBLISH_TOKEN")`, which throws if it's only an env var.
//   - ENABLE_PUBLISHING=true        — registers the `kmmBridgePublish` task (default: false)
//   - GITHUB_PUBLISH_TOKEN          — GH token with write access to the repo below (releases)
// Inject the secret as a Gradle property via the ORG_GRADLE_PROJECT_ env prefix, e.g.:
//   ORG_GRADLE_PROJECT_GITHUB_PUBLISH_TOKEN=$TOKEN ./gradlew :uxcam:kmmBridgePublish -PENABLE_PUBLISHING=true
// (GITHUB_PUBLISH_USER is optional, defaults to "cirunner"; the repo comes from the
// gitHubReleaseArtifacts(repository = ...) param below, so GITHUB_REPO is not needed.)
// Bump `version` above per release; KMMBridge tags the repo and creates the matching GH release.
// See .github/workflows/publish-ios.yml.
kmmbridge {
    frameworkName.set("UXCamKMP")

    // Must match `origin` (where Actions runs + the default GITHUB_TOKEN has write access).
    // Change to "uxcam/uxcam-kmp" when the canonical home moves to the org repo.
    gitHubReleaseArtifacts(repository = "ayu55hakya/uxcam-kmp")

    spm(useCustomPackageFile = true, swiftToolVersion = "5.9") {
        iOS { v("12") }
    }
}
