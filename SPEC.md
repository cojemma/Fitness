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
- **API Layer**: Public interfaces (`WorkoutManager`, `TemplateManager`) exposed to the app.
- **Domain Layer**: 
    - **Models**: `Workout`, `Exercise`, `WorkoutTemplate`.
    - **Use Cases**: Encapsulated business rules (e.g., `SaveWorkoutUseCase`).
    - **Repositories**: Interfaces for data access.
- **Data Layer**:
    - **Local**: Room Database definition, DAOs, Entities, Mappers.
    - **Repository Impl**: Concrete implementation of repository interfaces.

### 2. `sample-app` (Android Application)
A reference implementation using the SDK.
- **UI Layer**: Jetpack Compose screens (`HomeScreen`, `WorkoutDetailsScreen`, `TemplateListScreen`).
- **ViewModels**: State management for screens, interacting with SDK Managers.
- **Navigation**: `FitnessNavGraph` managing screen transitions.

## The Feature Loop
### Current State
**Version 1.3.0**
- **Core CRUD**: Workouts and Exercises can be created, read, updated, and deleted.
- **Exercise Library**: Pre-loaded library of 55+ exercises with categorization.
- **Template System**: 
    - Create/Edit templates.
    - Start active workout from template.
    - `ActiveWorkoutScreen` with timer and set logging.
- **Per-Set Recording**: Individual set records (weight, reps) stored in database via `ExerciseSet` entity.
- **Data Persistence**: Robust local storage using Room.

### Active Development (Next Steps)
1.  **UX Improvements**: "Swipe to Delete" for workout cards.
2.  **Search & Filter**: Global search for workouts and advanced filtering.
3.  **Visualization**: Charts and graphs for progress tracking (Volume, 1RM stats).
4.  **In-Workout Tools**: Dedicated rest timer, Superset support (referenced in PRD).
5.  **Data Portability**: Import/Export (JSON/CSV).

## Handover Readiness
- **Entry Point**: `FitnessSDK.initialize(context)` in `SampleApplication.kt`.
- **Key Files**: 
    - `README.md`: Comprehensive setup and architecture guide.
    - `ROADMAP.md`: Feature tracking and status.
    - `com.fitness.sdk.FitnessSDK`: Main facade for exploring SDK functionality.
- **Testing**: Unit tests located in `fitness-sdk/src/test/`.
