# Architecture

*Last Updated: 2026-10-17*

## Summary

ScreenOff is a single Android application module. `MainActivity` presents the Views/XML settings UI, manages accessibility and battery-optimization setup, and exposes activation choices. `GlobalService` is the long-lived accessibility service: it owns the floating overlay, gesture/key/orientation controls, screen-state synchronization, and optional LAN server.

The actual panel power operation is isolated in `ScreenController`, a Java class launched as a standalone `app_process` with the installed APK as its `CLASSPATH`. It reflects Android's private `SurfaceControl` APIs, changes the physical display power mode, and exposes an `IScreenOff` Binder implementation. The helper sends that Binder through a sticky broadcast; the app and accessibility service wrap it with the generated AIDL proxy.

## High-Level Diagram

```text
Launcher / settings UI ──> MainActivity
                              │ Shizuku, ADB, or root activation
                              ▼
                   activation command (CLASSPATH = installed APK)
                              │ app_process (uid 0 or 2000)
                              ▼
ScreenController ── SurfaceControl reflection ──> display power mode
      │ sticky broadcast: intent.screenoff.sendBinder
      ├──────────────────────────────> MainActivity (IScreenOff proxy)
      └──────────────────────────────> GlobalService (IScreenOff proxy)
                                                │
             overlay / key / orientation / LAN HTTP / shortcuts
                                                ▼
                                       setPowerMode(...)
```

## Components

- **Main UI and activation:** `app/src/main/java/com/tile/screenoff/MainActivity.java` binds the settings layout, persists preferences, registers the Binder broadcast receiver, and runs `activationCommand()` with `Shizuku.newProcess` or shows it as a copyable `adb shell '<cmd>'` for ADB/root. Layout and labels are in `app/src/main/res/layout/main.xml` and `app/src/main/res/values/strings.xml`.
- **Accessibility controller:** `app/src/main/java/com/tile/screenoff/GlobalService.java` is declared with `BIND_ACCESSIBILITY_SERVICE`. It creates an accessibility overlay, handles touch/key/orientation actions, receives screen lifecycle broadcasts, and optionally starts the local server.
- **Privileged display helper:** `app/src/main/java/com/tile/screenoff/ScreenController.java` runs outside the normal app process. It uses `android.view.SurfaceControl` reflection and the Android 14+ `DisplayControl` helper to call display power APIs.
- **Transport and protocol:** `SimpleTcpServer`, `HttpRequestParser`, and `HttpRequest` implement one-connection-at-a-time TCP transport and minimal HTTP request parsing for `GlobalService`.
- **Entry adapters:** `tileService`, `ScrOff`, and `ScrOn` are manifest entry points for quick settings, shortcuts, and direct screen state actions.

## Data Flow

1. The user opens `MainActivity` and enables the accessibility service and desired controls.
2. Shizuku, ADB, or root runs the self-contained activation command: it grants `WRITE_SECURE_SETTINGS`, sets `CLASSPATH` from `pm path`, and starts `app_process` with the app uid as the argument.
3. `ScreenController` validates that it runs as root or shell, records the allowed caller uid (checked in every Binder method), creates an `IScreenOff.Stub`, and publishes it in a sticky `intent.screenoff.sendBinder` broadcast using reflected ActivityManager APIs.
4. `MainActivity` and `GlobalService` receive the `BinderContainer`, create their AIDL proxies, and enable the UI/overlay.
5. A switch, overlay gesture, key, orientation event, shortcut, or HTTP endpoint invokes `setPowerMode`. The helper selects a display token and calls `SurfaceControl.setDisplayPowerMode` with `POWER_MODE_OFF` or `POWER_MODE_NORMAL`.
6. Screen lifecycle broadcasts update the helper's `ScreenState`; the accessibility service updates its overlay and orientation listener accordingly.

## Key Decisions & Constraints

- The helper is a separate `app_process` because the private display APIs require shell/root-level access; the app communicates over Binder rather than calling them directly.
- Android API differences are handled by the pre-Q/Q+ `SurfaceControl` path and the API 34+ `DisplayControl` path.
- The app uses an accessibility overlay instead of a normal application window for global input and controls.
- LAN control is opt-in, binds to TCP port `20000`, and requires a per-install `?t=` token over plain HTTP; it should only be enabled on a trusted network.
- The release signing configuration is present in `app/build.gradle`; its credential values are intentionally not documented.
