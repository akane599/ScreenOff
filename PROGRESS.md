# PROGRESS.md — ScreenOff

_Work items, stories and blockers live on the Sidequest board. This file keeps what outlives tickets._

## Baseline
- 2026-10-16 @ 354045d: build blocked by environment, not code. `dl.google.com` Maven returns HTTP 404 for every artifact (AGP 8.7.3, androidx, SDK index), so `com.android.application` 8.7.3 cannot resolve; `--offline` also fails. 0 unit tests (no `app/src/test`, no test deps). Re-run `bash .claude/kit/gradle-check.sh :app:assembleDebug :app:testDebugUnitTest --continue` once Google Maven is reachable.
- 2026-10-16 @ d7d44a8: Google Maven reachable again; build now fails in code at project evaluation: `app/build.gradle:21` `storeFile file('D:\\key.keystore')` → `URISyntaxException: Illegal character in opaque part at index 2` on Linux. Blocks every task (assembleDebug, testDebugUnitTest). Pre-existing, not fixed. 0 unit tests.

## Decision log (never delete; one line each)
- 2026-10-16: Adopted android-kit (hybrid) at 354045d. Java/Views fork; kit Compose conventions apply only to new Compose work.

## Audit status
| Area | Last run | Result | How |
|---|---|---|---|
| Plan audit | — | — | `/plan-audit` |
| Bug hunt | — | — | `/bug-hunt` |
| Security | — | — | `/claude-security` |
| UI / design | — | — | `/ui-overhaul` phase review |
| Lint | — | — | `bash .claude/kit/gradle-check.sh :app:lintDebug` |
| Device QA | — | — | android-emulator-qa skill |

## Environment notes
- 2026-10-16: KVM now available (detect-stack `kvm=yes`), so a hardware-accelerated emulator is possible; Shizuku/ADB activation still needed for runtime checks.
- 2026-10-16: Google Maven (dl.google.com) returned 404 for all artifacts earlier in the day; reachable again by the d7d44a8 baseline.
