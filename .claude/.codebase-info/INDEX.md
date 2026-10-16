# Codebase Map — ScreenOff

*Last Updated: 2026-10-16*

ScreenOff is a single-module Android app that turns the display panel off while leaving Android and other apps running. It uses an accessibility overlay for global controls and a privileged `app_process` helper, activated through Shizuku, ADB, or root.

**Stack:** Java · Android SDK 35 · Android Views/XML · Shizuku 13.1.0 · local Java TCP/HTTP server
**Shape:** Single `:app` module with an app UI, accessibility service, quick-settings tile, and privileged helper process

## Documents

| Document | What's inside |
|----------|---------------|
| [architecture.md](./architecture.md) | Components, boundaries, data flow, and constraints |
| [tech-landscape.md](./tech-landscape.md) | Languages, Android/Gradle stack, dependencies, and source-of-truth files |
| [entry-points.md](./entry-points.md) | Activities, services, helper process, tile, and HTTP entry points |
| [modules.md](./modules.md) | Responsibilities of the app's functional areas |
| [communication.md](./communication.md) | Binder/AIDL, broadcasts, and local HTTP/TCP protocol |
| [patterns.md](./patterns.md) | State, reflection, overlay, persistence, errors, and testing conventions |
| [coding-style.md](./coding-style.md) | Observed Java/XML naming and formatting conventions |
| [onboarding.md](./onboarding.md) | Build, activation, operation, and troubleshooting guidance |

## How to use this map

- New here? Read `onboarding.md`, then `architecture.md` and `communication.md`.
- Before changing behavior, read the map document for that entry point or component.
- Treat `app/src/main/aidl/com/tile/screenoff/IScreenOff.aidl` as the Binder contract and `app/src/main/AndroidManifest.xml` as the component/permission registry.

## Keeping this map current

Refresh the affected documents after changes to architecture, dependencies, entry points, AIDL/broadcasts/HTTP behavior, persistence, or conventions. Use `/codebase-mapper:update-codebase-map`; keep `.claude/.codebase-info/` atomic and do not copy signing secrets into documentation.
