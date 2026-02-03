package com.fitness.sdk.domain.usecase

import com.fitness.sdk.domain.model.Workout
import com.fitness.sdk.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for retrieving a specific workout by ID.
 */
class GetWorkoutByIdUseCase(
    private val repository: WorkoutRepository
) {
    /**
     * Get a workout by its ID.
     *
     * @param id The unique identifier of the workout
     * @return Result containing the workout if found, null otherwise
     */
    suspend operator fun invoke(id: Long): Result<Workout?> {
        return runCatching {
            require(id > 0) { "Workout ID must be positive" }
            repository.getWorkoutById(id)
        }
    }

    /**
     * Observe a specific workout for reactive updates.
     *
     * @param id The unique identifier of the workout
     * @return Flow emitting the workout whenever it changes
     */
    fun observe(id: Long): Flow<Workout?> {
        return repository.observeWorkoutById(id)
    }
}
