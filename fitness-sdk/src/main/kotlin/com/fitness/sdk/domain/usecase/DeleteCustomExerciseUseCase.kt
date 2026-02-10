package com.fitness.sdk.domain.usecase

import com.fitness.sdk.domain.repository.CustomExerciseRepository

/**
 * Use case for deleting a custom exercise by its ID.
 */
class DeleteCustomExerciseUseCase(
    private val customExerciseRepository: CustomExerciseRepository
) {

    suspend operator fun invoke(id: String): Result<Unit> {
        return try {
            customExerciseRepository.deleteCustomExercise(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
