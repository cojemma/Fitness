# CLAUDE.md

## Project Overview

Fitness SDK — an Android library (Kotlin) providing workout tracking, exercise library, and template management. Includes a sample app demonstrating SDK usage.

## Tech Stack

- **Language:** Kotlin 1.9.22
- **Android:** Target SDK 34, Min SDK 29
- **UI:** Jetpack Compose + Material 3
- **Database:** Room 2.6.1
- **Async:** Kotlin Coroutines + Flow
- **Architecture:** Clean Architecture (Domain → Data → Presentation), MVVM in sample app
- **Build:** Gradle 8.4+ with Kotlin DSL, AGP 8.2.2, KSP 1.9.22-1.0.17

## Module Structure

- `fitness-sdk/` — Android Library module (SDK core)
- `sample-app/` — Android Application module (demo app, depends on fitness-sdk)

## Build Commands

```bash
./gradlew :fitness-sdk:build          # Build SDK
./gradlew :sample-app:assembleDebug   # Build sample app (debug)
./gradlew clean build                 # Clean + full build
```

## Testing

```bash
./gradlew :fitness-sdk:test                                    # All unit tests
./gradlew :fitness-sdk:test --tests "SaveWorkoutUseCaseTest"   # Specific test class
./gradlew :fitness-sdk:connectedAndroidTest                    # Instrumentation tests
```

- **Framework:** JUnit 4, MockK 1.13.8, kotlinx-coroutines-test 1.7.3
- **Naming:** `*Test.kt` suffix
- **Pattern:** Given-When-Then; async tests use `runTest { }`
- **Location:** Unit tests in `src/test/kotlin/`, instrumentation in `src/androidTest/kotlin/`

## Code Conventions

- **Style:** `kotlin.code.style=official` (Kotlin official style guide)
- **No linter configured** (no detekt, ktlint, or spotless)
- **Classes:** PascalCase — `WorkoutManager`, `FitnessSDK`
- **Functions:** camelCase — `saveWorkout()`, `observeWorkouts()`
- **Constants:** UPPER_SNAKE_CASE
- **Interfaces:** "Manager" suffix for public API (`WorkoutManager`, `TemplateManager`)
- **Implementations:** "Impl" suffix (`WorkoutManagerImpl`)
- **Use Cases:** Action + Noun + UseCase (`SaveWorkoutUseCase`, `DeleteWorkoutUseCase`)
- **Models:** `data class` for domain models with default values
- **Error handling:** `Result<T>` return type from use cases
- **Reactive:** `Flow<T>` for observable data, `StateFlow` in ViewModels
- **DI:** Manual constructor injection (no DI framework)

## Architecture

```
Presentation (sample-app)     → ViewModels + Compose Screens
Domain (fitness-sdk/domain)   → Models, Use Cases, Repository interfaces
Data (fitness-sdk/data)       → Room DB, DAOs, Entities, Mappers, Repository impls
```

**Key patterns:** Singleton (FitnessSDK object), Repository, Use Case, Mapper (domain ↔ entity), Builder (FitnessSDKConfig)

## SDK Entry Points

```kotlin
FitnessSDK.initialize(context) { databaseName("fitness_db"); enableLogging(true) }
FitnessSDK.getWorkoutManager()          // Workout CRUD + exercise session counts
FitnessSDK.getExerciseLibraryManager()  // 55+ predefined exercises + custom exercises
FitnessSDK.getTemplateManager()         // Template management
```

## Localization

- **Supported languages:** English (`values/strings.xml`), Traditional Chinese (`values-zh-rTW/strings.xml`)
- **Runtime switching:** `AppCompatDelegate.setApplicationLocales()` — requires `AppCompatActivity`
- **Config:** `res/xml/locales_config.xml` for Android 13+ system per-app language settings
- **Manifest:** `AppLocalesMetadataHolderService` with `autoStoreLocales=true` for pre-Android 13 persistence
- **Theme:** Must use `Theme.AppCompat.*` in AndroidManifest (not `Theme.Material.*`) — Compose layers Material 3 on top
- **Shared utility:** `StringResUtil.kt` — `getMuscleGroupStringRes()` for muscle group name localization
- **Settings screen:** `ui/settings/SettingsScreen.kt` — language picker and calendar view picker dialogs, accessible via gear icon in TopAppBar

## Room DAO Guidelines

- **Never use `@Insert(onConflict = OnConflictStrategy.REPLACE)` for batch inserts** where entities have `id = 0` (auto-generate). `REPLACE` causes later entities to overwrite earlier ones sharing the same PK=0. Use `@Insert` (default `ABORT` strategy) instead — Room auto-generates unique IDs correctly.
- Single-entity inserts with `REPLACE` are safe when used for upsert semantics (e.g., `insertTemplate`).

## Exercise List Sorting

