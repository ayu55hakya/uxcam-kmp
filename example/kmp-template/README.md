# KmpTemplate — Compose Multiplatform starter

A clean, minimal **Kotlin Multiplatform + Compose Multiplatform** template targeting
**Android and iOS**. One shared Compose UI is rendered by thin native hosts: `MainActivity`
on Android and a SwiftUI `ContentView` on iOS.

```
kmp-template/                       # standalone Gradle project root
├── shared/                         # Kotlin Multiplatform library — shared Compose UI + iOS framework
│   └── src/
│       ├── commonMain/             # shared UI (App.kt) + expect Platform (Platform.kt)
│       ├── androidMain/            # Android `actual` (Platform.android.kt)
│       ├── iosMain/                # MainViewController + iOS `actual` (Platform.ios.kt)
│       └── commonTest/             # shared unit tests
├── androidApp/                     # Android application host (MainActivity)
└── iosApp/                         # Xcode project hosting the shared framework (CocoaPods)
```

## Toolchain

| Tool | Version |
| --- | --- |
| Kotlin | 2.2.21 |
| Compose Multiplatform | 1.9.3 |
| Android Gradle Plugin | 9.0.1 |
| Gradle | 9.1.0 (wrapper) |
| Android compile/target SDK | 36 · min SDK 24 |
| iOS deployment target | 15.0 |

> AGP 9 forbids combining `com.android.application` with the Kotlin Multiplatform plugin in
> one module, so the build is split into a KMP `shared` library and a thin `androidApp`
> application that depends on it.

## Run on Android

Open the project in Android Studio and run the `androidApp` configuration, or from the CLI:

```bash
./gradlew :androidApp:installDebug   # build + install on a connected device/emulator
./gradlew :androidApp:assembleDebug  # just build the APK
```

`local.properties` points `sdk.dir` at the Android SDK — update it for your machine.

## Run on iOS

The iOS framework is built from `:shared` via the Kotlin CocoaPods plugin.

```bash
./gradlew :shared:generateDummyFramework   # once, before the first pod install
cd iosApp
pod install                                # regenerates iosApp.xcworkspace
open iosApp.xcworkspace                     # build & run from Xcode
```

Set your signing `TEAM_ID` in `iosApp/Configuration/Config.xcconfig` before running on a
device.

## Where to put your code

- **Shared UI / logic** → `shared/src/commonMain`. This is the default home for new code.
- **Platform-specific** → declare an `expect` in `commonMain` and provide `actual`
  implementations in `androidMain` / `iosMain` (see `Platform.kt` for the pattern).
- The native hosts (`MainActivity`, `ContentView.swift`) should stay thin — they only mount
  the shared `App()`.
