# Entry Points

*Last Updated: 2026-10-17*

## Entry Points

| Entry point | Type | Purpose | File |
|-------------|------|---------|------|
| `android.intent.action.MAIN` / `LAUNCHER` | Activity | Open settings, activation, and manual display controls | [MainActivity.java](../../app/src/main/java/com/tile/screenoff/MainActivity.java) |
| `android.accessibilityservice.AccessibilityService` | Service | Global overlay, gestures, key/orientation controls, screen-state synchronization | [GlobalService.java](../../app/src/main/java/com/tile/screenoff/GlobalService.java) |
| `android.service.quicksettings.action.QS_TILE` | Quick Settings tile | Launch the no-display screen-off activity | [tileService.java](../../app/src/main/java/com/tile/screenoff/tileService.java) |
| `ScrOff` / `ScrOn` | No-display activities and launcher shortcuts | Send `action.ScrOff` with `state=true` or `false` when accessibility is enabled; otherwise only open accessibility Settings (new task) | [ScrOff.java](../../app/src/main/java/com/tile/screenoff/ScrOff.java), [ScrOn.java](../../app/src/main/java/com/tile/screenoff/ScrOn.java) |
| `app_process ... com.tile.screenoff.ScreenController` | Privileged helper process | Publish Binder and change physical display power mode | [ScreenController.java](../../app/src/main/java/com/tile/screenoff/ScreenController.java) (CLASSPATH is the installed APK; argument is the app uid) |
| TCP port `20000` | Optional local HTTP-like server | Serve the control page and switch screen state by request target; requires `?t=<token>` | [GlobalService.java](../../app/src/main/java/com/tile/screenoff/GlobalService.java) |

## Representative Flow

The main activation path begins in `MainActivity.showActivate()`. It builds `activationCommand()` (`pm grant … WRITE_SECURE_SETTINGS`, then `CLASSPATH` set from the first `pm path com.tile.screenoff` entry (covers split APKs) and `nohup app_process /system/bin com.tile.screenoff.ScreenController <app uid>`); the Shizuku branch pipes it to `sh` via `Shizuku.newProcess`, and the ADB/root dialog copies it as `adb shell '<cmd>'`. Nothing is written to external storage.

`ScreenController.main()` requires shell or root and the app uid argument, creates its `IScreenOff.Stub`, and publishes it in the sticky `intent.screenoff.sendBinder` broadcast. `MainActivity` and `GlobalService` receive the `BinderContainer`, unwrap the binder, and invoke the AIDL proxy. A UI switch or overlay action calls `setPowerMode`; the helper reflects the matching `SurfaceControl` method and changes the display power mode.

For the quick-settings path, `tileService.onClick()` launches `ScrOff` (via `startActivityAndCollapse(PendingIntent)` on API 34+, where the `Intent` overload throws), after first enabling `GlobalService` through `Settings.Secure` if it is not yet enabled (the tile is bound by SystemUI, so this is not reachable by other apps). `ScrOff` then broadcasts `action.ScrOff`, or opens accessibility settings if the service is still off. The exported `ScrOff`/`ScrOn` shortcuts never write secure settings themselves. `GlobalService` handles that action and delegates to the same Binder-backed `screenoff()` path.
