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
FitnessSDK.getWorkoutManager()          // Workout CRUD
FitnessSDK.getExerciseLibraryManager()  // 55+ predefined exercises
FitnessSDK.getTemplateManager()         // Template management
```

## Git Conventions

- **Branch:** `master`
- **Commit style:** Conventional commits — `feat:`, `refactor:`, `fix:` prefixes
- **No CI/CD configured**
