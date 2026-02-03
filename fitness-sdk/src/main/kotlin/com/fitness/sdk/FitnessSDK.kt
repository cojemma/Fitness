package com.fitness.sdk

import android.content.Context
import android.util.Log
import com.fitness.sdk.api.ExerciseLibraryManager
import com.fitness.sdk.api.ExerciseLibraryManagerImpl
import com.fitness.sdk.api.TemplateManager
import com.fitness.sdk.api.TemplateManagerImpl
import com.fitness.sdk.api.WorkoutManager
import com.fitness.sdk.api.WorkoutManagerImpl
import com.fitness.sdk.data.library.DefaultExerciseLibrary
import com.fitness.sdk.data.library.ExerciseLibraryProvider
import com.fitness.sdk.data.local.FitnessDatabase
import com.fitness.sdk.data.repository.TemplateRepositoryImpl
import com.fitness.sdk.data.repository.WorkoutRepositoryImpl
import com.fitness.sdk.domain.usecase.DeleteTemplateUseCase
import com.fitness.sdk.domain.usecase.DeleteWorkoutUseCase
import com.fitness.sdk.domain.usecase.DuplicateTemplateUseCase
import com.fitness.sdk.domain.usecase.GetExerciseLibraryUseCase
import com.fitness.sdk.domain.usecase.GetLastSessionDataUseCase
import com.fitness.sdk.domain.usecase.GetTemplateByIdUseCase
import com.fitness.sdk.domain.usecase.GetTemplatesUseCase
import com.fitness.sdk.domain.usecase.GetWorkoutByIdUseCase
import com.fitness.sdk.domain.usecase.GetWorkoutsUseCase
import com.fitness.sdk.domain.usecase.SaveTemplateUseCase
import com.fitness.sdk.domain.usecase.SaveWorkoutAsTemplateUseCase
import com.fitness.sdk.domain.usecase.SaveWorkoutUseCase
import com.fitness.sdk.domain.usecase.SearchExercisesUseCase
import com.fitness.sdk.domain.usecase.StartWorkoutFromTemplateUseCase
import com.fitness.sdk.domain.usecase.UpdateWorkoutUseCase

/**
 * Main entry point for the Fitness SDK.
 *
 * Usage:
 * ```kotlin
 * // Initialize in Application.onCreate()
 * FitnessSDK.initialize(context)
 *
 * // Or with custom configuration
 * FitnessSDK.initialize(context) {
 *     databaseName("my_fitness_db")
 *     enableLogging(true)
 * }
 *
 * // Get WorkoutManager to perform operations
 * val workoutManager = FitnessSDK.getWorkoutManager()
 *
 * // Get ExerciseLibraryManager to browse exercises
 * val exerciseLibrary = FitnessSDK.getExerciseLibraryManager()
 * ```
 */
object FitnessSDK {

    private const val TAG = "FitnessSDK"

    @Volatile
    private var isInitialized = false

    private lateinit var config: FitnessSDKConfig
    private lateinit var database: FitnessDatabase
    private lateinit var workoutManager: WorkoutManager
    private lateinit var exerciseLibraryManager: ExerciseLibraryManager
    private lateinit var templateManager: TemplateManager

    /**
     * Initialize the SDK with default configuration.
     *
     * @param context Application context
     */
    fun initialize(context: Context) {
        initialize(context, FitnessSDKConfig.default())
    }

    /**
     * Initialize the SDK with custom configuration using DSL.
     *
     * @param context Application context
     * @param configBuilder Lambda to configure the SDK
     */
    fun initialize(context: Context, configBuilder: FitnessSDKConfig.Builder.() -> Unit) {
        val builder = FitnessSDKConfig.Builder()
        builder.configBuilder()
        initialize(context, builder.build())
    }

