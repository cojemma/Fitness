package com.fitness.sdk.data.library

import com.fitness.sdk.domain.model.ExerciseCategory
import com.fitness.sdk.domain.model.ExerciseDefinition
import com.fitness.sdk.domain.model.MuscleGroup

/**
 * Interface for providing exercise library data.
 * Implement this to provide a custom exercise library or extend the default one.
 */
interface ExerciseLibraryProvider {
    /**
     * Get all exercises in the library.
     * @return List of all exercise definitions
     */
    fun getAllExercises(): List<ExerciseDefinition>

    /**
     * Get an exercise by its unique ID.
     * @param id The exercise ID
     * @return The exercise definition or null if not found
     */
    fun getExerciseById(id: String): ExerciseDefinition?

    /**
     * Get all exercises in a specific category.
     * @param category The exercise category to filter by
     * @return List of exercises in the specified category
     */
    fun getExercisesByCategory(category: ExerciseCategory): List<ExerciseDefinition>

    /**
     * Get all exercises targeting a specific muscle group.
     * Includes exercises where the muscle is either primary or secondary.
     * @param muscleGroup The muscle group to filter by
     * @return List of exercises targeting the specified muscle group
     */
    fun getExercisesByMuscleGroup(muscleGroup: MuscleGroup): List<ExerciseDefinition>

    /**
     * Search exercises by name.
     * @param query The search query (case-insensitive partial match)
     * @return List of exercises matching the query
     */
    fun searchExercises(query: String): List<ExerciseDefinition>
}
