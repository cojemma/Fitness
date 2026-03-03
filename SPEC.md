# Project Specification

## Project Goal

To provide a comprehensive, developer-friendly Android Fitness SDK that simplifies the creation of fitness tracking applications. The project includes a fully functional Sample App to demonstrate best practices, Clean Architecture, and the SDK's capabilities in a real-world scenario. The app targets users interested in strength training, progressive overload, and structured workout templates.

## Tech Stack

- **Language**: Kotlin 1.9.21
- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture Pattern**: Clean Architecture (Presentation, Domain, Data)
- **Dependency Injection**: Manual (via generic Singleton/Factory pattern in SDK) / Hilt (potential future, currently not explicitly mentioned but common in this stack)
- **Database**: Room Persistence Library 2.6.1 (SQLite)
- **Concurrency**: Kotlin Coroutines + Flow
- **Build System**: Gradle (Kotlin DSL)
- **Navigation**: Jetpack Navigation Compose
- **Target SDK**: Android 34

## Architecture

The project is structured as a multi-module Gradle project:

### 1. `fitness-sdk` (Android Library)

Encapsulates all business logic and data persistence.

- **API Layer**: Public interfaces (`WorkoutManager`, `TemplateManager`, `ExerciseLibraryManager`) exposed to the app.
- **Domain Layer**:
  - **Models**: `Workout`, `Exercise`, `WorkoutTemplate`.
  - **Use Cases**: Encapsulated business rules (e.g., `SaveWorkoutUseCase`).
  - **Repositories**: Interfaces for data access.
- **Data Layer**:
  - **Local**: Room Database definition, DAOs, Entities, Mappers.
  - **Repository Impl**: Concrete implementation of repository interfaces.

### 2. `sample-app` (Android Application)

A reference implementation using the SDK.

- **UI Layer**: Jetpack Compose screens (`HomeScreen`, `WorkoutDetailsScreen`, `TemplateListScreen`, `CreateCustomExerciseScreen`, `SettingsScreen`).
- **ViewModels**: State management for screens, interacting with SDK Managers.
- **Data Layer**: `PreferencesManager` wrapping `SharedPreferences` for app-level settings (e.g., calendar view type).
- **Navigation**: `FitnessNavGraph` managing screen transitions.

## The Feature Loop

### Current State

**Version 1.15.0**

- **Template Set Data Adherence**: In the active workout page, the input fields for reps and weight now accurately retrieve their targets from the specific set records defined in the template (e.g., varying weights/reps across sets), instead of defaulting to the exercise's overall aggregate values.
- **Set Change Propagation**: When a user logs a set with modified reps or weight, all remaining (uncompleted) sets of the same exercise automatically update their targets to match the new values.

**Version 1.14.0**

