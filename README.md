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
