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

### Local HTTP-like API

When the `net` preference is enabled, `GlobalService` starts `SimpleTcpServer` on TCP port `20000`. Requests are parsed from a request line and headers; the implementation uses the request target and does not implement authentication or a general HTTP framework.

| Request target | Response / effect |
|----------------|-------------------|
| `/` or `/index.html` | HTML control page with device and current-state substitutions |
| `/favicon.ico` | Packaged `favicon.png` |
| `/1?...` | Call `setPowerMode(false)` and return `200 OK` |
| `/2?...` | Call `setPowerMode(true)` and return `200 OK` |
| Anything else | Packaged `404.html` and `404 Not Found` |

The LAN surface has no authentication in this version. Treat the displayed address and port as a trusted-network-only control surface.

## Events & Messaging

- `intent.screenoff.sendBinder` is the sticky helper-to-app Binder handoff. The helper sends it via reflected `IActivityManager.broadcastIntent` with `appOp=-1` (`OP_NONE`); `0` is `OP_COARSE_LOCATION`, which Android 17 enforces on receivers. App receivers (`MainActivity`, `GlobalService`) register it `RECEIVER_EXPORTED` because the sender is the shell/root helper; it is not caller-authenticated (open security finding).
- `intent.screenoff.exit` asks `GlobalService` to disable/stop itself; sent by `MainActivity` with `setPackage(getPackageName())`.
- `action.ScrOff` carries a boolean `state` and is sent by `ScrOff`/`ScrOn` with `setPackage(getPackageName())` to request a display action.
- `GlobalService` registers `action.ScrOff`, `intent.screenoff.exit`, `Intent.ACTION_SCREEN_OFF`, `ACTION_SCREEN_ON`, and `ACTION_USER_PRESENT` in a second filter with `RECEIVER_NOT_EXPORTED` (API 33+). Senders must stay package-explicit: on API 37 implicit app broadcasts matched no NOT_EXPORTED receiver. The screen-state actions update the helper's state and overlay behavior.
- `SharedPreferences` file `s` is the in-process configuration channel for overlay, gesture, orientation, key, and network settings.

## External Integrations

- **Shizuku:** `MainActivity` uses `rikka.shizuku.Shizuku` to request permission and start a shell process. The provider is declared in `AndroidManifest.xml`.
- **Android system services:** accessibility, window manager, settings, ActivityManager, and power/display services are used through Android APIs and reflected hidden APIs.
- **ADB/root:** `app/src/main/assets/starter.sh` is the fallback activation script. It grants `WRITE_SECURE_SETTINGS` and starts the helper through `app_process`.
