package com.fitness.sdk.data.mapper

import com.fitness.sdk.data.local.entity.CustomExerciseDefinitionEntity
import com.fitness.sdk.domain.model.ExerciseCategory
import com.fitness.sdk.domain.model.ExerciseDefinition
import com.fitness.sdk.domain.model.MuscleGroup
import org.json.JSONArray

/**
 * Mapper for converting between CustomExerciseDefinitionEntity and ExerciseDefinition.
 */
object CustomExerciseMapper {

    fun toEntity(definition: ExerciseDefinition): CustomExerciseDefinitionEntity {
        return CustomExerciseDefinitionEntity(
            id = definition.id,
            name = definition.name,
            category = definition.category.name,
            primaryMuscle = definition.primaryMuscle.name,
            secondaryMuscles = muscleGroupsToJson(definition.secondaryMuscles),
            description = definition.description,
            instructions = definition.instructions,
            isTimeBased = definition.isTimeBased,
            defaultSets = definition.defaultSets,
            defaultReps = definition.defaultReps,
            defaultDurationSeconds = definition.defaultDurationSeconds
        )
    }

    fun toDomain(entity: CustomExerciseDefinitionEntity): ExerciseDefinition {
        return ExerciseDefinition(
            id = entity.id,
            name = entity.name,
            category = try {
                ExerciseCategory.valueOf(entity.category)
            } catch (e: IllegalArgumentException) {
                ExerciseCategory.STRENGTH
            },
            primaryMuscle = try {
                MuscleGroup.valueOf(entity.primaryMuscle)
            } catch (e: IllegalArgumentException) {
                MuscleGroup.FULL_BODY
            },
            secondaryMuscles = jsonToMuscleGroups(entity.secondaryMuscles),
            description = entity.description,
            instructions = entity.instructions,
            isTimeBased = entity.isTimeBased,
            defaultSets = entity.defaultSets,
            defaultReps = entity.defaultReps,
            defaultDurationSeconds = entity.defaultDurationSeconds,
            isCustom = true
        )
    }

    fun toDomainList(entities: List<CustomExerciseDefinitionEntity>): List<ExerciseDefinition> {
        return entities.map { toDomain(it) }
    }

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
