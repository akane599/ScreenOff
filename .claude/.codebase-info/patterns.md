# Patterns & Conventions

*Last Updated: 2026-10-17*

## Code Organization

All Java application code is in `app/src/main/java/com/tile/screenoff/`. Android component declarations are centralized in `app/src/main/AndroidManifest.xml`; Views, drawables, labels, accessibility metadata, and shortcuts live under `app/src/main/res/`. The helper script and HTTP page assets are packaged under `app/src/main/assets/`.

## Recurring Patterns

- **Static helper state:** `ScreenController` keeps reflected classes/methods, Binder publication state, screen state, and the optional volume-event process in static fields because it is a standalone process service.
- **Lazy reflection:** `ScreenController` and `DisplayControl` cache private framework `Method` objects on first use and return `null`/log on invocation failures.
- **Broadcast-to-Binder handoff:** `BinderContainer` makes an `IBinder` parcelable so the helper can publish it via the reflected ActivityManager sticky-broadcast path.
- **Preference-backed UI:** `MainActivity` writes settings as controls change; `GlobalService` implements `OnSharedPreferenceChangeListener` and refreshes the overlay.
- **Overlay controller:** `GlobalService` uses `TYPE_ACCESSIBILITY_OVERLAY`, touch coordinates, snap-to-edge behavior, and persisted orientation-specific positions. The overlay view also carries `setKeepScreenOn` during screen-off mode, so `floatWindow()` never removes it while the service runs: with `float` off (the default) it becomes an invisible, untouchable 1px window.
- **Single-connection server restart:** `SimpleTcpServer` accepts one socket, handles it on worker threads, and restarts after a response or disconnect.

## Error Handling

The code generally logs or prints reflection, I/O, and Binder failures, often continuing with a disabled feature or a `null` result. Activation errors are surfaced with Toasts or an accessibility-settings fallback. Be careful when changing these paths: hidden API availability, process identity, Binder liveness, and permission state are runtime prerequisites rather than ordinary input validation.

## Testing

`app/src/test/java/com/tile/screenoff/` holds JVM tests for the HTTP parser and request (`HttpRequestParserTest`, `HttpRequestTest`); there is no `androidTest` source set. Beyond those, the most direct validation is an Android build/lint run plus device verification of activation, Binder handoff, display power changes, accessibility overlay behavior, and optional port-20000 requests. The repository's documented commands are listed in `onboarding.md`.

## Configuration

Preferences are stored under the name `s`; keys include `float`, `land`, `canmove`, `doubleTap`, `shake`, `volume`, `net`, `size`, `tran`, `sensity`, `scrOffKey`, `scrOnKey`, `net_token` (LAN token), and orientation-specific `x1`/`y1`/`x2`/`y2`. The `net` preference controls server startup. Release signing is configured in `app/build.gradle`; never copy its credential values into map documents.
