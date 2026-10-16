# PROGRESS.md — ScreenOff

_Work items, stories and blockers live on the Sidequest board. This file keeps what outlives tickets._

## Baseline
- 2026-10-16 @ 354045d: build blocked by environment, not code. `dl.google.com` Maven returns HTTP 404 for every artifact (AGP 8.7.3, androidx, SDK index), so `com.android.application` 8.7.3 cannot resolve; `--offline` also fails. 0 unit tests (no `app/src/test`, no test deps). Re-run `bash .claude/kit/gradle-check.sh :app:assembleDebug :app:testDebugUnitTest --continue` once Google Maven is reachable.
- 2026-10-16 @ d7d44a8: Google Maven reachable again; build now fails in code at project evaluation: `app/build.gradle:21` `storeFile file('D:\\key.keystore')` → `URISyntaxException: Illegal character in opaque part at index 2` on Linux. Blocks every task (assembleDebug, testDebugUnitTest). Pre-existing, not fixed. 0 unit tests.
- 2026-10-17 @ fb8bbae: assembleDebug/assembleRelease/testDebugUnitTest pass (signing optional via untracked keystore.properties; 9 HttpRequestParser JVM tests). Device e2e (ADB activation → helper → ScrOff/ScrOn → QS tile → exit) passes on API 35, 36, 37 emulators (google_apis x86_64).

## Decision log (never delete; one line each)
- 2026-10-16: Adopted android-kit (hybrid) at 354045d. Java/Views fork; kit Compose conventions apply only to new Compose work.
- 2026-10-16: App UI is English by default (values/), original Chinese kept in values-zh / values-zh-rTW; no Turkish.
- 2026-10-17: Android 16/17: no targetSdk bump (stays 35) and Shizuku stays 13.1.0 (13.1.5 makes newProcess private). Fixed only what broke: tile PendingIntent on API 34+, GlobalService control receiver registration (NOT_EXPORTED), helper broadcast appOp 0→-1 (API 37 enforced COARSE_LOCATION), control broadcasts made package-explicit (implicit ones reached no NOT_EXPORTED receiver on API 37).
- 2026-10-17: API 37 Advanced Protection Mode disables non-tool accessibility services; accepted as a known limitation, no workaround.

## Audit status
| Area | Last run | Result | How |
|---|---|---|---|
| Plan audit | — | — | `/plan-audit` |
| Bug hunt | — | — | `/bug-hunt` |
| Security | 2026-10-17 | 5 findings open (Binder exposure/spoofing, LAN auth/DoS, legacy-storage helper TOCTOU, exported a11y-enabling shortcuts) | manual review ticket |
| UI / design | — | — | `/ui-overhaul` phase review |
| Lint | — | — | `bash .claude/kit/gradle-check.sh :app:lintDebug` |
| Device QA | 2026-10-17 | pass on API 35/36/37 | manual adb e2e (activation, shortcuts, tile, exit) |

## Environment notes
- 2026-10-16: KVM now available (detect-stack `kvm=yes`), so a hardware-accelerated emulator is possible; Shizuku/ADB activation still needed for runtime checks.
- 2026-10-16: Google Maven (dl.google.com) returned 404 for all artifacts earlier in the day; reachable again by the d7d44a8 baseline.
