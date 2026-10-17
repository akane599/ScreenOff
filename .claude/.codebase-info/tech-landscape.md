# Technology Landscape

*Last Updated: 2026-10-17*

## Source-of-Truth Files

| Information | File |
|-------------|------|
| Module list and repositories | [settings.gradle](../../settings.gradle) |
| Android plugin versions | [build.gradle](../../build.gradle) |
| App SDK, signing configuration, build features, dependencies | [app/build.gradle](../../app/build.gradle) |
| Manifest components and permissions | [app/src/main/AndroidManifest.xml](../../app/src/main/AndroidManifest.xml) |
| App behavior and UI | [app/src/main/java/com/tile/screenoff/](../../app/src/main/java/com/tile/screenoff/) |
| Binder contract | [app/src/main/aidl/com/tile/screenoff/IScreenOff.aidl](../../app/src/main/aidl/com/tile/screenoff/IScreenOff.aidl) |
| Build wrapper version | [gradle/wrapper/gradle-wrapper.properties](../../gradle/wrapper/gradle-wrapper.properties) |

## Stack

| Layer | Technology | Notes |
|-------|------------|-------|
| Language(s) | Java | App sources use Java; source/target compatibility is Java 8. |
| UI | Android Views/XML | `MainActivity` uses `main.xml`; no Compose sources are present. |
| Android runtime | Android SDK | `compileSdk`/`targetSdk` 35, `minSdk` 21. |
| System integration | AccessibilityService, TileService, Binder/AIDL, reflected framework APIs | Declared in `AndroidManifest.xml`; the helper uses `SurfaceControl`. |
| Privileged activation | Shizuku 13.1.0, ADB, or root | Shizuku API/provider are the only declared app dependencies. |
| Network | Java `ServerSocket` and streams | Optional local HTTP-like control server on port 20000. |
| Persistence | `SharedPreferences` | Preference file name is `s`; no database is used. |
| Build/tooling | Gradle 8.9 wrapper, Android Gradle Plugin 8.7.3 | AIDL build feature is enabled. |

## Infrastructure

There is no repository-defined backend, container, database, or monitoring stack. CI is a single GitHub Actions workflow, `.github/workflows/build-apk.yml` (JDK 21, `:app:assembleDebug :app:testDebugUnitTest`, debug APK uploaded as an artifact). Runtime assets are packaged under `app/src/main/assets/`. `app/build.gradle` contains a release `signingConfigs` block; keep its keystore location and credentials out of documentation and source changes unless the task explicitly requires signing work.

JVM unit tests live in `app/src/test/` (HTTP parser/request). Build, unit-test, lint, and screenshot commands are documented in `onboarding.md`; this mapping intentionally did not run Gradle.
