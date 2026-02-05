package com.fitness.sdk.domain.usecase

import com.fitness.sdk.domain.model.ExerciseHistory
import com.fitness.sdk.domain.repository.WorkoutRepository

/**
 * Use case for fetching exercise history and computing aggregated stats.
 * Calculates total sessions, max weight, and estimated 1RM using the Epley formula.
 */
class GetExerciseHistoryUseCase(
    private val workoutRepository: WorkoutRepository
) {

    /**
     * Get history for an exercise by name.
     */
    suspend operator fun invoke(exerciseName: String): ExerciseHistory {
        return workoutRepository.getExerciseHistory(exerciseName)
    }
}
