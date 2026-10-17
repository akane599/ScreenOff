# Communication

*Last Updated: 2026-10-17*

## APIs

### `IScreenOff` Binder API

The AIDL contract in `app/src/main/aidl/com/tile/screenoff/IScreenOff.aidl` defines four calls:

- `setPowerMode(boolean turnOff)` changes the display to off or normal power mode.
- `updateNowScreenState(boolean isScreenOn)` synchronizes the helper's tracked state with Android screen broadcasts.
- `getNowScreenState()` returns the helper enum ordinal: `0` off, `1` on, `2` special/off-running state.
- `closeAndExit()` removes the sticky Binder broadcast and terminates the helper process.

`BinderContainer` carries the raw `IBinder` in an `Intent` extra named `binder`. The helper publishes it through the sticky action `intent.screenoff.sendBinder`; `MainActivity` and `GlobalService` register receivers for that action and create the generated AIDL proxy.

Every `IScreenOff` method calls `ScreenController.enforceScreenOffCaller()`, which rejects any `Binder.getCallingUid()` other than the app uid passed as the helper's first argument (a `SecurityException` the app catches at each call site). This is the primary protection, because sticky replay can hand the raw Binder to any registrant.

### Local HTTP-like API

When the `net` preference is enabled, `GlobalService` starts `SimpleTcpServer` on TCP port `20000`. Requests are parsed from a request line and headers (capped at 8 KiB → `431 Request Header Fields Too Large`); each connection is handled independently with a 10 s socket timeout. Every request must carry the per-install token as `?t=<token>` (`net_token` in preferences `s`, 16 random bytes, URL-safe base64), otherwise `403 Forbidden`. This is not a general HTTP framework.

| Request target | Response / effect |
|----------------|-------------------|
| `/` or `/index.html` | HTML control page with device and current-state substitutions |
| `/favicon.ico` | Packaged `favicon.png` |
| `/1?...` | Call `setPowerMode(false)` and return `200 OK` |
| `/2?...` | Call `setPowerMode(true)` and return `200 OK` |
| Anything else | Packaged `404.html` and `404 Not Found` |

The token is the only authentication and travels in plain HTTP; keep the LAN server (off by default) to trusted networks.

## Events & Messaging

- `intent.screenoff.sendBinder` is the sticky helper-to-app Binder handoff. The helper sends it via reflected `IActivityManager.broadcastIntent` with `appOp=-1` (`OP_NONE`); `0` is `OP_COARSE_LOCATION`, which Android 17 enforces on receivers. App receivers (`MainActivity`, `GlobalService`) register it `RECEIVER_EXPORTED` because the sender is the shell/root helper; the intent is package-explicit (`setPackage("com.tile.screenoff")`) and carries no permissions, because ActivityManager refuses sticky broadcasts with `requiredPermissions` and sticky replay arrives as uid -1 (so a receiver `broadcastPermission` drops it). A spoofed `BinderContainer` can only cause denial of service (`whittle:` in `GlobalService`).
- `intent.screenoff.exit` asks `GlobalService` to disable/stop itself; sent by `MainActivity` with `setPackage(getPackageName())`.
- `action.ScrOff` carries a boolean `state` and is sent by `ScrOff`/`ScrOn` with `setPackage(getPackageName())` to request a display action.
- `GlobalService` registers `action.ScrOff`, `intent.screenoff.exit`, `Intent.ACTION_SCREEN_OFF`, `ACTION_SCREEN_ON`, and `ACTION_USER_PRESENT` in a second filter with `RECEIVER_NOT_EXPORTED` (API 33+). Senders must stay package-explicit: on API 37 implicit app broadcasts matched no NOT_EXPORTED receiver. The screen-state actions update the helper's state and overlay behavior.
- `SharedPreferences` file `s` is the in-process configuration channel for overlay, gesture, orientation, key, and network settings.

## External Integrations

- **Shizuku:** `MainActivity` uses `rikka.shizuku.Shizuku` to request permission and start a shell process. The provider is declared in `AndroidManifest.xml`.
- **Android system services:** accessibility, window manager, settings, ActivityManager, and power/display services are used through Android APIs and reflected hidden APIs.
- **ADB/root:** `MainActivity.activationCommand()` builds a self-contained shell command: `pm grant … WRITE_SECURE_SETTINGS`, then `CLASSPATH` set from the first `pm path com.tile.screenoff` entry (covers split APKs) and `nohup app_process /system/bin com.tile.screenoff.ScreenController <app uid>`. The dialog shows and copies it as `adb shell '<cmd>'`; Shizuku runs the same command through `Shizuku.newProcess`.
