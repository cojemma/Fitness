package com.fitness.sdk.api

import com.fitness.sdk.domain.model.LastSessionData
import com.fitness.sdk.domain.model.Workout
import com.fitness.sdk.domain.model.WorkoutTemplate
import com.fitness.sdk.domain.usecase.DeleteTemplateUseCase
import com.fitness.sdk.domain.usecase.DuplicateTemplateUseCase
import com.fitness.sdk.domain.usecase.GetLastSessionDataUseCase
import com.fitness.sdk.domain.usecase.GetTemplateByIdUseCase
import com.fitness.sdk.domain.usecase.GetTemplatesUseCase
import com.fitness.sdk.domain.usecase.SaveTemplateUseCase
import com.fitness.sdk.domain.usecase.SaveWorkoutAsTemplateUseCase
import com.fitness.sdk.domain.usecase.StartWorkoutFromTemplateUseCase
import kotlinx.coroutines.flow.Flow

/**
 * Implementation of TemplateManager using use cases.
 */
class TemplateManagerImpl(
    private val saveTemplateUseCase: SaveTemplateUseCase,
    private val getTemplatesUseCase: GetTemplatesUseCase,
    private val getTemplateByIdUseCase: GetTemplateByIdUseCase,
    private val deleteTemplateUseCase: DeleteTemplateUseCase,
    private val duplicateTemplateUseCase: DuplicateTemplateUseCase,
    private val startWorkoutFromTemplateUseCase: StartWorkoutFromTemplateUseCase,
    private val getLastSessionDataUseCase: GetLastSessionDataUseCase,
    private val saveWorkoutAsTemplateUseCase: SaveWorkoutAsTemplateUseCase
) : TemplateManager {

    override suspend fun saveTemplate(template: WorkoutTemplate): Result<Long> {
        return saveTemplateUseCase(template)
    }

    override suspend fun getTemplateById(id: Long): Result<WorkoutTemplate> {
        return getTemplateByIdUseCase(id)
    }

    override fun observeTemplates(): Flow<List<WorkoutTemplate>> {
        return getTemplatesUseCase()
    }

    override suspend fun getAllTemplates(): Result<List<WorkoutTemplate>> {
        return getTemplatesUseCase.getAll()
    }

    override suspend fun deleteTemplate(id: Long): Result<Unit> {
        return deleteTemplateUseCase(id)
    }

    override suspend fun duplicateTemplate(id: Long, newName: String?): Result<Long> {
        return duplicateTemplateUseCase(id, newName)
    }

    override suspend fun startWorkout(templateId: Long, preloadLastSession: Boolean): Result<Workout> {
        return startWorkoutFromTemplateUseCase(templateId, preloadLastSession)
    }

    override suspend fun getLastSessionData(templateId: Long): Result<LastSessionData?> {
        return getLastSessionDataUseCase(templateId)
    }

    override suspend fun saveWorkoutAsTemplate(
        workoutId: Long,
        templateName: String,
        description: String?
    ): Result<Long> {
        return saveWorkoutAsTemplateUseCase(workoutId, templateName, description)
    }
}