- **Rest Timer on Last Set & Persistence**: The rest timer is now correctly triggered after the final set of an exercise (unless it's the final set of the entire workout). Additionally, the countdown timer persists and continues running even when the user manually navigates between different exercises (e.g., via the exercise chips or next/previous arrows). This ensures the rest cadence is maintained while preparing for the next movement.

**Version 1.13.0**

- **Export Workout History (CSV)**: Users can export workout history as a CSV file from the Workouts page via a download icon in the top bar. A date-range picker offers presets (7 days, 30 days, 3 months, all time). The CSV is generated via `ExportWorkoutHistoryCsvUseCase` with one row per exercise set, including date, workout name, type, duration, exercise name, set number, weight, reps, warmup flag, and volume. The file is shared via Android Share Sheet using `FileProvider`. Both English and Traditional Chinese localization are supported.

**Version 1.12.0**

- **Rest Timer Countdown Alert**: Audible beep (via `ToneGenerator`) and visual cues during the last 5 seconds of the rest timer. Timer text turns red with a pulsing scale animation. A distinct tone plays when rest ends. Beep stops immediately when "Skip Rest" is pressed.

**Version 1.11.0**

- **Number Input UX**:
  - **Fluent Typing**: Replaced `String` state with `TextFieldValue` and `TextRange` in `ActiveWorkoutScreen` and `TemplateExerciseCard` to prevent cursor jumping during editing.
  - **Steppers (Active Workout)**: Added +/− buttons for quick adjustments (Reps ±1, Weight ±2.5kg) in active workout session.
  - **Input Filtering**: Regex-based validation restricts non-numeric input.
  - **Layout Improvements**: Optimized input rows for varying screen widths; "SpaceEvenly" layout for active workout; compact single-row layout for template editing.
  - **Warmup Toggle**: Restored compact "W" column in template editor.

**Version 1.10.0**

- **Core CRUD**: Workouts and Exercises can be created, read, updated, and deleted.
- **Exercise Library**: Pre-loaded library of 55+ exercises with categorization.
- **Custom Exercises**: Users can create their own exercises with full details (name, category, primary/secondary muscles, description, instructions, time-based toggle, default sets/reps/duration). Custom exercises are persisted in Room and appear alongside predefined exercises in all library views, pickers, and filters. `CompositeExerciseLibraryProvider` transparently merges predefined + custom exercises. Reactive `observeAllExercises()` Flow keeps UI in sync.
- **Exercise List Page**: "Exercises" tab listing all exercises with expandable history. Each exercise shows total sessions, max weight, estimated 1RM (Epley formula), and session-by-session history. Tap a session to navigate to workout details. **Default sort by done times** (most-performed exercises appear first).
- **Exercise Session Counts API**: `WorkoutManager.getExerciseSessionCounts()` returns a map of exercise name to workout session count, enabling efficient sorting without loading full history. `observeExerciseSessionCounts()` provides a reactive Flow for real-time UI updates when workout data changes.
- **Template System**:
  - Create/Edit templates with per-set reps/weight targets.
  - Start active workout from template — each set pre-fills with its own template-defined target.
  - `ActiveWorkoutScreen` with timer and set logging.
  - **Per-Set Template Targets**: `StartWorkoutFromTemplateUseCase` converts every `TemplateSet` into an `ExerciseSet` record; `SessionStateManager.getTargetReps()`/`getTargetWeight()` read per-set data.
  - **Set Change Propagation**: When a user modifies reps/weight from the template target, remaining sets auto-update. Unchanged values preserve the original per-set template targets.
  - **Last Session Data**: Pre-loads previous workout performance for progressive overload tracking using actual per-set records. Used as fallback when template sets have no explicit target.
  - **Save as Template**: Options to replace original template, save as new, or skip after workout completion.
- **Per-Set Recording**: Individual set records (weight, reps) stored in database via `ExerciseSet` entity.
- **Data Persistence**: Robust local storage using Room.
- **Architecture Refactor**:
  - **Active Workout**: Split logic into `ActiveWorkoutViewModel` (Coordination), `TimerManager` (Time tracking), and `SessionStateManager` (Exercise/Set logic) for better maintainability.
  - **SDK**: Added `addExerciseToWorkout` capability for dynamic workout modification.
- **Dynamic Exercise Addition**: Users can add exercises to an active workout session via FloatingActionButton, selecting from the exercise library. Exercises are added to the in-memory workout and immediately available for logging sets.
- **Calendar View**: Home screen supports optional Weekly or Monthly calendar overlay above the workout list. Calendar days with workouts show dot indicators; tapping a day filters the workout list to that date (tap again to deselect). Calendar type (None/Weekly/Monthly) is configurable in Settings and persisted via `SharedPreferences`. Built with pure Compose (no 3rd-party calendar library). `HomeViewModel` extends `AndroidViewModel` for access to `PreferencesManager`.
- **Settings Screen**: `SettingsScreen` with language picker (English/Traditional Chinese via `AppCompatDelegate.setApplicationLocales()`) and calendar view type picker. Accessible via gear icon in Home screen TopAppBar.
- **Exercise Navigator & Reorder**: During active workout, a horizontal exercise navigator rail (`ExerciseNavigatorRail`) displays all exercises as scrollable chips with completion status. Tapping a chip jumps to that exercise (resuming at next uncompleted set). A reorder button opens `ExerciseReorderSheet` (ModalBottomSheet) where exercises can be long-press-dragged to reorder. Reordering remaps the `completedSets` index-keyed map and adjusts `currentExerciseIndex` to follow the viewed exercise.

### Active Development (Next Steps)

1. **UX Improvements**: "Swipe to Delete" for workout cards.
2. **Search & Filter**: Global search for workouts and advanced filtering.
3. **Visualization**: Charts and graphs for progress tracking (Volume, 1RM stats).
4. **In-Workout Tools**: Further superset support improvements.

## Handover Readiness

- **Entry Point**: `FitnessSDK.initialize(context)` in `SampleApplication.kt`.
- **Key Files**:
  - `README.md`: Comprehensive setup and architecture guide.
  - `ROADMAP.md`: Feature tracking and status.
  - `com.fitness.sdk.FitnessSDK`: Main facade for exploring SDK functionality.
- **Testing**: Unit tests located in `fitness-sdk/src/test/`.
