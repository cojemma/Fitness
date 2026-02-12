package com.fitness.sdk.api

import com.fitness.sdk.domain.model.LastSessionData
import com.fitness.sdk.domain.model.Workout
import com.fitness.sdk.domain.model.WorkoutTemplate
import kotlinx.coroutines.flow.Flow

/**
 * Public API for managing workout templates.
 * Templates allow users to save and reuse workout routines.
 */
interface TemplateManager {

    // ==================== CRUD Operations ====================

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
     * @return Result containing the template on success
     */
    suspend fun getTemplateById(id: Long): Result<WorkoutTemplate>

    /**
     * Observe all templates as a Flow.
     * Emits updates whenever templates change.
     *
     * @return Flow of template list sorted by last update time (newest first)
     */
    fun observeTemplates(): Flow<List<WorkoutTemplate>>

    /**
     * Get all templates as a one-time result.
     *
     * @return Result containing list of all templates
     */
    suspend fun getAllTemplates(): Result<List<WorkoutTemplate>>

    /**
     * Delete a template by its ID.
     * This also deletes all associated exercises and sets.
     *
     * @param id The template ID to delete
     * @return Result indicating success or failure
     */
    suspend fun deleteTemplate(id: Long): Result<Unit>

    /**
     * Duplicate an existing template.
     *
     * @param id The ID of the template to duplicate
     * @param newName Optional new name for the duplicated template
     * @return Result containing the new template ID on success
     */
    suspend fun duplicateTemplate(id: Long, newName: String? = null): Result<Long>

    // ==================== Execution Automation ====================

    /**
     * Start a workout from a template.
     * Creates a new Workout object pre-populated with the template's exercises.
     * If preloadLastSession is true, weights and reps from the last session will be used.
     *
     * @param templateId The ID of the template to start from
     * @param preloadLastSession Whether to preload data from the last session (default: true)
     * @return Result containing a new Workout object ready to be used
     */
    suspend fun startWorkout(templateId: Long, preloadLastSession: Boolean = true): Result<Workout>

    /**
     * Get the last session data for a template.
     * This is useful for displaying previous performance to the user.
     *
     * @param templateId The ID of the template
     * @return Result containing LastSessionData, or null if no previous session exists
     */
    suspend fun getLastSessionData(templateId: Long): Result<LastSessionData?>

    /**
     * Save a completed workout as a new template.
     * This allows users to capture a workout they liked and reuse it.
     *
     * @param workoutId The ID of the workout to save as template
     * @param templateName The name for the new template
     * @param description Optional description for the template
     * @return Result containing the new template ID on success
     */
    suspend fun saveWorkoutAsTemplate(
        workoutId: Long,
        templateName: String,
        description: String? = null
    ): Result<Long>

    /**
     * Update an existing template using data from a completed workout.
     * Overwrites exercises in the template with those from the workout.
     *
     * @param templateId The ID of the template to update
     * @param workoutId The ID of the workout to use as source
     * @return Result indicating success or failure
     */
    suspend fun updateTemplateFromWorkout(
        templateId: Long,
        workoutId: Long
    ): Result<Unit>

    /**
     * Update an existing template using an in-memory workout object.
     * Faster than the workoutId overload — avoids re-fetching workout data from DB.
     *
     * @param templateId The ID of the template to update
     * @param workout The workout object already in memory
     * @return Result indicating success or failure
     */
    suspend fun updateTemplateFromWorkout(
        templateId: Long,
        workout: Workout
    ): Result<Unit>
}

