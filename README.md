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

Library coordinate: **`com.uxcam.kmp:uxcam:0.0.1`** (set in `uxcam/build.gradle.kts`).

The sample lives in this same build and consumes the wrapper via a project dependency
(`implementation(projects.uxcam)`), so editing the library is picked up directly — no
`publishToMavenLocal` round-trip needed to run the sample.

## Running the sample app

### Android
```bash
./gradlew :example:androidApp:installDebug
```
The Android sample needs the native UXCam Android SDK resolvable (see the modes below);
`./gradlew publishToMavenLocal` is only required when testing an **external** consumer.

### iOS

The iOS wrapper binds to the native iOS UXCam SDK via the **Kotlin CocoaPods plugin**. The
SDK is consumed as a **local pod** built from the sibling `ios-framework` repo (the iOS
analogue of the Android local-debug SDK). One-time local setup:

```bash
# 1. Build the native SDK xcframework from the SDK repo
xcodebuild -project ~/Documents/ios-framework/UXCam/UXCamFramework.xcodeproj \
  -scheme "UXCam XCFramework build" -destination 'generic/platform=iOS' build

# 2. Stage it as a local pod (gitignored: uxcam/localpods/UXCam/)
mkdir -p uxcam/localpods/UXCam
cp -R ~/Documents/ios-framework/UXCam/Products/UXCam.xcframework uxcam/localpods/UXCam/
sed 's/__UXCAM_VERSION__/3.7.10/g' \
  ~/Documents/ios-framework/UXCam/External/UXCam.podspec > uxcam/localpods/UXCam/UXCam.podspec

# 3. Generate the dummy framework and install pods
./gradlew :uxcam:generateDummyFramework
cd example/iosApp && pod install
```

Then **open the workspace** (not the `.xcodeproj`) and run:
```bash
open example/iosApp/iosApp.xcworkspace
```
The CocoaPods script phase builds the `UXCamKMP` framework (`:uxcam:syncFramework`) on each
Xcode build, so library changes are picked up automatically.

> **iOS app key:** `iOSApp.swift` currently uses the Android sample's app key as a placeholder.
> UXCam issues a separate key per platform — set the **iOS** app key for real recording.
> A few common-API methods have no native iOS equivalent (e.g. `markSessionAsFavorite`,
> runtime auto-tagging toggles) and are documented no-ops in `UXCam.ios.kt`.

## Publishing the library

This library is consumed as a Maven artifact. For local development, publish it to
your local Maven repository (`~/.m2`):

```bash
./gradlew publishToMavenLocal
```

This publishes the umbrella module plus per-target variants
(`uxcam-android`, `uxcam-iosarm64`, `uxcam-iossimulatorarm64`). Re-run it after every
change — consumers pick up changes only after a republish.

## Choosing the native SDK build

The wrapper depends on the native UXCam Android SDK. Two modes are supported; switch
by editing **`gradle/libs.versions.toml`** and **`settings.gradle.kts`**, then
republishing.

### A. Published build (default / release)

Uses the released SDK from UXCam's Maven repo.

`gradle/libs.versions.toml`:
```toml
[versions]
uxcam = "3.10.3"            # a released version

[libraries]
uxcam-android = { module = "com.uxcam:uxcam", version.ref = "uxcam" }
```

`settings.gradle.kts` → `dependencyResolutionManagement.repositories`:
```kotlin
maven("https://sdk.uxcam.com/android/") {
    mavenContent { includeGroup("com.uxcam") }
}
```

### B. Local debug build (for testing native SDK changes)

Uses a locally-built debug variant of the native SDK. The artifact id is suffixed
`-debug` and lives only in `~/.m2`.

1. In the **native SDK repo** (`android-sdk`), publish the debug build to `~/.m2`:
   ```bash
   ./gradlew publishDebugToMavenLocal -PcreateMavenLocalBigDebug=true
   ```
   This produces `com.uxcam:uxcam-debug:<version>` (+ `screenshot-debug`,
   `screenaction-debug`, …), e.g. version `3.10.4-alpha.1`.

2. Point this library at the debug artifact — `gradle/libs.versions.toml`:
   ```toml
   [versions]
   uxcam = "3.10.4-alpha.1"   # the locally-published debug version

   [libraries]
   uxcam-android = { module = "com.uxcam:uxcam-debug", version.ref = "uxcam" }
   ```

3. Let this library's build resolve from `~/.m2` — `settings.gradle.kts`, add
   **before** the `sdk.uxcam.com` repo:
   ```kotlin
   mavenLocal {
       mavenContent { includeGroupAndSubgroups("com.uxcam") }
   }
   ```

4. Republish the wrapper so its metadata points at the debug artifact:
   ```bash
   ./gradlew publishToMavenLocal
   ```

> The wrapper's Kotlin source is identical in both modes — `uxcam-debug` exposes the
> same `com.uxcam.UXCam` API. Only the dependency coordinate changes.

## Consuming from an app

In the consumer project's `settings.gradle.kts` → `dependencyResolutionManagement.repositories`,
match the mode the wrapper was published with.

**If the wrapper uses the published native SDK (mode A):** the wrapper comes from `~/.m2`,
the native SDK from UXCam's repo.
```kotlin
mavenLocal { mavenContent { includeGroupAndSubgroups("com.uxcam.kmp") } }
maven("https://sdk.uxcam.com/android/") { mavenContent { includeGroup("com.uxcam") } }
```

**If the wrapper uses the local debug native SDK (mode B):** both the wrapper and the
`-debug` native artifacts are in `~/.m2`. One filter covers both, because `com.uxcam.kmp`
is a subgroup of `com.uxcam`:
```kotlin
mavenLocal { mavenContent { includeGroupAndSubgroups("com.uxcam") } }
```

Then declare the dependency (e.g. via the consumer's version catalog):
```kotlin
implementation("com.uxcam.kmp:uxcam:0.0.1")
```

### Usage

```kotlin
import com.uxcam.kmp.UXCam
import com.uxcam.kmp.UXConfig

// From shared/common code or Android code — no Context needed; the native SDK
// captures its own Application context at startup.
UXCam.startWithConfiguration(UXConfig(appKey = "YOUR_APP_KEY"))
UXCam.logEvent("button_clicked")
```
