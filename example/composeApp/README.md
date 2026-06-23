# composeApp — Compose Multiplatform + UXCam starter

A clean, minimal Compose Multiplatform app wired up with **UXCam** on both Android and
iOS. One shared UI rendered by thin Android and iOS hosts, and a single place to configure
UXCam.

This is a **standalone Gradle build** — fully independent of the main `example/exampleApp`.
It has its own `gradlew`, `settings.gradle.kts`, and version catalog, so it builds and
opens entirely on its own. It consumes the UXCam KMP wrapper (`com.uxcam.kmp:uxcam`) as a
published dependency from `mavenLocal`, the same way a real consumer would add the library.

```
composeApp/                         # standalone Gradle project root
├── shared/                         # Kotlin Multiplatform library — shared UI + iOS framework
│   └── src/
│       ├── commonMain/             # shared UI (App.kt) + UxcamSetup.kt  ← single UXCam config
│       ├── androidMain/            # Android Platform actual
│       └── iosMain/                # MainViewController + iOS Platform actual
├── androidApp/                     # Android application host (MainActivity)
└── iosApp/                         # Xcode project hosting the shared framework (CocoaPods)
```

> Android can't combine `com.android.application` with the Kotlin Multiplatform plugin
> in one module since AGP 9, so the app is split into a KMP `shared` library and a thin
> `androidApp` application.

## Configuring UXCam (single place)

The app key and config live in **one file**: `shared/src/commonMain/.../UxcamSetup.kt`.
Change `APP_KEY` there to your dashboard's key — nowhere else. Both platforms start UXCam
from this exact object (`MainActivity.onCreate` on Android, `MainViewController()` on iOS),
so there is one config and one code path.

```kotlin
object UxcamSetup {
    private const val APP_KEY = "your-app-key"   // ← the only place to set this
    fun start() { UXCam.startWithConfiguration(UXConfig(appKey = APP_KEY, ...)) }
}
```

## Prerequisites

- The UXCam KMP wrapper (`com.uxcam.kmp:uxcam`) is built **from source** via a Gradle
  composite build (`includeBuild("../..")` in `settings.gradle.kts`) — no
  `publishToMavenLocal` step, and the iOS framework always links the real implementation
  (no stale-artifact crashes).
- The native debug Android SDK (`com.uxcam:uxcam-debug`, pulled transitively) is resolved
  from `~/.m2`; if missing, run `./gradlew :uxcam:publishDebugToMavenLocal` from the repo root.
- iOS only: the native SDK xcframework at `uxcam/localpods/UXCam/UXCam.xcframework`
  (gitignored) and the CocoaPods CLI (`pod`).

## Run on Android

From this directory (`example/composeApp`):

```bash
./gradlew :androidApp:installDebug
```

Or open `example/composeApp` in Android Studio and run the `androidApp` configuration. The
native UXCam Android SDK arrives transitively via the wrapper — nothing else to add.

## Run on iOS

UXCam's native iOS SDK is linked via CocoaPods. From `iosApp/`:

```bash
cd iosApp
pod install            # generates iosApp.xcworkspace + Pods
open iosApp.xcworkspace # IMPORTANT: open the .xcworkspace, not the .xcodeproj
```

In Xcode, set your signing **Team** in `Configuration/Config.xcconfig` (via `TEAM_ID`)
only if running on a physical device, pick a simulator, and Run. The `[CP-User] Build shared`
phase compiles the shared Kotlin framework automatically before linking.

> Targets `iosArm64` + `iosSimulatorArm64` only, so use an **Apple-Silicon** Mac/simulator.
> Add `iosX64()` in `shared/build.gradle.kts` for an Intel simulator.

## Verifying a session reaches the dashboard

Run the app, tap around for a few seconds, then background it (UXCam uploads on the next
launch / when the session ends). With `enableIntegrationLogging = true`, the logs print a
UXCam verification-success line. The session then appears in your UXCam dashboard shortly
after.
