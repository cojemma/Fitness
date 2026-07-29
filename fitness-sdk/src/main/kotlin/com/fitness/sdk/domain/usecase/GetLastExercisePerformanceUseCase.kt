package com.fitness.sdk.domain.usecase

import com.fitness.sdk.domain.model.Exercise
import com.fitness.sdk.domain.repository.WorkoutRepository

/**
 * Use case for fetching the most recently performed sets/reps/weight for an exercise by name.
 * Used to prefill a newly swapped-in exercise during an active workout.
 */
class GetLastExercisePerformanceUseCase(
    private val workoutRepository: WorkoutRepository
) {

    suspend operator fun invoke(exerciseName: String): Exercise? {
        return workoutRepository.getLastExercisePerformance(exerciseName)
    }
}
