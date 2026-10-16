---
description: Independent audit of an implementation plan before you approve it, as a read-only plan-review ticket (Claude Opus/medium, fresh independent context) with the Android checklist
argument-hint: "[plan file path | blank = newest plan in ~/.claude/plans or the plan in this conversation]"
---

Audit the plan before it becomes tickets. Target: $ARGUMENTS

1. **Resolve the plan:** the given path → else the newest `~/.claude/plans/*.md` → else the plan most recently presented in this conversation. In that last case, write it to `~/.claude/plans/<slug>.md` first. Use absolute paths from here on; executors run in worktrees outside this checkout.

2. **File one read-only review ticket** (Sidequest skill rules apply: classify, then dispatch):
   - title `Plan audit: <plan name>`, category `plan-review` (android-kit-hybrid profile: Claude Opus/medium; if unavailable, inspect any configured fallback and disclose it; if the category is missing, repair profile selection before dispatch), `--readonly true`, no declared files;
   - description: the absolute plan path; the absolute path of `$CLAUDE_PROJECT_DIR/.claude/kit/android-plan-checklist.md`; "Audit the plan with that checklist against this repository. Report in the checklist's report format as your closing body. Do not edit files."
   - If the plan touches data migrations, security or release/signing, add `--high-stakes` and file a **second** ticket in category `review-audit` (GPT Terra/medium) focused on that risk: for example migration recovery, auth boundaries or signing/release integrity. Give it the raw plan, checklist and relevant repo paths, not the first reviewer's conclusions. It verifies that distinct concern rather than duplicating the entire plan audit. File count alone does not require another reviewer; broad mechanical changes can still use one. Honor an explicit request for a second full review. `sidequest models` shows where each resolved; report actual model/effort and coverage. Use fresh contexts and separate evidence gathering. Report the actual providers; a fallback may remove provider diversity.

3. **Dispatch and wait.** Don't audit it yourself in the meantime; you wrote the plan and would defend it.

4. **Merge and apply.** Read the closing report(s) (`sidequest comments <ref>`). With two reports:
   - flagged by both → keep;
   - flagged by one → check it in the repo yourself and keep it only if it holds;
   - different verdicts → resolve against repo evidence and the reviewers' different scopes; retain any supported blocker and explain why. Do not promote unsupported caution into a blocker.

   Apply Blockers and Fixes to the plan file (plan edits, not code), put `audited: <date> (<refs>)` at the top, and add one line to PROGRESS.md's decision log and audit table.

5. **Tell the user** the verdict in ≤5 lines. Once they approve, pin the plan as a Sidequest story (see the `user-story` skill) and file the ticket waves.

For adopted projects, discover the actual module, build variant and test framework first. The `:app`/`Debug`/Compose screenshot commands above are examples for the generated scaffold, not universal commands. Keep existing Paparazzi, Roborazzi, XML or device-based checks; report unavailable visual validation explicitly.
