package com.fitness.sdk.domain.usecase

import com.fitness.sdk.domain.model.WorkoutTemplate
import com.fitness.sdk.domain.repository.TemplateRepository

/**
 * Use case for getting a workout template by its ID.
 */
class GetTemplateByIdUseCase(
    private val repository: TemplateRepository
) {
    /**
     * Get a template by its ID.
     *
     * @param id The template ID
     * @return Result containing the template, or error if not found
     */
    suspend operator fun invoke(id: Long): Result<WorkoutTemplate> {
        if (id <= 0) {
            return Result.failure(IllegalArgumentException("Invalid template ID: $id"))
        }

        return repository.getTemplateById(id).mapCatching { template ->
            template ?: throw NoSuchElementException("Template not found: $id")
        }
    }
}
