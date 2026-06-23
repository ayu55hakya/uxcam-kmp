# composeApp — Compose Multiplatform starter

A clean, minimal Compose Multiplatform app. One shared UI rendered by thin Android and
iOS hosts. **No UXCam wiring yet** — that gets integrated on top of this template later.

This is a **standalone Gradle build** — fully independent of the main `example/exampleApp`.
It has its own `gradlew`, `settings.gradle.kts`, and version catalog, so it builds and
opens entirely on its own.

```
composeApp/                         # standalone Gradle project root
├── shared/                         # Kotlin Multiplatform library — shared UI + iOS framework
│   └── src/
│       ├── commonMain/             # shared UI — App.kt, Platform.kt
│       ├── androidMain/            # Android Platform actual
│       └── iosMain/                # MainViewController + iOS Platform actual
├── androidApp/                     # Android application host (MainActivity)
└── iosApp/                         # Xcode project hosting the shared framework
```

> Android can't combine `com.android.application` with the Kotlin Multiplatform plugin
> in one module since AGP 9, so the app is split into a KMP `shared` library and a thin
> `androidApp` application.

## Run on Android

From this directory (`example/composeApp`):

```bash
./gradlew :androidApp:installDebug
```

Or open `example/composeApp` in Android Studio and run the `androidApp` configuration.

## Run on iOS

Open `iosApp/iosApp.xcodeproj` in Xcode, set your signing **Team** in
`iosApp/Configuration/Config.xcconfig` (via `TEAM_ID`), pick a simulator, and run.

The `Compile Kotlin Framework` build phase invokes
`:shared:embedAndSignAppleFrameworkForXcode`, so Xcode builds the shared Kotlin
framework automatically before linking the app.
