# CLAUDE.md — ScreenOff

Turns the phone's display panel off while apps keep running (SurfaceControl reflection via Shizuku, like scrcpy), for users of screen mirroring and background tasks. Controlled from a QS tile, an accessibility service and a local HTTP/TCP server.
Android, developed on Ubuntu from the CLI.

- Origin: fork of WuDi-ZhanShen/ScreenOff @ 354045d · Integration branch: main
- Language: java (11 Java, 0 Kotlin) · UI: Views (1 XML layout) · screenshot tests: none
- JDK target 1.8 (source/target) · Gradle 8.9 · AGP 8.7.3 · Kotlin — · compileSdk 35 · targetSdk 35 · minSdk 21
- Modules: :app · Architecture: single module, no layering (Activity + services + AIDL Shizuku UserService) · DI/DB/Net: none / none / none (hand-rolled HTTP/TCP server)
- Deps: dev.rikka.shizuku:api/provider 13.1.0 · AIDL enabled · no flavors, single debug/release
- App id: com.tile.screenoff · Launcher: .MainActivity
- Baseline: blocked (environment: Google Maven returns 404, AGP can't resolve) · 0 unit tests, no test sources (PROGRESS.md)
## How work flows here
- **The main session orchestrates; it doesn't implement.** Work becomes Sidequest tickets, executors work in isolated worktrees on their returned Claude or GPT route, and the main session integrates verified results. Bounded inline-safe maintenance follows Sidequest's current rules.
- **Routing** (Sidequest profile `android-kit-hybrid`): Claude Opus/medium orchestrates. Follow current mixed Sidequest routes: Luna bounded work; Terra routine coding/UI/review; Opus debugging, hard coding, experiments and visual judgement; Sonnet research. Explicit category fallbacks may cross Claude/GPT; global fallback stays provider-scoped. Inspect effective routes and disclose overrides or provider blockers.
- **Big or multi-step changes:** plan mode → `/plan-audit` (routed review ticket with the Android checklist) → pin the plan as a Sidequest story → dispatch waves.
- **UI overhaul:** `/ui-overhaul <scope>`.
- **Bugs:** every ticket's checks must pass before integration; weak oracles, unchecked consumers and high-stakes changes require independent review. `/bug-hunt` uses the mixed review-audit route. Both native review plugins use GPT reviewers with Claude finding validation; temporarily disable Sidequest if it blocks them, keep Gateway enabled and restart.
- **End of a session or story:** `/wrap-up` turns corrections and decisions into rules, CLAUDE.md lines and the decision log (you approve each). Personal preferences go to auto memory ("remember that …").
- **Conventions** live in `.claude/live-rules/rules/` and are injected into every session and executor, so they're not repeated here. **Project map:** `.claude/.codebase-info/` (codebase-mapper).
- **Board:** "show the Sidequest board"; side issues mentioned mid-task get filed, not worked.
- **Plugin health / updates:** `/quartermaster:toolshed-doctor`, `/quartermaster:update-toolshed`. After a few weeks of real work: `/quartermaster:resupply`.
- **Compaction:** your last instructions are saved before compaction and re-injected after; re-read them before acting.

## Commands
- Build: `./gradlew :app:assembleDebug --console=plain -q` · Unit: `:app:testDebugUnitTest` · Lint: `:app:lintDebug`
- Screenshots: none (Views app, no screenshot framework)
- Release signing in `app/build.gradle` points at a Windows keystore path (`D:\\key.keystore`); only debug builds work here.
- Device: `./gradlew :app:installDebug -q && adb shell am start -n com.tile.screenoff/.MainActivity` · logs: `adb logcat -d --pid=$(adb shell pidof -s com.tile.screenoff) | tail -80`
- Emulator (no KVM on this host; prefer a physical device): `emulator -avd <AVD> -no-window -no-audio &` then `adb wait-for-device shell 'while [[ -z $(getprop sys.boot_completed) ]]; do sleep 1; done'`
