package com.fitness.sdk.domain.usecase

import com.fitness.sdk.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for fetching session counts for all exercises.
 */
class GetExerciseSessionCountsUseCase(
    private val workoutRepository: WorkoutRepository
) {

    suspend operator fun invoke(): Map<String, Int> {
        return workoutRepository.getExerciseSessionCounts()
    }

    fun observe(): Flow<Map<String, Int>> {
        return workoutRepository.observeExerciseSessionCounts()
    }
}
