package com.fitness.sdk.domain.usecase

import com.fitness.sdk.domain.model.WorkoutTemplate
import com.fitness.sdk.domain.repository.TemplateRepository

/**
 * Use case for saving a workout template.
 * Validates the template before saving.
 */
class SaveTemplateUseCase(
    private val repository: TemplateRepository
) {
    /**
     * Save a workout template.
     *
     * @param template The template to save
     * @return Result containing the template ID on success, or error on failure
     */
    suspend operator fun invoke(template: WorkoutTemplate): Result<Long> {
        // Validate template
        val validationError = validateTemplate(template)
        if (validationError != null) {
            return Result.failure(IllegalArgumentException(validationError))
        }

        return repository.saveTemplate(template)
    }

    private fun validateTemplate(template: WorkoutTemplate): String? {
        return when {
            template.name.isBlank() -> "Template name cannot be empty"
            template.name.length > 100 -> "Template name cannot exceed 100 characters"
            template.exercises.isEmpty() -> "Template must have at least one exercise"
            template.exercises.any { it.exerciseName.isBlank() } -> "Exercise name cannot be empty"
            template.exercises.any { it.sets.isEmpty() } -> "Each exercise must have at least one set"
            else -> null
        }
    }
}
