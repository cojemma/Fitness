package com.fitness.sdk.domain.repository

import com.fitness.sdk.domain.model.Workout
import com.fitness.sdk.domain.model.Exercise
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for workout data operations.
 * This interface follows the Repository pattern and defines the contract
 * for data access operations related to workouts.
 */
interface WorkoutRepository {

    /**
     * Save a new workout to the data source.
     *
     * @param workout The workout to save
     * @return The ID of the newly created workout
     */
    suspend fun saveWorkout(workout: Workout): Long

    /**
     * Retrieve all workouts from the data source.
     *
     * @return List of all workouts, ordered by start time descending
     */
    suspend fun getWorkouts(): List<Workout>

    /**
     * Retrieve a specific workout by its ID.
     *
     * @param id The unique identifier of the workout
     * @return The workout if found, null otherwise
     */
    suspend fun getWorkoutById(id: Long): Workout?

    /**
     * Retrieve workouts within a specific date range.
     *
     * @param startTime Start of the date range (inclusive, milliseconds since epoch)
     * @param endTime End of the date range (inclusive, milliseconds since epoch)
     * @return List of workouts within the specified range
     */
    suspend fun getWorkoutsByDateRange(startTime: Long, endTime: Long): List<Workout>

    /**
     * Update an existing workout.
     *
     * @param workout The workout with updated values (must have valid ID)
     */
    suspend fun updateWorkout(workout: Workout)

    /**
     * Add an exercise to an existing workout.
     *
     * @param workoutId The ID of the workout to add the exercise to
     * @param exercise The exercise to add
     */
    suspend fun addExerciseToWorkout(workoutId: Long, exercise: Exercise)

    /**
     * Delete a workout by its ID.
     *
     * @param id The unique identifier of the workout to delete
     */
    suspend fun deleteWorkout(id: Long)

    /**
     * Observe all workouts as a Flow for reactive updates.
     *
     * @return Flow emitting list of workouts whenever data changes
     */
    fun observeWorkouts(): Flow<List<Workout>>

    /**
     * Observe a specific workout by ID for reactive updates.
     *
     * @param id The unique identifier of the workout
     * @return Flow emitting the workout whenever it changes
     */
    fun observeWorkoutById(id: Long): Flow<Workout?>
}
