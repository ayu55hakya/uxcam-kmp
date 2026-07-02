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
// `group`/`version` track `:uxcam` via the shared `uxcamKmpVersion` in the root gradle.properties,
// so the plugin and the artifact it installs can never drift apart (previously two hardcoded
// literals that silently diverged). `java-gradle-plugin` + `maven-publish` auto-create the
// implementation publication AND the plugin marker
// (com.uxcam.kmp.gradle:com.uxcam.kmp.gradle.gradle.plugin); `publishToMavenLocal` writes both to
// ~/.m2 so external consumers can resolve via `pluginManagement { mavenLocal() }`.
val uxcamKmpVersion = providers.gradleProperty("uxcamKmpVersion").get()

group = "com.uxcam.kmp.gradle"
version = uxcamKmpVersion

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

// The default library version the plugin installs into consumers (Versions.UXCAM_KMP) must equal
// the plugin's own version. Rather than hand-sync a source literal, generate it from the same
// `uxcamKmpVersion` property so it can never drift.
val generateUxcamVersion by tasks.registering {
    val outputDir = layout.buildDirectory.dir("generated/uxcamVersion/kotlin")
    val versionValue = uxcamKmpVersion
    inputs.property("version", versionValue)
    outputs.dir(outputDir)
    doLast {
        val pkgDir = outputDir.get().dir("com/uxcam/kmp/gradle").asFile
        pkgDir.mkdirs()
        pkgDir.resolve("GeneratedVersion.kt").writeText(
            "package com.uxcam.kmp.gradle\n\n" +
                "/** Generated from `uxcamKmpVersion` in the root gradle.properties — do not edit. */\n" +
                "internal const val GENERATED_UXCAM_KMP_VERSION: String = \"$versionValue\"\n"
        )
    }
}

kotlin.sourceSets.named("main") {
    kotlin.srcDir(generateUxcamVersion)
}
