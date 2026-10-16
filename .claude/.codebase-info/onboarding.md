# Onboarding

*Last Updated: 2026-10-16*

## Prerequisites

- Android SDK with API 35 available through `ANDROID_HOME`.
- JDK compatible with the project's Android Gradle Plugin and Gradle wrapper.
- A device on Android API 21 or newer; Shizuku is optional if using ADB or root activation.
- For development, USB debugging and a device/emulator capable of exercising accessibility and display behavior.

## Quick Start

1. Open the project root and let the Gradle wrapper resolve its distribution and Android dependencies.
2. Build the debug APK with `./gradlew :app:assembleDebug --console=plain -q`.
3. Install with `./gradlew :app:installDebug -q` and launch `com.tile.screenoff/.MainActivity`.
4. In the app, enable the accessibility service. On Android versions where direct secure-settings writes are rejected, follow the link to Android accessibility settings.
5. Tap activation and choose Shizuku, ADB, or root. The activation helper must run as shell/root to call the private display API.
6. Enable the floating overlay or another control mode, then use the main switch, quick-settings tile, shortcut, configured keys, orientation wake-up, or optional LAN page.

## Common Commands

| Command | Purpose |
|---------|---------|
| `./gradlew :app:assembleDebug --console=plain -q` | Build a debug APK |
| `./gradlew :app:testDebugUnitTest --console=plain -q` | Run the configured debug unit-test task; no app test sources are currently mapped |
| `./gradlew :app:lintDebug --console=plain -q` | Run debug lint |
| `./gradlew :app:installDebug -q` | Install the debug build |
| `adb shell am start -n com.tile.screenoff/.MainActivity` | Launch the main activity |
| `adb logcat -d --pid=$(adb shell pidof -s com.tile.screenoff) | tail -80` | Inspect recent app logs |

## Common Tasks

### Change control defaults

Update the preference reads/writes in `MainActivity.java` and the corresponding initialization/refresh logic in `GlobalService.java`. Keep the preference key and default synchronized, and update user-visible labels in `app/src/main/res/values/strings.xml`.

### Add or change a control surface

Trace the entry point from `AndroidManifest.xml`, `shortcuts.xml`, or the tile class into `GlobalService` and the `IScreenOff` Binder. Keep display power calls in `ScreenController`; callers should use the AIDL interface.

### Change the LAN protocol

Update the parser/transport and `GlobalService.output()` together. Check the packaged assets in `app/src/main/assets/`, preserve explicit response headers, and document any new request target in `communication.md`. Remember that the server has no authentication.

## Gotchas

- The display helper is not a normal Android service. It is extracted from the APK's `classes.dex` and started with `app_process` by `assets/starter.sh`.
- The helper requires shell or root identity and `WRITE_SECURE_SETTINGS`; a successful app build does not prove activation works.
- Android API 34+ uses `DisplayControl`, while older paths use reflected `SurfaceControl` methods. Test on the target API levels when changing display code.
- The accessibility overlay and key interception depend on the service being enabled and on the relevant system permissions.
- LAN control listens on port `20000` without authentication. Enable it only on a trusted network.
- `app/build.gradle` contains release signing configuration. Do not expose or copy its keystore credentials.
- This map was created without running Gradle; dependency resolution may require access to the configured Google and Maven Central repositories.
