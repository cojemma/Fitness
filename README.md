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
│      FitnessSDK, WorkoutManager, ExerciseLibraryManager     │
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
│   │   ├── Workout.kt           # Workout data class
│   │   ├── Exercise.kt          # Exercise data class
│   │   ├── ExerciseSet.kt       # Individual set record (weight, reps)
│   │   ├── WorkoutType.kt       # Enum: STRENGTH, CARDIO, etc.
│   │   ├── ExerciseDefinition.kt  # Library exercise template
│   │   ├── MuscleGroup.kt       # Enum: CHEST, BACK, LEGS, etc.
│   │   └── ExerciseCategory.kt  # Enum: STRENGTH, CARDIO, etc.
│   ├── repository/
│   │   └── WorkoutRepository.kt  # Repository interface
│   └── usecase/
│       ├── SaveWorkoutUseCase.kt
│       ├── GetWorkoutsUseCase.kt
│       ├── GetWorkoutByIdUseCase.kt
│       ├── UpdateWorkoutUseCase.kt
│       ├── DeleteWorkoutUseCase.kt
│       ├── GetExerciseLibraryUseCase.kt
│       ├── GetExerciseSessionCountsUseCase.kt
│       └── SearchExercisesUseCase.kt
├── data/
│   ├── local/
│   │   ├── FitnessDatabase.kt     # Room database
│   │   ├── dao/
│   │   │   ├── WorkoutDao.kt
│   │   │   └── ExerciseDao.kt
│   │   └── entity/
│   │       ├── WorkoutEntity.kt
│   │       ├── ExerciseEntity.kt
│   │       ├── ExerciseSetEntity.kt  # Per-set performance records
│   │       ├── ExerciseSessionCount.kt  # Query result for session counts
│   │       └── WorkoutWithExercises.kt
│   ├── library/
│   │   ├── ExerciseLibraryProvider.kt  # Interface
│   │   └── DefaultExerciseLibrary.kt   # 55+ predefined exercises
│   ├── mapper/
│   │   ├── WorkoutMapper.kt
│   │   ├── ExerciseMapper.kt
│   │   └── ExerciseSetMapper.kt
│   └── repository/
│       └── WorkoutRepositoryImpl.kt
└── api/
    ├── WorkoutManager.kt             # Workout operations
    ├── WorkoutManagerImpl.kt
    ├── ExerciseLibraryManager.kt     # Exercise library API
    ├── ExerciseLibraryManagerImpl.kt
    ├── TemplateManager.kt            # Template operations
    └── TemplateManagerImpl.kt
