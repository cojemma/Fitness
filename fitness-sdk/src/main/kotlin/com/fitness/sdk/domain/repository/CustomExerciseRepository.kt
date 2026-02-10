package com.fitness.sdk.domain.repository

import com.fitness.sdk.domain.model.ExerciseDefinition
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for custom exercise operations.
 */
interface CustomExerciseRepository {

    /**
     * Save a new custom exercise.
     *
     * @param exercise The exercise definition to save (must have isCustom = true)
     */
    suspend fun saveCustomExercise(exercise: ExerciseDefinition)

    /**
     * Delete a custom exercise by its ID.
     *
     * @param id The unique identifier of the custom exercise
     */
    suspend fun deleteCustomExercise(id: String)

    /**
     * Get all custom exercises.
     *
     * @return List of all custom exercise definitions
     */
    suspend fun getAllCustomExercises(): List<ExerciseDefinition>

    /**
     * Get a custom exercise by name (for uniqueness checks).
     *
     * @param name The exercise name
     * @return The exercise definition if found, null otherwise
     */
    suspend fun getCustomExerciseByName(name: String): ExerciseDefinition?

    /**
     * Observe all custom exercises as a Flow for reactive updates.
     *
     * @return Flow emitting list of custom exercises whenever data changes
     */
    fun observeAllCustomExercises(): Flow<List<ExerciseDefinition>>
}