- **Default sort:** Exercise list page sorts by done times (session count, descending) — most-performed exercises appear first.
- **SDK API:** `WorkoutManager.getExerciseSessionCounts()` returns `Result<Map<String, Int>>` — exercise name to distinct workout session count. `observeExerciseSessionCounts()` returns `Flow<Map<String, Int>>` for reactive updates.
- **DAO query:** `ExerciseDao.getExerciseSessionCounts()` (one-shot) and `observeExerciseSessionCounts()` (Flow) use `COUNT(DISTINCT workoutId)` grouped by exercise name for efficiency.
- **Sorting persists through filters:** Search and muscle group filters also apply the session count sort order.
- **Reactivity:** ViewModel collects session counts via Flow. When counts change, the exercise history cache is invalidated so expanded details refresh on next access.

## Custom Exercises

- **Architecture:** `CompositeExerciseLibraryProvider` merges `DefaultExerciseLibrary` (in-memory predefined) with `CustomExerciseDao` (Room-persisted custom exercises). All `ExerciseLibraryProvider` methods transparently return both.
- **Cache:** `CompositeExerciseLibraryProvider` maintains a `@Volatile cachedCustomExercises` list for synchronous interface methods, updated via `refreshCustomExercises()` (called at SDK init) and `observeAllExercises()` Flow.
- **Entity:** `CustomExerciseDefinitionEntity` in `custom_exercise_definitions` table. Secondary muscles stored as JSON string (same pattern as `TemplateMapper`).
- **Mapper:** `CustomExerciseMapper` sets `isCustom = true` on domain model.
- **Validation:** `SaveCustomExerciseUseCase` checks name not blank and uniqueness against both predefined library and existing custom exercises.
- **SDK API:** `ExerciseLibraryManager.saveCustomExercise()`, `deleteCustomExercise()`, `observeAllExercises()`.
- **UI:** `CreateCustomExerciseScreen` — full form (name, category dropdown, primary muscle dropdown, secondary muscle chips, description, instructions, time-based toggle, default sets/reps/duration). FAB on Exercise List and "+" button on Exercise Picker navigate to this screen. Custom exercises show "Custom" badge and delete button in lists.
- **Database version:** v5 (added `CustomExerciseDefinitionEntity`, destructive migration).

## Calendar View (Home Screen)

- **Settings:** `CalendarViewType` enum (`NONE`, `WEEKLY`, `MONTHLY`) persisted via `SharedPreferences` in `PreferencesManager`.
- **Preference key:** `"calendar_view_type"` in `"fitness_sample_prefs"` SharedPreferences file.
- **Default:** `NONE` (no calendar, preserves original workout list behavior).
- **UI components:** `WeeklyCalendarView` and `MonthlyCalendarView` in `ui/components/WorkoutCalendar.kt` — pure Compose, no 3rd-party calendar library.
- **Weekly view:** Single-row 7-day layout (Sun–Sat) with left/right week navigation. Dot indicators on days with workouts. Tap to filter workout list by date.
- **Monthly view:** Full month grid with month navigation. Same dot indicators and tap-to-filter behavior.
- **Date filtering:** `HomeViewModel.selectedDate` (nullable `LocalDate`). Tap a day to filter; tap again to deselect. `filteredWorkouts` StateFlow combines `workouts` + `selectedDate` via `combine`. Weekly stats always use unfiltered workouts.
- **ViewModel:** `HomeViewModel` extends `AndroidViewModel` (needs app context for `PreferencesManager`). `refreshCalendarViewType()` re-reads preference on screen resume (e.g., after returning from Settings).
- **Settings integration:** `SettingsScreen.kt` — "Calendar View" row with `CalendarMonth` icon + `CalendarViewPickerDialog` (3 radio options).

## Exercise Navigator & Reorder (Active Workout)

- **Navigator Rail:** `ExerciseNavigatorRail.kt` — horizontal `LazyRow` of exercise chips placed between progress bar and current exercise card during active workout. Each chip shows exercise number (or checkmark if complete), truncated name, and set progress (e.g., "2/4"). Tapping jumps to that exercise. Trailing reorder button opens bottom sheet.
- **Reorder Sheet:** `ExerciseReorderSheet.kt` — `ModalBottomSheet` with draggable exercise list. Long-press + drag to reorder; tap to jump to exercise.
- **State:** `SessionStateManager.goToExercise(index)` jumps to exercise and resumes at next uncompleted set. `reorderExercises(fromIndex, toIndex)` moves exercise, remaps `completedSets` keys (index-based `Map<Int, List<SetLogEntry>>`), and adjusts `currentExerciseIndex` to follow the viewed exercise.
- **ViewModel:** `ActiveWorkoutViewModel.goToExercise()` and `reorderExercises()` delegate to SessionStateManager; `goToExercise` also cancels rest timer.
- **No `finishWorkout()` changes needed:** It already uses `mapIndexed` on the synced exercise list + completedSets map.

## Git Conventions

- **Branch:** `master`
- **Commit style:** Conventional commits — `feat:`, `refactor:`, `fix:` prefixes
- **No CI/CD configured**
