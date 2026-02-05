package com.fitness.sdk.domain.usecase

import com.fitness.sdk.domain.model.Exercise
import com.fitness.sdk.domain.repository.WorkoutRepository

/**
 * Use case to add an exercise to an existing workout.
 */
class AddExerciseToWorkoutUseCase(
    private val workoutRepository: WorkoutRepository
) {
    /**
     * Add an exercise to an existing workout.
     *
     * @param workoutId The ID of the workout
     * @param exercise The exercise to add
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(workoutId: Long, exercise: Exercise): Result<Unit> {
        return try {
            if (workoutRepository.getWorkoutById(workoutId) == null) {
                return Result.failure(NoSuchElementException("Workout not found: $workoutId"))
            }
            workoutRepository.addExerciseToWorkout(workoutId, exercise)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
