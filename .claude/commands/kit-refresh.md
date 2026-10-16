---
description: Refresh gateway model discovery and verify Claude/GPT agent definitions after kit merges; optionally update Toolshed explicitly
argument-hint: "[--update-toolshed]"
---

Run this hybrid kit maintenance inline in the current project. Options: $ARGUMENTS
No paid test inference, feature tickets, nested Claude sessions, plugin enablement changes,
hook bypasses, agent-file generation or routing resets. Preserve unresolved merge blockers.

1. Locate the installed kit using `ANDROID_KIT_HOME`, then `kit_home` in
   `.claude/kit/state.json`, otherwise `$HOME/.local/share/android-kit-hybrid`. Read `.claude/kit/conflicts.json` and the installed
   plugin inventory (`claude plugin list --json`). Resolve plugin roots from the actual
   installed registry, respecting `CLAUDE_CONFIG_DIR` when configured. Prefer the local
   install for this project, then its project install, then user scope. Do not select
   another project's local install or a stale cache directory by version-name guessing.

2. If and only if `--update-toolshed` was explicitly supplied, invoke
   `/quartermaster:update-toolshed` using its installed skill. This updates active Toolshed
   installs across projects and can update/restart the shared gateway; state that scope
   before running it. Existing authorization for this explicit option is sufficient.
   Surface failures, then re-read installed paths. This updater does not update the kit's
   `android-hybrid` review plugins; stale bundled versions require the current kit setup.
   Without this option, use `/quartermaster:toolshed-doctor` for version/health findings.
   Do not upgrade every plugin simply because Markdown files were merged.

3. Audit configured hook overlaps without executing them:

   ```bash
   python3 "<KIT_ROOT>/lib/merge-settings.py" --audit-hooks \
     "<KIT_ROOT>/template/.claude/settings.json" \
     "<CLAUDE_CONFIG_DIR_OR_HOME_DOT_CLAUDE>/settings.json" \
     "$PWD/.claude/settings.json" "$PWD/.claude/settings.local.json"
   ```

   Substitute resolved absolute paths, not literal placeholders. Summarize event, source
   file/group and exact duplicate versus differing options. Do not print commands, tokens
   or environment values. The helper does not inspect plugin/managed hooks or count actual
   executions; use `/hooks` to locate those sources when relevant. Kit setup deduplicates
   exact known kit handlers within local settings. Conflicting custom options, cross-scope
   copies and unrelated hooks require review, not blind deletion.

4. Sync merged live-rules through the selected installed live-rules plugin:

   ```bash
   node "<LIVE_RULES_ROOT>/scripts/sync-atomic-rules.js" --project "$PWD"
   ```

   Skip only when the plugin is absent, and report that coverage. For Model Gateway use
   its stable launcher when present, otherwise its selected installed `bin/model-gateway.js`:

   ```bash
   node "<GATEWAY_CLI>" doctor
   node "<GATEWAY_CLI>" catalog --refresh --json
   ```

   These inspect health and refresh model/discovery caches; they do not send a paid model
   request. Inspect exit codes and readiness, not just a printed catalog: a failed refresh
   can still print old data. Surface auth, missing model and serving-version blockers.
   Do not change wiring, log in, restart the shared gateway or download a proxy implicitly.
   Follow the installed gateway skill's specific recovery only when that repair is requested.

5. Check `sidequest models --full --json` from this project and the kit routing audit.
   Keep board/ticket overrides visible. Sidequest 5.11.2's GPT routes use bundled
   `sidequest:sidequest-exec-dispatch` and `sidequest:sidequest-exec-dispatch-readonly`;
   the route marker selects the actual GPT model/effort. Do not generate one executor per
   GPT model or rewrite upstream agent files after a merge. Inspect the installed version's
   contract if it differs. If Sidequest is deliberately disabled for native reviews, report
   board dispatch as unavailable rather than enabling it.

6. Inspect only the frontmatter of the 14 installed `android-hybrid` review agent definitions:
   names, provider/client IDs and effort. Expect Terra/medium reviewers, Terra/high explicit
   simplification, Luna metadata, Sonnet comment specialists and Opus/medium finding validation;
   use the bundled definitions as the exact source. Check plugin versions against the installed kit's
   bundled manifests and official-namespace masks. Files on disk, available model rows and
   the current session's registered Agent types are three different checks. Verify actual
   registered types if the host exposes them; otherwise mark that check pending and ask the
   user to check `/agents` after restarting. Never claim loaded registration from disk alone.

7. Report catalog freshness/readiness, rule sync, hook overlaps, on-disk agent pins and
   loaded-agent status separately. After changes to commands/rules/plugins or gateway model
   discovery, require a full Claude Code exit and fresh start in this project. A refresh
   cannot replace a running session's loaded model registry. Run `/kit-doctor` in the new
   session; if nothing changed and registration is already verified, don't demand another
   restart. End with specific remaining blockers, without claiming inference was tested.
