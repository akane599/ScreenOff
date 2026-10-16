# Entry Points

*Last Updated: 2026-10-16*

## Entry Points

| Entry point | Type | Purpose | File |
|-------------|------|---------|------|
| `android.intent.action.MAIN` / `LAUNCHER` | Activity | Open settings, activation, and manual display controls | [MainActivity.java](../../app/src/main/java/com/tile/screenoff/MainActivity.java) |
| `android.accessibilityservice.AccessibilityService` | Service | Global overlay, gestures, key/orientation controls, screen-state synchronization | [GlobalService.java](../../app/src/main/java/com/tile/screenoff/GlobalService.java) |
| `android.service.quicksettings.action.QS_TILE` | Quick Settings tile | Launch the no-display screen-off activity | [tileService.java](../../app/src/main/java/com/tile/screenoff/tileService.java) |
| `ScrOff` / `ScrOn` | No-display activities and launcher shortcuts | Send `action.ScrOff` with `state=true` or `false` | [ScrOff.java](../../app/src/main/java/com/tile/screenoff/ScrOff.java), [ScrOn.java](../../app/src/main/java/com/tile/screenoff/ScrOn.java) |
| `app_process ... com.tile.screenoff.ScreenController` | Privileged helper process | Publish Binder and change physical display power mode | [ScreenController.java](../../app/src/main/java/com/tile/screenoff/ScreenController.java), [starter.sh](../../app/src/main/assets/starter.sh) |
| TCP port `20000` | Optional local HTTP-like server | Serve the control page and switch screen state by request target | [GlobalService.java](../../app/src/main/java/com/tile/screenoff/GlobalService.java) |

## Representative Flow

The main activation path begins in `MainActivity.showActivate()`. It writes `starter.sh` to external app storage and extracts the packaged `classes.dex` as `ScreenController.dex`; the Shizuku branch then runs the script through `Shizuku.newProcess`. The script starts `ScreenController` with `app_process` after granting `WRITE_SECURE_SETTINGS`.

`ScreenController.main()` requires shell or root, creates its `IScreenOff.Stub`, and publishes it in the sticky `intent.screenoff.sendBinder` broadcast. `MainActivity` and `GlobalService` receive the `BinderContainer`, unwrap the binder, and invoke the AIDL proxy. A UI switch or overlay action calls `setPowerMode`; the helper reflects the matching `SurfaceControl` method and changes the display power mode.

For the quick-settings path, `tileService.onClick()` launches `ScrOff`, which either broadcasts `action.ScrOff` immediately when the accessibility service is enabled or opens accessibility settings. `GlobalService` handles that action and delegates to the same Binder-backed `screenoff()` path.