```

### Key Components

| Component | Description |
|-----------|-------------|
| `FitnessSDK` | Singleton entry point. Initialize with `FitnessSDK.initialize(context)` |
| `WorkoutManager` | Public API for CRUD operations on workouts |
| `ExerciseLibraryManager` | Public API for browsing/searching predefined exercises |
| `TemplateManager` | Public API for creating, managing, and starting workouts from templates |
| `ExerciseDefinition` | Template for library exercises with defaults |
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

// Get managers
val workoutManager = FitnessSDK.getWorkoutManager()
val exerciseLibrary = FitnessSDK.getExerciseLibraryManager()
val templateManager = FitnessSDK.getTemplateManager()

// Workout CRUD operations
workoutManager.createWorkout(workout)      // Returns Result<Long>
workoutManager.getAllWorkouts()            // Returns Result<List<Workout>>
workoutManager.getWorkout(id)              // Returns Result<Workout?>
workoutManager.updateWorkout(workout)      // Returns Result<Unit>
workoutManager.deleteWorkout(id)           // Returns Result<Unit>
workoutManager.addExerciseToWorkout(id, exercise) // Returns Result<Unit>
workoutManager.getExerciseSessionCounts()         // Returns Result<Map<String, Int>>
workoutManager.observeExerciseSessionCounts()     // Returns Flow<Map<String, Int>>

// Template operations
templateManager.saveTemplate(template)     // Returns Result<Long>
templateManager.getTemplates()             // Returns Flow<List<WorkoutTemplate>>
templateManager.startWorkout(templateId)   // Returns Result<Workout> (active session)
templateManager.saveWorkoutAsTemplate(workoutId, name) // Create template from workout
templateManager.updateTemplateFromWorkout(templateId, workoutId) // Update existing template

// Reactive observation
workoutManager.observeWorkouts()           // Returns Flow<List<Workout>>

// Exercise library operations
exerciseLibrary.getAllExercises()                      // List<ExerciseDefinition>
exerciseLibrary.getExercisesByMuscleGroup(CHEST)       // Filter by muscle
exerciseLibrary.getExercisesByCategory(STRENGTH)       // Filter by category
exerciseLibrary.searchExercises("bench")               // Search by name
exerciseLibrary.getExercise("bench_press")             // Get by ID

// Convert library exercise to workout exercise
val benchPress = exerciseLibrary.getExercise("bench_press")
val exercise = benchPress?.toExercise(sets = 4, reps = 8, weight = 80f)
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
    │   ├── EmptyState.kt      # Empty list placeholder
    │   ├── TemplateCard.kt          # Template list item
    │   └── TemplateExerciseCard.kt  # Template editor card
    ├── home/
    │   ├── HomeScreen.kt      # Main workout list
    │   └── HomeViewModel.kt   # Home screen state
    ├── workout/
    │   ├── AddWorkoutScreen.kt       # Create/edit workout form
    │   ├── WorkoutDetailsScreen.kt   # Workout detail view
    │   └── WorkoutViewModel.kt       # Workout form state
    ├── template/
    │   ├── TemplateListScreen.kt     # List of templates
    │   ├── AddTemplateScreen.kt      # Create/edit template
    │   ├── ActiveWorkoutScreen.kt    # Active session UI (timer, set logging, add exercise FAB)
    │   ├── TemplateViewModel.kt      # Template editor state
    │   ├── TemplateListViewModel.kt  # Template list state
    │   ├── ActiveWorkoutViewModel.kt # Active session state (Coordinator)
    │   ├── TimerManager.kt           # Workout/Rest timer logic
    │   ├── SessionStateManager.kt    # Exercise/Set session logic (merge on setWorkout for race safety)
    │   └── SetLogEntry.kt            # Logging data model
    └── exercise/
        ├── AddExerciseDialog.kt          # Add exercise modal
        ├── ExercisePickerScreen.kt       # Browse exercise library
        ├── ExerciseListScreen.kt         # Exercise list with history (sorted by done times)
        ├── ExerciseListViewModel.kt      # Exercise list state with session count sorting
        └── ExerciseLibraryViewModel.kt   # Library search/filter
```

### Screens

| Screen | Route | Description |
|--------|-------|-------------|
| Home | `home` | Workout list with weekly stats |
| Add Workout | `add_workout` | Create new workout form |
| Workout Details | `workout/{id}` | View workout and exercises |
| Edit Workout | `edit_workout/{id}` | Edit existing workout |
| Exercise Picker | `exercise_picker/{source}` | Browse and select from library |
| Templates | `templates` | List of reusable workout templates |
| Add Template | `add_template` | Create new template |
| Edit Template | `edit_template/{id}` | Edit existing template |
| Active Workout | `active_workout/{templateId}` | Active training session with timer, set logging, and add-exercise from library |

### Navigation

```kotlin
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AddWorkout : Screen("add_workout")
    object WorkoutDetails : Screen("workout/{workoutId}")
    object EditWorkout : Screen("edit_workout/{workoutId}")
    object ExercisePicker : Screen("exercise_picker/{source}")
    object Templates : Screen("templates")
    object AddTemplate : Screen("add_template")
    object EditTemplate : Screen("edit_template/{templateId}")
    object ActiveWorkout : Screen("active_workout/{templateId}")
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
