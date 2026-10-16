---
description: Android build, test and dependency rules
priority: 20
---
- Gradle: always preserve its exit code. From the checkout root use `bash .claude/kit/gradle-check.sh <tasks>` (full log retained, last 80 lines on failure), or raw `./gradlew <tasks> --console=plain -q` if the helper is absent. Never use a grep/head pipeline as the pass/fail oracle.
- Verify with the narrowest task: `./gradlew :<module>:testDebugUnitTest --tests '<Class>' --console=plain -q`; UI changes add `:<module>:validateDebugScreenshotTest` (only UI tickets update refs, via `updateDebugScreenshotTest`).
- One Gradle run per change-set, not per file.
- Never bump compileSdk/targetSdk, AGP, Gradle, Kotlin or the JDK target, and never add a dependency, without it being in the ticket. For new apps use `gradle/libs.versions.toml`; adopted projects keep their existing catalog/build-logic/versioning layout.
- The SDK comes from `ANDROID_HOME`. Don't create or edit `local.properties`.
- Never read, print or commit keystores (`*.jks`, `*.keystore`), `local.properties`, `google-services.json`, `.env*`, `.claude-plugin-config.json`.
- Library APIs: check context7 instead of recalling from memory.
