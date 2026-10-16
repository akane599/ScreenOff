---
description: Implementation baseline for ScreenOff changes
priority: 95
---
- Trace the flow first (`.claude/.codebase-info/` names the files). Decide whether the change needs to exist, then extend an existing seam (`ScreenController` + `IScreenOff.aidl` for privileged display work, `GlobalService` for overlay/key/orientation/LAN control, `SimpleTcpServer`/`HttpRequestParser` for the HTTP surface, `MainActivity` for settings and activation) before adding classes.
- Smallest fix at the shared root. Prefer deletion and direct calls over new wrappers, flags, guards or layers.
- Keep the safety floors: `ScreenController`'s root/shell uid check, the pre-Q/Q+/API 34+ display code paths, the LAN server off by default, accessibility and Shizuku permission flows, and no signing values or other secrets in tracked files.
- Changing `IScreenOff.aidl` or the `intent.screenoff.sendBinder` handoff touches both the helper process and the app: update both sides together and say so in the ticket.
- Mark a deliberate simplification `whittle:` with its limit and upgrade trigger.
- Non-trivial pure-Java logic leaves a JVM unit test; never claim helper-process or device behavior was verified from a JVM test.
