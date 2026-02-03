package com.fitness.sdk.data.repository

import com.fitness.sdk.data.local.dao.TemplateDao
import com.fitness.sdk.data.mapper.TemplateMapper
import com.fitness.sdk.domain.model.WorkoutTemplate
import com.fitness.sdk.domain.repository.TemplateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementation of TemplateRepository using Room database.
 */
class TemplateRepositoryImpl(
    private val templateDao: TemplateDao
) : TemplateRepository {

    override suspend fun saveTemplate(template: WorkoutTemplate): Result<Long> {
        return try {
            val templateEntity = TemplateMapper.toEntity(template)
            val exerciseEntities = template.exercises.map { TemplateMapper.toEntity(it) }
            val setsByExerciseIndex = template.exercises.mapIndexed { index, exercise ->
                index to exercise.sets.map { TemplateMapper.toEntity(it) }
            }.toMap()

            val templateId = templateDao.saveCompleteTemplate(
                template = templateEntity,
                exercises = exerciseEntities,
                setsByExerciseIndex = setsByExerciseIndex
            )
            Result.success(templateId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTemplateById(id: Long): Result<WorkoutTemplate?> {
        return try {
            val templateWithExercises = templateDao.getTemplateWithExercises(id)
            val template = templateWithExercises?.let { TemplateMapper.toDomain(it) }
            Result.success(template)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeAllTemplates(): Flow<List<WorkoutTemplate>> {
        return templateDao.observeAllTemplatesWithExercises().map { list ->
            list.map { TemplateMapper.toDomain(it) }
        }
    }

    override suspend fun getAllTemplates(): Result<List<WorkoutTemplate>> {
        return try {
            val templates = templateDao.getAllTemplatesWithExercises()
                .map { TemplateMapper.toDomain(it) }
            Result.success(templates)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteTemplate(id: Long): Result<Unit> {
        return try {
            templateDao.deleteTemplateById(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun duplicateTemplate(id: Long, newName: String?): Result<Long> {
        return try {
            val original = templateDao.getTemplateWithExercises(id)
                ?: return Result.failure(IllegalArgumentException("Template not found: $id"))

            val originalTemplate = TemplateMapper.toDomain(original)
            val duplicatedTemplate = originalTemplate.copy(
                id = 0,
                name = newName ?: "${originalTemplate.name} (Copy)",
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                version = 1,
                exercises = originalTemplate.exercises.map { exercise ->
                    exercise.copy(
                        id = 0,
                        templateId = 0,
                        sets = exercise.sets.map { set ->
                            set.copy(id = 0, templateExerciseId = 0)
                        }
                    )
                }
            )

            saveTemplate(duplicatedTemplate)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateExerciseOrder(templateId: Long, exerciseIds: List<Long>): Result<Unit> {
        return try {
            val exercises = templateDao.getExercisesByTemplateId(templateId)
            
            exerciseIds.forEachIndexed { newIndex, exerciseId ->
                exercises.find { it.id == exerciseId }?.let { exercise ->
                    templateDao.updateExercise(exercise.copy(orderIndex = newIndex))
                }
            }

            // Update the template's updatedAt timestamp
            templateDao.getTemplateById(templateId)?.let { template ->
                templateDao.updateTemplate(template.copy(updatedAt = System.currentTimeMillis()))
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
