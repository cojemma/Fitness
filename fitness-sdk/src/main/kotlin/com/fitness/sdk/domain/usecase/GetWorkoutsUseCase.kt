package com.fitness.sdk.domain.usecase

import com.fitness.sdk.domain.model.Workout
import com.fitness.sdk.domain.model.WorkoutType
import com.fitness.sdk.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for retrieving workouts with optional filtering.
 */
class GetWorkoutsUseCase(
    private val repository: WorkoutRepository
) {
    /**
     * Get all workouts.
     *
     * @return Result containing list of all workouts
     */
    suspend operator fun invoke(): Result<List<Workout>> {
        return runCatching {
            repository.getWorkouts()
        }
    }

    /**
     * Get workouts filtered by type.
     *
     * @param type The workout type to filter by
     * @return Result containing filtered list of workouts
     */
    suspend fun byType(type: WorkoutType): Result<List<Workout>> {
        return runCatching {
            repository.getWorkouts().filter { it.type == type }
        }
    }

    /**
     * Get workouts within a date range.
     *
     * @param startTime Start of the date range (milliseconds since epoch)
     * @param endTime End of the date range (milliseconds since epoch)
     * @return Result containing workouts in the date range
     */
    suspend fun byDateRange(startTime: Long, endTime: Long): Result<List<Workout>> {
        return runCatching {
            require(startTime <= endTime) { "Start time must be before or equal to end time" }
            repository.getWorkoutsByDateRange(startTime, endTime)
        }
    }

    /**
     * Observe workouts as a Flow for reactive updates.
     *
     * @return Flow of workout lists
     */
    fun observe(): Flow<List<Workout>> {
        return repository.observeWorkouts()
    }
}
