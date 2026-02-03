package com.fitness.sdk.domain.repository

import com.fitness.sdk.domain.model.WorkoutTemplate
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for workout template operations.
 */
interface TemplateRepository {

    /**
     * Save a new template or update an existing one.
     *
     * @param template The template to save
     * @return Result containing the template ID on success
     */
    suspend fun saveTemplate(template: WorkoutTemplate): Result<Long>

    /**
     * Get a template by its ID.
     *
     * @param id The template ID
     * @return Result containing the template, or null if not found
     */
    suspend fun getTemplateById(id: Long): Result<WorkoutTemplate?>

    /**
     * Observe all templates as a Flow.
     * Updates are emitted whenever templates change.
     *
     * @return Flow of template list
     */
    fun observeAllTemplates(): Flow<List<WorkoutTemplate>>

    /**
     * Get all templates.
     *
     * @return Result containing list of all templates
     */
    suspend fun getAllTemplates(): Result<List<WorkoutTemplate>>

    /**
     * Delete a template by its ID.
     *
     * @param id The template ID to delete
     * @return Result indicating success or failure
     */
    suspend fun deleteTemplate(id: Long): Result<Unit>

    /**
     * Duplicate an existing template with a new name.
     *
     * @param id The ID of the template to duplicate
     * @param newName Optional new name for the duplicated template
     * @return Result containing the new template ID on success
     */
    suspend fun duplicateTemplate(id: Long, newName: String? = null): Result<Long>

    /**
     * Update the order of exercises within a template.
     *
     * @param templateId The template ID
     * @param exerciseIds Ordered list of exercise IDs
     * @return Result indicating success or failure
     */
    suspend fun updateExerciseOrder(templateId: Long, exerciseIds: List<Long>): Result<Unit>
}
