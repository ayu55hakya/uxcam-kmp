import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    `maven-publish`
}

// Compose occlusion helpers (Modifier.uxcamOcclude) split out of :uxcam so that:
//  - non-Compose consumers (SwiftUI + Views apps) never get compose-runtime/ui on their
//    runtime graph or a forced Compose version bump,
//  - :uxcam can declare targets compose-ui is not published for (linux/mingw, later
//    macos/tvos/watchos) without this module holding it back.
// Same group + version + package (com.uxcam.kmp) as the core wrapper; the Gradle plugin
// auto-installs this artifact only when a Compose plugin is detected on the consumer.
val uxcamKmpVersion = providers.gradleProperty("uxcamKmpVersion").get()

group = "com.uxcam.kmp"
version = uxcamKmpVersion

kotlin {
    iosArm64()
    iosSimulatorArm64()
    iosX64()
    jvm()
    js(IR) {
        browser()
        nodejs()
    }

    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    androidLibrary {
        namespace = "com.uxcam.kmp.compose"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        // Targets with no native UXCam SDK bind to the no-op Modifier actual.
        val noopMain by creating { dependsOn(commonMain.get()) }
        val jvmMain by getting { dependsOn(noopMain) }
        val jsMain by getting { dependsOn(noopMain) }
        val wasmJsMain by getting { dependsOn(noopMain) }

        commonMain.dependencies {
            api(projects.uxcam)
            implementation(libs.compose.runtime)
            implementation(libs.compose.ui)
        }
        androidMain.dependencies {
            // Compose occlusion bridge (UXCamKt.occludeSensitiveComposable).
            implementation(libs.uxcam.ktx.android)
        }
    }
}
