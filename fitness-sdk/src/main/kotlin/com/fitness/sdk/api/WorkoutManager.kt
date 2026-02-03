package com.fitness.sdk.api

import com.fitness.sdk.domain.model.Workout
import com.fitness.sdk.domain.model.WorkoutType
import kotlinx.coroutines.flow.Flow

/**
 * Main interface for workout management operations.
 * This is the primary API that consumers of the SDK will interact with.
 */
interface WorkoutManager {

    /**
     * Create a new workout.
     *
     * @param workout The workout to create
     * @return Result containing the new workout ID on success
     */
    suspend fun createWorkout(workout: Workout): Result<Long>

    /**
     * Get all workouts.
     *
     * @return Result containing list of all workouts
     */
    suspend fun getAllWorkouts(): Result<List<Workout>>

    /**
     * Get workouts filtered by type.
     *
     * @param type The workout type to filter by
     * @return Result containing filtered list of workouts
     */
    suspend fun getWorkoutsByType(type: WorkoutType): Result<List<Workout>>

    /**
     * Get workouts within a date range.
     *
     * @param startTime Start of the date range (milliseconds since epoch)
     * @param endTime End of the date range (milliseconds since epoch)
     * @return Result containing workouts in the date range
     */
    suspend fun getWorkoutsByDateRange(startTime: Long, endTime: Long): Result<List<Workout>>

    /**
     * Get a specific workout by ID.
     *
     * @param id The workout ID
     * @return Result containing the workout if found
     */
    suspend fun getWorkout(id: Long): Result<Workout?>

    /**
     * Update an existing workout.
     *
     * @param workout The workout with updated values
     * @return Result indicating success or failure
     */
    suspend fun updateWorkout(workout: Workout): Result<Unit>

    /**
     * Delete a workout.
     *
     * @param id The ID of the workout to delete
     * @return Result indicating success or failure
     */
    suspend fun deleteWorkout(id: Long): Result<Unit>

    /**
     * Observe all workouts as a Flow.
     * Emits new list whenever the data changes.
     *
     * @return Flow of workout lists
     */
    fun observeWorkouts(): Flow<List<Workout>>

    /**
     * Observe a specific workout by ID.
     *
     * @param id The workout ID
     * @return Flow emitting the workout whenever it changes
     */
    fun observeWorkout(id: Long): Flow<Workout?>
}
