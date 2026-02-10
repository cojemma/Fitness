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

- **UI Layer**: Jetpack Compose screens (`HomeScreen`, `WorkoutDetailsScreen`, `TemplateListScreen`, `CreateCustomExerciseScreen`).
- **ViewModels**: State management for screens, interacting with SDK Managers.
- **Navigation**: `FitnessNavGraph` managing screen transitions.

## The Feature Loop

### Current State

**Version 1.8.0**

- **Core CRUD**: Workouts and Exercises can be created, read, updated, and deleted.
- **Exercise Library**: Pre-loaded library of 55+ exercises with categorization.
- **Custom Exercises**: Users can create their own exercises with full details (name, category, primary/secondary muscles, description, instructions, time-based toggle, default sets/reps/duration). Custom exercises are persisted in Room and appear alongside predefined exercises in all library views, pickers, and filters. `CompositeExerciseLibraryProvider` transparently merges predefined + custom exercises. Reactive `observeAllExercises()` Flow keeps UI in sync.
- **Exercise List Page**: "Exercises" tab listing all exercises with expandable history. Each exercise shows total sessions, max weight, estimated 1RM (Epley formula), and session-by-session history. Tap a session to navigate to workout details. **Default sort by done times** (most-performed exercises appear first).
- **Exercise Session Counts API**: `WorkoutManager.getExerciseSessionCounts()` returns a map of exercise name to workout session count, enabling efficient sorting without loading full history. `observeExerciseSessionCounts()` provides a reactive Flow for real-time UI updates when workout data changes.
- **Template System**:
  - Create/Edit templates.
  - Start active workout from template.
  - `ActiveWorkoutScreen` with timer and set logging.
  - **Last Session Data**: Pre-loads previous workout performance for progressive overload tracking using actual per-set records.
  - **Save as Template**: Options to replace original template, save as new, or skip after workout completion.
- **Per-Set Recording**: Individual set records (weight, reps) stored in database via `ExerciseSet` entity.
- **Data Persistence**: Robust local storage using Room.
- **Architecture Refactor**:
  - **Active Workout**: Split logic into `ActiveWorkoutViewModel` (Coordination), `TimerManager` (Time tracking), and `SessionStateManager` (Exercise/Set logic) for better maintainability.
  - **SDK**: Added `addExerciseToWorkout` capability for dynamic workout modification.
- **Dynamic Exercise Addition**: Users can add exercises to an active workout session via FloatingActionButton, selecting from the exercise library. Exercises are added to the in-memory workout and immediately available for logging sets.

### Active Development (Next Steps)

1. **Rest Timer Notifications**: Audio/vibration feedback when rest timer completes.
2. **UX Improvements**: "Swipe to Delete" for workout cards.
3. **Search & Filter**: Global search for workouts and advanced filtering.
4. **Visualization**: Charts and graphs for progress tracking (Volume, 1RM stats).
5. **In-Workout Tools**: Superset support (referenced in PRD).

## Handover Readiness

- **Entry Point**: `FitnessSDK.initialize(context)` in `SampleApplication.kt`.
- **Key Files**:
  - `README.md`: Comprehensive setup and architecture guide.
  - `ROADMAP.md`: Feature tracking and status.
  - `com.fitness.sdk.FitnessSDK`: Main facade for exploring SDK functionality.
- **Testing**: Unit tests located in `fitness-sdk/src/test/`.
