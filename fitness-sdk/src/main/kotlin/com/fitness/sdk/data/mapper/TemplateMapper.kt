package com.fitness.sdk.data.mapper

import com.fitness.sdk.data.local.entity.TemplateExerciseEntity
import com.fitness.sdk.data.local.entity.TemplateExerciseWithSets
import com.fitness.sdk.data.local.entity.TemplateSetEntity
import com.fitness.sdk.data.local.entity.WorkoutTemplateEntity
import com.fitness.sdk.data.local.entity.WorkoutTemplateWithExercises
import com.fitness.sdk.domain.model.MuscleGroup
import com.fitness.sdk.domain.model.TemplateExercise
import com.fitness.sdk.domain.model.TemplateSet
import com.fitness.sdk.domain.model.WorkoutTemplate
import org.json.JSONArray

/**
 * Mapper for converting between Template domain models and Room entities.
 */
object TemplateMapper {

    // ==================== Domain -> Entity ====================

    fun toEntity(template: WorkoutTemplate): WorkoutTemplateEntity {
        return WorkoutTemplateEntity(
            id = template.id,
            name = template.name,
            description = template.description,
            targetMuscleGroups = muscleGroupsToJson(template.targetMuscleGroups),
            estimatedDurationMinutes = template.estimatedDurationMinutes,
            createdAt = template.createdAt,
            updatedAt = template.updatedAt,
            version = template.version
        )
    }

    fun toEntity(exercise: TemplateExercise): TemplateExerciseEntity {
        return TemplateExerciseEntity(
            id = exercise.id,
            templateId = exercise.templateId,
            exerciseName = exercise.exerciseName,
            orderIndex = exercise.orderIndex,
            supersetGroupId = exercise.supersetGroupId,
            restSeconds = exercise.restSeconds,
            notes = exercise.notes
        )
    }

    fun toEntity(set: TemplateSet): TemplateSetEntity {
        return TemplateSetEntity(
            id = set.id,
            templateExerciseId = set.templateExerciseId,
            setNumber = set.setNumber,
            targetReps = set.targetReps,
            targetWeight = set.targetWeight,
            targetRpe = set.targetRpe,
            percentageOf1RM = set.percentageOf1RM,
            isWarmupSet = set.isWarmupSet
        )
    }

    // ==================== Entity -> Domain ====================

    fun toDomain(entity: WorkoutTemplateEntity): WorkoutTemplate {
        return WorkoutTemplate(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            targetMuscleGroups = jsonToMuscleGroups(entity.targetMuscleGroups),
            estimatedDurationMinutes = entity.estimatedDurationMinutes,
            exercises = emptyList(), // Will be populated separately
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            version = entity.version
        )
    }

    fun toDomain(entity: TemplateExerciseEntity, sets: List<TemplateSet> = emptyList()): TemplateExercise {
        return TemplateExercise(
            id = entity.id,
            templateId = entity.templateId,
            exerciseName = entity.exerciseName,
            orderIndex = entity.orderIndex,
            supersetGroupId = entity.supersetGroupId,
            sets = sets,
            restSeconds = entity.restSeconds,
            notes = entity.notes
        )
    }

    fun toDomain(entity: TemplateSetEntity): TemplateSet {
        return TemplateSet(
            id = entity.id,
            templateExerciseId = entity.templateExerciseId,
            setNumber = entity.setNumber,
            targetReps = entity.targetReps,
            targetWeight = entity.targetWeight,
            targetRpe = entity.targetRpe,
            percentageOf1RM = entity.percentageOf1RM,
            isWarmupSet = entity.isWarmupSet
        )
    }

    // ==================== Complex Mappings ====================

    fun toDomain(entityWithExercises: WorkoutTemplateWithExercises): WorkoutTemplate {
        val exercises = entityWithExercises.exercisesWithSets
            .map { exerciseWithSets ->
                toDomain(exerciseWithSets)
            }
            .sortedBy { it.orderIndex }

        return WorkoutTemplate(
            id = entityWithExercises.template.id,
            name = entityWithExercises.template.name,
            description = entityWithExercises.template.description,
            targetMuscleGroups = jsonToMuscleGroups(entityWithExercises.template.targetMuscleGroups),
            estimatedDurationMinutes = entityWithExercises.template.estimatedDurationMinutes,
            exercises = exercises,
            createdAt = entityWithExercises.template.createdAt,
            updatedAt = entityWithExercises.template.updatedAt,
            version = entityWithExercises.template.version
        )
    }

    fun toDomain(exerciseWithSets: TemplateExerciseWithSets): TemplateExercise {
        val sets = exerciseWithSets.sets
            .map { toDomain(it) }
            .sortedBy { it.setNumber }

        return TemplateExercise(
            id = exerciseWithSets.exercise.id,
            templateId = exerciseWithSets.exercise.templateId,
            exerciseName = exerciseWithSets.exercise.exerciseName,
            orderIndex = exerciseWithSets.exercise.orderIndex,
            supersetGroupId = exerciseWithSets.exercise.supersetGroupId,
            sets = sets,
            restSeconds = exerciseWithSets.exercise.restSeconds,
            notes = exerciseWithSets.exercise.notes
        )
    }

    // ==================== Helper Functions ====================

    private fun muscleGroupsToJson(muscleGroups: List<MuscleGroup>): String {
        val jsonArray = JSONArray()
        muscleGroups.forEach { jsonArray.put(it.name) }
        return jsonArray.toString()
    }

    private fun jsonToMuscleGroups(json: String): List<MuscleGroup> {
        if (json.isBlank()) return emptyList()
        
        return try {
            val jsonArray = JSONArray(json)
            (0 until jsonArray.length()).mapNotNull { index ->
                try {
                    MuscleGroup.valueOf(jsonArray.getString(index))
                } catch (e: IllegalArgumentException) {
                    null
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