    /**
     * Initialize the SDK with a configuration object.
     *
     * @param context Application context
     * @param configuration SDK configuration
     */
    fun initialize(context: Context, configuration: FitnessSDKConfig) {
        if (isInitialized) {
            log("FitnessSDK is already initialized")
            return
        }

        synchronized(this) {
            if (isInitialized) return

            config = configuration
            database = FitnessDatabase.getInstance(
                context.applicationContext,
                config.databaseName
            )

            // Create repository
            val repository = WorkoutRepositoryImpl(
                database.workoutDao(),
                database.exerciseDao()
            )

            // Create workout use cases
            val saveWorkoutUseCase = SaveWorkoutUseCase(repository)
            val getWorkoutsUseCase = GetWorkoutsUseCase(repository)
            val getWorkoutByIdUseCase = GetWorkoutByIdUseCase(repository)
            val updateWorkoutUseCase = UpdateWorkoutUseCase(repository)
            val deleteWorkoutUseCase = DeleteWorkoutUseCase(repository)

            // Create WorkoutManager
            workoutManager = WorkoutManagerImpl(
                saveWorkoutUseCase,
                getWorkoutsUseCase,
                getWorkoutByIdUseCase,
                updateWorkoutUseCase,
                deleteWorkoutUseCase
            )

            // Create exercise library
            val exerciseLibraryProvider: ExerciseLibraryProvider = DefaultExerciseLibrary()
            val getExerciseLibraryUseCase = GetExerciseLibraryUseCase(exerciseLibraryProvider)
            val searchExercisesUseCase = SearchExercisesUseCase(exerciseLibraryProvider)

            exerciseLibraryManager = ExerciseLibraryManagerImpl(
                getExerciseLibraryUseCase,
                searchExercisesUseCase
            )

            // Create template repository and use cases
            val templateRepository = TemplateRepositoryImpl(database.templateDao())
            val saveTemplateUseCase = SaveTemplateUseCase(templateRepository)
            val getTemplatesUseCase = GetTemplatesUseCase(templateRepository)
            val getTemplateByIdUseCase = GetTemplateByIdUseCase(templateRepository)
            val deleteTemplateUseCase = DeleteTemplateUseCase(templateRepository)
            val duplicateTemplateUseCase = DuplicateTemplateUseCase(templateRepository)

            // Create execution automation use cases
            val getLastSessionDataUseCase = GetLastSessionDataUseCase(database.workoutDao())
            val startWorkoutFromTemplateUseCase = StartWorkoutFromTemplateUseCase(
                templateRepository,
                getLastSessionDataUseCase
            )
            val saveWorkoutAsTemplateUseCase = SaveWorkoutAsTemplateUseCase(
                repository,
                templateRepository
            )

            templateManager = TemplateManagerImpl(
                saveTemplateUseCase,
                getTemplatesUseCase,
                getTemplateByIdUseCase,
                deleteTemplateUseCase,
                duplicateTemplateUseCase,
                startWorkoutFromTemplateUseCase,
                getLastSessionDataUseCase,
                saveWorkoutAsTemplateUseCase
            )

            isInitialized = true
            log("FitnessSDK initialized successfully with database: ${config.databaseName}")
        }
    }

    /**
     * Get the WorkoutManager instance.
     *
     * @return WorkoutManager for performing workout operations
     * @throws IllegalStateException if SDK is not initialized
     */
    fun getWorkoutManager(): WorkoutManager {
        checkInitialized()
        return workoutManager
    }

    /**
     * Get the ExerciseLibraryManager instance.
     * The exercise library provides predefined exercises that can be used
     * when creating workouts.
     *
     * @return ExerciseLibraryManager for browsing and searching exercises
     * @throws IllegalStateException if SDK is not initialized
     */
    fun getExerciseLibraryManager(): ExerciseLibraryManager {
        checkInitialized()
        return exerciseLibraryManager
    }

    /**
     * Get the TemplateManager instance.
     * Templates allow users to save and reuse workout routines.
     *
     * @return TemplateManager for managing workout templates
     * @throws IllegalStateException if SDK is not initialized
     */
    fun getTemplateManager(): TemplateManager {
        checkInitialized()
        return templateManager
    }

    /**
     * Check if the SDK is initialized.
     *
     * @return true if initialized, false otherwise
     */
    fun isInitialized(): Boolean = isInitialized

    /**
     * Get the current SDK configuration.
     *
     * @return Current configuration
     * @throws IllegalStateException if SDK is not initialized
     */
    fun getConfig(): FitnessSDKConfig {
        checkInitialized()
        return config
    }

    /**
     * Shutdown the SDK and release resources.
     * Call this when the SDK is no longer needed.
     */
    fun shutdown() {
        if (!isInitialized) return

        synchronized(this) {
            FitnessDatabase.closeDatabase()
            isInitialized = false
            log("FitnessSDK shutdown complete")
        }
    }

    private fun checkInitialized() {
        if (!isInitialized) {
            throw IllegalStateException(
                "FitnessSDK is not initialized. Call FitnessSDK.initialize(context) first."
            )
        }
    }

    private fun log(message: String) {
        if (::config.isInitialized && config.enableLogging) {
            Log.d(TAG, message)
        }
    }
}
