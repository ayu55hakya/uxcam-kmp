import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinCocoapods)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
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
        }
        // The shared UI calls the :uxcam wrapper, which cinterops the native UXCam pod;
        // declare it here so this module's iOS framework links the native symbols. Mirrors
        // the :uxcam module's own declaration.
        pod("UXCam") {
            source = path(file("../../../uxcam/localpods/UXCam"))
            moduleName = "UXCam"
        }
    }

    sourceSets {
        commonMain.dependencies {
            // The UXCam KMP wrapper — used by UxcamSetup and the occlusion screens.
            implementation(projects.uxcam)
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
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
