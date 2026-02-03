package com.fitness.sdk.domain.usecase

import com.fitness.sdk.domain.repository.TemplateRepository

/**
 * Use case for deleting a workout template.
 */
class DeleteTemplateUseCase(
    private val repository: TemplateRepository
) {
    /**
     * Delete a template by its ID.
     *
     * @param id The template ID to delete
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(id: Long): Result<Unit> {
        if (id <= 0) {
            return Result.failure(IllegalArgumentException("Invalid template ID: $id"))
        }

        return repository.deleteTemplate(id)
    }
}
