package com.fitness.sdk.domain.usecase

import com.fitness.sdk.domain.model.WorkoutTemplate
import com.fitness.sdk.domain.repository.TemplateRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for getting all workout templates.
 */
class GetTemplatesUseCase(
    private val repository: TemplateRepository
) {
    /**
     * Observe all templates as a Flow.
     * Emits updates whenever templates change.
     *
     * @return Flow of template list
     */
    operator fun invoke(): Flow<List<WorkoutTemplate>> {
        return repository.observeAllTemplates()
    }

    /**
     * Get all templates as a one-time result.
     *
     * @return Result containing list of all templates
     */
    suspend fun getAll(): Result<List<WorkoutTemplate>> {
        return repository.getAllTemplates()
    }
}
