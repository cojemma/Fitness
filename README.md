# Fitness SDK & Sample App

A complete Android fitness tracking SDK with a sample application built using **Kotlin**, **Clean Architecture**, **Room Database**, and **Jetpack Compose**.

---

## 📁 Project Structure

```
Fitness/
├── fitness-sdk/          # SDK Module (Android Library)
├── sample-app/           # Demo Application
├── gradle/               # Gradle wrapper
└── build.gradle.kts      # Root build configuration
```

---

## 🏗️ Architecture Overview

The project follows **Clean Architecture** with three distinct layers:

```
┌─────────────────────────────────────────────────────────────┐
│                      sample-app                             │
│                  (Jetpack Compose UI)                       │
├─────────────────────────────────────────────────────────────┤
│                      API Layer                              │
│              FitnessSDK, WorkoutManager                     │
├─────────────────────────────────────────────────────────────┤
│                    Domain Layer                             │
│          Models, Use Cases, Repository Interface            │
├─────────────────────────────────────────────────────────────┤
│                     Data Layer                              │
│         Room Database, DAOs, Entities, Mappers              │
└─────────────────────────────────────────────────────────────┘
```

---

## 📦 Fitness SDK (`fitness-sdk/`)

### Directory Structure

```
src/main/kotlin/com/fitness/sdk/
├── FitnessSDK.kt              # Main entry point (singleton)
├── FitnessSDKConfig.kt        # Configuration builder
├── api/
│   ├── WorkoutManager.kt      # Public interface
│   └── WorkoutManagerImpl.kt  # Implementation
├── domain/
│   ├── model/
│   │   ├── Workout.kt         # Workout data class
│   │   ├── Exercise.kt        # Exercise data class
│   │   └── WorkoutType.kt     # Enum: STRENGTH, CARDIO, etc.
│   ├── repository/
│   │   └── WorkoutRepository.kt  # Repository interface
│   └── usecase/
│       ├── SaveWorkoutUseCase.kt
│       ├── GetWorkoutsUseCase.kt
│       ├── GetWorkoutByIdUseCase.kt
│       ├── UpdateWorkoutUseCase.kt
│       └── DeleteWorkoutUseCase.kt
└── data/
    ├── local/
    │   ├── FitnessDatabase.kt     # Room database
    │   ├── dao/
    │   │   ├── WorkoutDao.kt
    │   │   └── ExerciseDao.kt
    │   └── entity/
    │       ├── WorkoutEntity.kt
    │       ├── ExerciseEntity.kt
    │       └── WorkoutWithExercises.kt
    ├── mapper/
    │   ├── WorkoutMapper.kt
    │   └── ExerciseMapper.kt
    └── repository/
        └── WorkoutRepositoryImpl.kt
```

### Key Components

| Component | Description |
|-----------|-------------|
| `FitnessSDK` | Singleton entry point. Initialize with `FitnessSDK.initialize(context)` |
| `WorkoutManager` | Public API for CRUD operations on workouts |
| `Use Cases` | Business logic with validation (e.g., workout name cannot be blank) |
| `Room Database` | Local persistence with `WorkoutEntity` and `ExerciseEntity` |
| `Mappers` | Convert between domain models and database entities |

### SDK Usage

```kotlin
// Initialize (in Application.onCreate)
FitnessSDK.initialize(context) {
    databaseName("my_fitness_db")
    enableLogging(true)
}

// Get manager
val manager = FitnessSDK.getWorkoutManager()

// CRUD operations
manager.createWorkout(workout)      // Returns Result<Long>
manager.getAllWorkouts()            // Returns Result<List<Workout>>
manager.getWorkout(id)              // Returns Result<Workout?>
manager.updateWorkout(workout)      // Returns Result<Unit>
manager.deleteWorkout(id)           // Returns Result<Unit>

// Reactive observation
manager.observeWorkouts()           // Returns Flow<List<Workout>>
```

---

## 📱 Sample App (`sample-app/`)

### Directory Structure

```
src/main/kotlin/com/fitness/sample/
├── MainActivity.kt            # Compose entry point
├── SampleApplication.kt       # SDK initialization
├── navigation/
│   └── FitnessNavGraph.kt     # Navigation routes
└── ui/
    ├── theme/
    │   ├── Color.kt           # Color definitions
    │   ├── Theme.kt           # Material 3 theme
    │   └── Type.kt            # Typography
    ├── components/
    │   ├── WorkoutCard.kt     # Workout list item
    │   ├── ExerciseItem.kt    # Exercise list item
    │   ├── StatsSummary.kt    # Weekly stats card
    │   └── EmptyState.kt      # Empty list placeholder
    ├── home/
    │   ├── HomeScreen.kt      # Main workout list
    │   └── HomeViewModel.kt   # Home screen state
    ├── workout/
    │   ├── AddWorkoutScreen.kt       # Create/edit workout form
    │   ├── WorkoutDetailsScreen.kt   # Workout detail view
    │   └── WorkoutViewModel.kt       # Workout form state
    └── exercise/
        └── AddExerciseDialog.kt      # Add exercise modal
```

### Screens

| Screen | Route | Description |
|--------|-------|-------------|
| Home | `home` | Workout list with weekly stats |
| Add Workout | `add_workout` | Create new workout form |
| Workout Details | `workout/{id}` | View workout and exercises |
| Edit Workout | `edit_workout/{id}` | Edit existing workout |

### Navigation

```kotlin
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AddWorkout : Screen("add_workout")
    object WorkoutDetails : Screen("workout/{workoutId}")
    object EditWorkout : Screen("edit_workout/{workoutId}")
}
```

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog or newer
- JDK 17+
- Android SDK 34

### Build & Run

```bash
# Build SDK
./gradlew :fitness-sdk:build

# Build sample app
./gradlew :sample-app:assembleDebug

# Run unit tests
./gradlew :fitness-sdk:test
```

### In Android Studio

1. **File → Open** → Select project root
2. Wait for Gradle sync
3. Select `sample-app` configuration
4. Run on Android 10+ device/emulator

---

## 🛠️ Adding New Features

### Adding a New Use Case

1. Create use case in `fitness-sdk/domain/usecase/`
2. Inject repository via constructor
3. Add method to `WorkoutRepository` if needed
4. Implement in `WorkoutRepositoryImpl`
5. Expose via `WorkoutManager` interface

### Adding a New Screen

1. Create screen composable in `sample-app/ui/<feature>/`
2. Create ViewModel if needed
3. Add route to `FitnessNavGraph.kt`
4. Navigate using `navController.navigate(Screen.NewScreen.route)`

### Adding a New Entity Field

1. Update `domain/model/` data class
2. Update `data/local/entity/` entity class
3. Update mapper in `data/mapper/`
4. Increment database version in `FitnessDatabase.kt`
5. Add migration if preserving data

---

## 📋 Tech Stack

| Technology | Purpose |
|------------|---------|
| Kotlin 1.9.21 | Programming language |
| Jetpack Compose | UI framework |
| Material 3 | Design system |
| Room 2.6.1 | Local database |
| Navigation Compose | Screen navigation |
| Coroutines + Flow | Async operations |
| KSP | Annotation processing |

---

## 📄 License

MIT License
