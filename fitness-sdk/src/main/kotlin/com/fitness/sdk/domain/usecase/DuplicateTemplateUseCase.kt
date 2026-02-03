package com.fitness.sdk.domain.usecase

import com.fitness.sdk.domain.repository.TemplateRepository

/**
 * Use case for duplicating a workout template.
 */
class DuplicateTemplateUseCase(
    private val repository: TemplateRepository
) {
    /**
     * Duplicate an existing template with an optional new name.
     *
     * @param id The ID of the template to duplicate
     * @param newName Optional new name for the duplicated template
     * @return Result containing the new template ID on success
     */
    suspend operator fun invoke(id: Long, newName: String? = null): Result<Long> {
        if (id <= 0) {
            return Result.failure(IllegalArgumentException("Invalid template ID: $id"))
        }

        // Validate new name if provided
        if (newName != null && newName.isBlank()) {
            return Result.failure(IllegalArgumentException("Template name cannot be empty"))
        }

        if (newName != null && newName.length > 100) {
            return Result.failure(IllegalArgumentException("Template name cannot exceed 100 characters"))
        }

        return repository.duplicateTemplate(id, newName)
    }
}
