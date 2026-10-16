# Android plan audit checklist

Used by `/plan-audit` review tickets. Read the plan, then CLAUDE.md's STACK block, then check the repo. Do not edit anything.

**A. Grounded in reality (verify against the repo, cite `path:line`)**
1. Every file the plan says it will modify exists — or the plan says it's new. Every function/class it references exists with that name.
2. The plan's assumptions about architecture match `CLAUDE.md`'s STACK block (DI framework, DB, navigation, UI toolkit, min/compile SDK). Flag any step that assumes Compose in an XML app, Hilt in a Koin app, etc.
3. Scope: list anything in the plan that isn't required by the stated goal (scope creep), and anything the goal requires that no step delivers (gap).

**B. Android-specific omissions (mark each: covered / not needed / MISSING)**
- Manifest changes: permissions (+ runtime request flow for dangerous ones), new Activities/Services/Receivers, `exported` flags, deep links.
- API-level gating: any API above `minSdk` guarded (`Build.VERSION.SDK_INT`) or backported.
- Persistence: Room/SQLDelight schema change → migration + migration test; DataStore/SharedPrefs key changes → read-old-write-new.
- Config changes / process death: state survives rotation and restore (`SavedStateHandle`, `rememberSaveable`).
- Threading: no blocking IO on main; coroutine scope is lifecycle-bound; Flow collection uses `repeatOnLifecycle`/`collectAsStateWithLifecycle`.
- Strings/resources: user-visible text goes to `strings.xml`; new drawables have density or vector variants; RTL and large-font tolerance.
- Compose (if applicable): new state classes immutable (`compose-stability`), new screens get previews (light/dark/fontScale), tokens from theme only (`compose-design`).
- Release impact: ProGuard/R8 keep rules for reflection/serialization; `google-services`/Firebase config; version catalog updated rather than inline versions.
- Backward compatibility: existing users upgrading — data, cached files, notification channels, widget/shortcut IDs.
- Security: nothing new writes secrets to logs, prefs, or source; network calls stay on HTTPS; new `exported` components validated.

**C. Executability**
1. Each task has a **verification step with a command** (`./gradlew … testDebugUnitTest`, a screenshot, an adb check). A task ending in "verify it works" with no command is a MISSING.
2. Steps meant to run in parallel don't touch the same files, and at most 2 of them run Gradle at once. List collisions.
3. Order: no task depends on a later one. Theme/tokens before screens; schema before DAO before repo before UI.
4. Rollback: can the change be reverted with `git revert` alone, or does it leave migrated data / renamed keys behind? Say which.
5. Size: any single task that touches >8 files or >3 modules should be split. Name them.
6. Ticket shape: can each step become a Sidequest ticket with a declared file scope and an exact Gradle `--verify`? Name steps that can't.

**D. Contrarian pass**
Pick the 3 decisions in the plan most likely to be wrong and argue the alternative in 2 lines each. The main context decides; the auditor only argues.

## Report format (your done/submit body)
```
PLAN AUDIT — <plan name>            Verdict: APPROVE / APPROVE WITH FIXES / REWORK
Blockers (must fix before execution):
  - <item> — <path:line or reason>
Fixes (add to plan):
  - <item>
Not needed (auditor checked, fine as is):
  - <B-items marked not needed, one line>
Collisions: <none | task X & Y both edit path>
Contrarian: 1) … 2) … 3) …
```
