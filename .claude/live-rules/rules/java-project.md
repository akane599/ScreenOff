---
description: ScreenOff is a Java/Views app; keep new code in its language and UI toolkit
priority: 90
---
- Write new and changed code in Java at the project's Java 8 source level (`compileOptions` in `app/build.gradle`), matching the surrounding class's naming and formatting. The kit's Kotlin/Compose conventions do not apply here unless a ticket explicitly adopts Kotlin or Compose.
- UI stays Android Views/XML (`app/src/main/res/layout/`); user-visible text goes in `res/values/strings.xml` (keep translated `values-*` folders in step when present).
- Adding Kotlin, Compose, a new library, or raising the Java level needs its own ticket; never as a side effect of another change.
- `ScreenController` runs as a standalone `app_process` dex: it may only use framework classes (reflection allowed), not AndroidX or app-process-only APIs.
