# uxcam-kmp

A Kotlin Multiplatform wrapper over the native UXCam SDKs (Android + iOS). Shared
Kotlin code calls UXCam through one common API; each platform's `actual` delegates
to the native SDK directly (no MethodChannel-style bridge).

## Module layout

```
uxcam-kmp/
├── settings.gradle.kts            # repositories + included modules
├── gradle/libs.versions.toml      # versions (incl. the native SDK coordinate)
├── uxcam/                         # the library module → .aar + UXCamKMP.framework
│   └── src/
│       ├── commonMain/   UXCam.kt (expect), UXConfig.kt   # public API
│       ├── androidMain/  UXCam.android.kt                 # actual → com.uxcam.UXCam
│       └── iosMain/      UXCam.ios.kt                      # actual → native iOS UXCam SDK (CocoaPods)
└── example/                       # sample app, part of this same Gradle build
    ├── shared/                    # Android-only Compose UI/logic, depends on projects.uxcam
    ├── androidApp/                # Android entry point (Compose + XML screens)
    └── iosApp/                    # native SwiftUI app, links the UXCamKMP framework
```

The sample follows a Flutter-style split: one common API (the `:uxcam` wrapper, whose
`actual` per platform talks to the native SDK) with **native UI per platform** — Compose/XML
on Android, **SwiftUI** on iOS. The SwiftUI app (`example/iosApp`) calls the wrapper directly
through a thin Swift facade (`iosApp/Support/UX.swift`) and mirrors the Android demo screens.

## Consuming from an app

In the consumer project's `settings.gradle.kts`,
```kotlin
mavenLocal { mavenContent { includeGroupAndSubgroups("com.uxcam") } }
```

`gradle/libs.versions.toml`:
```toml
[versions]
uxcamKmp = "<latest_version>"     

[libraries]
uxcam-kmp = { module = "com.uxcam.kmp:uxcam", version.ref = "uxcamKmp" }
```

`build.gradle.kts`:
```kotlin
implementation("com.uxcam.kmp:uxcam:<latest_version>")
```

### Usage

```kotlin
import com.uxcam.kmp.UXCamKMP
import com.uxcam.kmp.UXConfig

UXCamKMP.startWithConfiguration(UXConfig(appKey = "YOUR_APP_KEY"))
UXCamKMP.logEvent("button_clicked")
```

## Gradle plugin (Kotlin-source consumers)

For consumers who write **shared Kotlin** that calls the wrapper and build their **own** iOS
framework, the `com.uxcam.kmp.gradle` plugin removes the per-module boilerplate — modelled on
[Sentry's KMP Gradle plugin](https://docs.sentry.io/platforms/kotlin/guides/kotlin-multiplatform/configuration/gradle/).
Applied to a KMP shared module it:

- adds `com.uxcam.kmp:uxcam` to the `commonMain` source set, and
- for Kotlin-CocoaPods users on a macOS host, adds the native `pod("UXCam")` so the iOS framework
  links the native symbols the wrapper's cinterop references.

> This is independent of the binary-XCFramework/SPM distribution above (which is for Swift-only
> consumers). iOS native linking is **CocoaPods-only** for now; an SPM linker path may follow.

```kotlin
// build.gradle.kts of your KMP shared module
plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")   // required for the native UXCam pod auto-install
    id("com.uxcam.kmp.gradle") version "0.0.2"
}

uxcamKmp { }   // defaults: installs com.uxcam.kmp:uxcam into commonMain + pod("UXCam") for iOS
```

The plugin currently resolves from **mavenLocal**, so build it once and add the repo:

```kotlin
// settings.gradle.kts
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenLocal { mavenContent { includeGroupAndSubgroups("com.uxcam") } }
    }
}
```
```bash
./gradlew :uxcam-kmp-gradle-plugin:publishToMavenLocal
```

### Configuration

Everything is on by default; override only what you need:

```kotlin
uxcamKmp {
    autoInstall {
        enabled.set(true)                              // master switch (default true)
        commonMain {
            enabled.set(true)                          // add com.uxcam.kmp:uxcam (default true)
            uxcamKmpVersion.set("0.0.2")               // wrapper version
        }
        cocoapods {
            enabled.set(true)                          // add pod("UXCam") (default true)
            uxcamCocoaVersion.set("3.8.3")             // native iOS SDK version
        }
    }
}
```

A `pod("UXCam")` you declare yourself always wins — the plugin never overwrites it.
