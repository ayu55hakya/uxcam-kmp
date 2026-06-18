import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
}

// Coordinate used by consumers. Distinct from the native SDK's `com.uxcam:uxcam`
// so the two never collide. A composite build (includeBuild) substitutes this
// with the local project; the same coordinate works for publishToMavenLocal later.
group = "com.uxcam.kmp"
version = "0.0.1"

kotlin {
    // `expect`/`actual` objects are stable enough to use; this flag suppresses the
    // Beta advisory warning the compiler emits for them.
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    // iOS targets are declared so the wrapper is genuinely multiplatform and ready
    // for iOS work later. For now their actuals are stubs (see iosMain).
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "UXCamKMP"
            isStatic = true
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
        androidMain.dependencies {
            // The real native Android SDK — only visible to the Android target.
            // It self-initializes its context via its own UXCamContentProvider,
            // so no startup/context plumbing is needed here.
            implementation(libs.uxcam.android)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
