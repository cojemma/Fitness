package com.fitness.sdk.api

import com.fitness.sdk.domain.model.ExerciseCategory
import com.fitness.sdk.domain.model.ExerciseDefinition
import com.fitness.sdk.domain.model.MuscleGroup
import kotlinx.coroutines.flow.Flow

/**
 * Public API for accessing the exercise library.
 * Provides methods to browse and search predefined and custom exercises.
 */
interface ExerciseLibraryManager {
    /**
     * Get all exercises in the library (predefined + custom).
     * @return List of all exercise definitions
     */
    fun getAllExercises(): List<ExerciseDefinition>

    /**
     * Get a specific exercise by its ID.
     * @param id The unique exercise ID (e.g., "chest_bench_press")
     * @return The exercise definition or null if not found
     */
    fun getExerciseById(id: String): ExerciseDefinition?

    /**
     * Get all exercises in a specific category.
     * @param category The exercise category (STRENGTH, CARDIO, etc.)
     * @return List of exercises in that category
     */
    fun getExercisesByCategory(category: ExerciseCategory): List<ExerciseDefinition>

    /**
     * Get all exercises targeting a specific muscle group.
     * Includes exercises where the muscle is primary or secondary.
     * @param muscleGroup The target muscle group
     * @return List of exercises targeting that muscle
     */
    fun getExercisesByMuscleGroup(muscleGroup: MuscleGroup): List<ExerciseDefinition>

    /**
     * Search exercises by name.
     * @param query The search query (case-insensitive partial match)
     * @return List of matching exercises
     */
    fun searchExercises(query: String): List<ExerciseDefinition>

    /**
     * Save a user-created custom exercise to the library.
     *
     * @param exercise The exercise definition to save
     * @return Result indicating success or failure (e.g., duplicate name)
     */
    suspend fun saveCustomExercise(exercise: ExerciseDefinition): Result<Unit>

    /**
     * Delete a custom exercise from the library.
     * Only custom exercises (isCustom = true) can be deleted.
     *
     * @param id The unique ID of the custom exercise
     * @return Result indicating success or failure
     */
    suspend fun deleteCustomExercise(id: String): Result<Unit>

    /**
     * Observe all exercises (predefined + custom) as a reactive Flow.
     * Emits a new list whenever custom exercises are added or removed.
     *
     * @return Flow of exercise definition lists
     */
    fun observeAllExercises(): Flow<List<ExerciseDefinition>>
}
