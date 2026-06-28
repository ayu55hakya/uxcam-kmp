plugins {
    alias(libs.plugins.kotlinJvm)
    `java-gradle-plugin`
    `maven-publish`
}

// Sentry-style convenience plugin for Kotlin-source consumers of the wrapper. Applying it to a
// downstream KMP shared module auto-installs `com.uxcam.kmp:uxcam` into commonMain and, for
// Kotlin-CocoaPods users on a Mac host, auto-adds the native `pod("UXCam")` so the iOS framework
// links the native symbols the wrapper's cinterop references. Distinct from the `:uxcam` library
// itself — this only configures a consuming build, it ships no runtime code.
//
// `group`/`version` track `:uxcam` so the plugin and the artifact it installs move together.
// `java-gradle-plugin` + `maven-publish` auto-create the implementation publication AND the plugin
// marker (com.uxcam.kmp.gradle:com.uxcam.kmp.gradle.gradle.plugin); `publishToMavenLocal` writes
// both to ~/.m2 so external consumers can resolve via `pluginManagement { mavenLocal() }`.
group = "com.uxcam.kmp.gradle"
// 0.1.0: adds the embedAndSign / SPM deliver-and-link path (linker/ package) on top of the
// CocoaPods auto-install. Canonical home for the plugin (the standalone uxcamKmp dev folder was
// folded in here).
version = "0.1.0"

dependencies {
    // Provides KotlinMultiplatformExtension, CocoapodsExtension, KotlinCocoapodsPlugin and
    // HostManager. compileOnly: the consuming build already brings the Kotlin Gradle plugin onto
    // the classpath, so we must not embed a second copy.
    compileOnly(libs.kotlin.gradle.plugin)

    testImplementation(libs.kotlin.gradle.plugin)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlin.testJunit)
    testImplementation(libs.junit)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
    }
}

gradlePlugin {
    plugins {
        create("uxcamKmp") {
            id = "com.uxcam.kmp.gradle"
            implementationClass = "com.uxcam.kmp.gradle.UXCamPlugin"
        }
    }
}
