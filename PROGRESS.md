# PROGRESS.md — ScreenOff

_Work items, stories and blockers live on the Sidequest board. This file keeps what outlives tickets._

## Baseline
- 2026-10-16 @ 354045d: build blocked by environment, not code. `dl.google.com` Maven returns HTTP 404 for every artifact (AGP 8.7.3, androidx, SDK index), so `com.android.application` 8.7.3 cannot resolve; `--offline` also fails. 0 unit tests (no `app/src/test`, no test deps). Re-run `bash .claude/kit/gradle-check.sh :app:assembleDebug :app:testDebugUnitTest --continue` once Google Maven is reachable.
- 2026-10-16 @ d7d44a8: Google Maven reachable again; build now fails in code at project evaluation: `app/build.gradle:21` `storeFile file('D:\\key.keystore')` → `URISyntaxException: Illegal character in opaque part at index 2` on Linux. Blocks every task (assembleDebug, testDebugUnitTest). Pre-existing, not fixed. 0 unit tests.
- 2026-10-17 @ fb8bbae: assembleDebug/assembleRelease/testDebugUnitTest pass (signing optional via untracked keystore.properties; 9 HttpRequestParser JVM tests). Device e2e (ADB activation → helper → ScrOff/ScrOn → QS tile → exit) passes on API 35, 36, 37 emulators (google_apis x86_64).
- 2026-10-17 @ af56728: assembleDebug + testDebugUnitTest pass (11 JVM tests: HttpRequestParser 9, HttpRequest 2). Device e2e (dialog command → helper from APK → ScrOff/ScrOn → tile enables a11y → exit, plus LAN token checks on 37) passes on API 35/36/37. Pre-release v21-fork.1 (debug APK) published; CI `Build APK` uploads the debug APK as an artifact on every push to main.

## Decision log (never delete; one line each)
- 2026-10-16: Adopted android-kit (hybrid) at 354045d. Java/Views fork; kit Compose conventions apply only to new Compose work.
- 2026-10-16: App UI is English by default (values/), original Chinese kept in values-zh / values-zh-rTW; no Turkish.
- 2026-10-17: Android 16/17: no targetSdk bump (stays 35) and Shizuku stays 13.1.0 (13.1.5 makes newProcess private). Fixed only what broke: tile PendingIntent on API 34+, GlobalService control receiver registration (NOT_EXPORTED), helper broadcast appOp 0→-1 (API 37 enforced COARSE_LOCATION), control broadcasts made package-explicit (implicit ones reached no NOT_EXPORTED receiver on API 37).
- 2026-10-17: API 37 Advanced Protection Mode disables non-tool accessibility services; accepted as a known limitation, no workaround.
- 2026-10-17: Helper Binder lockdown: ScreenController takes the app uid as its argument and checks `Binder.getCallingUid()` in every method (primary protection); the sticky sendBinder broadcast is package-explicit and carries no permissions (AM refuses sticky + requiredPermissions, and sticky replay arrives as uid -1, so a receiver broadcastPermission breaks it). A spoofed BinderContainer can only cause denial of service (whittle in GlobalService).
- 2026-10-17: LAN server requires a per-install token (`?t=`, 403 otherwise), caps headers at 8 KiB (431), and uses a 10 s socket timeout with independent connections. It stays off by default.
- 2026-10-17: Activation no longer extracts starter.sh/dex to external storage. The command copied from the dialog is `adb shell '<cmd>'` and builds CLASSPATH from `pm path` (the first entry covers split APKs). Exported ScrOff/ScrOn only open accessibility Settings (in a new task); only the tile and MainActivity write enabled_accessibility_services.
- 2026-10-17: CI builds the debug APK only, because release signing needs an untracked keystore.properties. Old keystore values remain in public git history; rotating the key is the owner's call.
- 2026-10-17: Owner rotated the release key: new RSA-4096 PKCS12 key (alias `screenoff`, CN=ScreenOff, O=akane599, cert SHA-256 `451403bc2e6f7c0c7a1631517095f1431f7dcf1379b6a48b4ad1fb27d8cb56f7`), kept outside the repo and referenced only by the untracked `keystore.properties`. The leaked old key is retired.
- 2026-10-17: Owner confirmed the API 37 Advanced Protection Mode accessibility block is expected behavior; no workaround planned.

## Audit status
| Area | Last run | Result | How |
|---|---|---|---|
| Plan audit | — | — | `/plan-audit` |
| Bug hunt | — | — | `/bug-hunt` |
| Security | 2026-10-17 | 5 findings fixed (Binder uid check, LAN token/limits, helper loaded from APK, shortcuts no longer enable a11y); BinderContainer spoof DoS accepted | manual review ticket + SQ-5/6/7/9 |
| UI / design | — | — | `/ui-overhaul` phase review |
| Lint | — | — | `bash .claude/kit/gradle-check.sh :app:lintDebug` |
| Device QA | 2026-10-17 @ af56728 | pass on API 35/36/37 | adb e2e (dialog command, shortcuts, tile, exit, LAN on 37) |

## Environment notes
- 2026-10-16: KVM now available (detect-stack `kvm=yes`), so a hardware-accelerated emulator is possible; Shizuku/ADB activation still needed for runtime checks.
- 2026-10-16: Google Maven (dl.google.com) returned 404 for all artifacts earlier in the day; reachable again by the d7d44a8 baseline.
