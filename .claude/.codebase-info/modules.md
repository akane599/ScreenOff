# Key Modules

*Last Updated: 2026-10-16*

### Main UI and activation
- **Location:** `app/src/main/java/com/tile/screenoff/MainActivity.java`, `app/src/main/res/layout/main.xml`, `app/src/main/res/values/` (English default; `values-zh/`, `values-zh-rTW/` translations)
- **Purpose:** Configure controls, persist preferences, display service status, and activate the privileged helper.
- **Key files:** `MainActivity.java`, `main.xml`, `strings.xml`, `AndroidManifest.xml`
- **Depends on:** Android Activity/Settings APIs, Shizuku, `IScreenOff`, `GlobalService`
- **Exposes:** Launcher UI, activation dialog, manual screen switch, shortcut/permission guidance.

### Accessibility and global controls
- **Location:** `app/src/main/java/com/tile/screenoff/GlobalService.java`
- **Purpose:** Run the global service and manage the floating overlay, touch gestures, volume-key interception, orientation wake-up, and screen lifecycle.
- **Key files:** `GlobalService.java`, `app/src/main/res/xml/a.xml`, overlay drawable resources
- **Depends on:** Android `AccessibilityService`, `WindowManager`, `SharedPreferences`, `IScreenOff`, network helper classes
- **Exposes:** The service declared in the manifest and the optional LAN control server.

### Privileged display controller
- **Location:** `app/src/main/java/com/tile/screenoff/ScreenController.java`, `DisplayControl.java`, `BinderContainer.java`
- **Purpose:** Run as shell/root, obtain a physical display token, set display power mode, and expose Binder operations.
- **Key files:** `ScreenController.java`, `DisplayControl.java`, `BinderContainer.java`, `app/src/main/aidl/com/tile/screenoff/IScreenOff.aidl`
- **Depends on:** Android hidden `SurfaceControl`/display APIs and framework AIDL types under `app/src/main/aidl/android/`
- **Exposes:** `IScreenOff` methods for power mode, state synchronization, state query, and helper shutdown.

### Local HTTP/TCP transport
- **Location:** `SimpleTcpServer.java`, `HttpRequestParser.java`, `HttpRequest.java`, and the server methods in `GlobalService.java`
- **Purpose:** Accept one TCP client, parse a minimal HTTP request, serve packaged assets, and map request targets to display actions.
- **Key files:** `app/src/main/java/com/tile/screenoff/SimpleTcpServer.java`, `HttpRequestParser.java`, `HttpRequest.java`, `app/src/main/assets/index.html`, `404.html`, `favicon.png`
- **Depends on:** Java networking/stream classes and `GlobalService`'s Binder proxy.
- **Exposes:** Port `20000` when the `net` preference is enabled.

### Entry adapters and resources
- **Location:** `tileService.java`, `ScrOff.java`, `ScrOn.java`, `app/src/main/res/`, `app/src/main/assets/`
- **Purpose:** Connect quick settings, shortcuts, accessibility settings, themes, drawables, and web assets to the core modules.
- **Depends on:** Android manifest declarations and broadcasts.
- **Exposes:** User-facing launch and control surfaces; no separate application module.
