package com.fitness.sdk.domain.usecase

import com.fitness.sdk.domain.repository.WorkoutRepository

/**
 * Use case for deleting a workout.
 */
class DeleteWorkoutUseCase(
    private val repository: WorkoutRepository
) {
    /**
     * Delete a workout by its ID.
     *
     * @param id The unique identifier of the workout to delete
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(id: Long): Result<Unit> {
        return runCatching {
            require(id > 0) { "Workout ID must be positive" }
            repository.deleteWorkout(id)
        }
    }
}
