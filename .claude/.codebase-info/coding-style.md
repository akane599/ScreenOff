# Coding Style

*Last Updated: 2026-10-16*

## Tooling

The project uses the Gradle wrapper (`gradlew`/`gradlew.bat`) with Android Gradle Plugin configuration in `build.gradle` and `app/build.gradle`. No formatter, Java linter, or test-specific configuration was found in the mapped application tree. `app/build.gradle` enables Android lint but does not make release lint failures fatal.

## Conventions

| Kind | Convention | Example |
|------|------------|---------|
| Files | Java classes use package `com.tile.screenoff`; Android resources use lowercase snake_case or existing short identifiers | `MainActivity.java`, `main.xml`, `shortcuts.xml` |
| Types/Classes | PascalCase for most classes; Android component subclasses mirror their role | `MainActivity`, `GlobalService`, `ScreenController` |
| Functions | lowerCamelCase; Android lifecycle callbacks retain framework names | `setPowerMode`, `onServiceConnected` |
| Variables | lowerCamelCase; preference keys are short lowercase names | `displayToken`, `netControl`, `scrOffKey` |
| Constants | uppercase snake_case for numeric/protocol constants | `POWER_MODE_OFF`, `BUFFER_SIZE` |
| Resources | XML ids and preference-facing names are short lowercase identifiers; user text belongs in English `values/strings.xml`, with translations in `values-zh/` and `values-zh-rTW/` | `screenoff_switch`, `start_global_control` |

## Notes

The code is legacy Java with compact one-line conditionals, lambdas, anonymous listeners, and English comments and labels (the default locale is English since `9d7f82a`; Chinese lives only in translated resource folders). Preserve existing Android component naming and behavior when making focused changes; notably, `tileService` is intentionally lowercase in its class/file name and manifest reference. Hidden framework APIs are isolated in `ScreenController` and `DisplayControl` and should not be spread into UI/service code.
