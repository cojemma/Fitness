package com.fitness.sdk.data.mapper

import com.fitness.sdk.data.local.entity.ExerciseSetEntity
import com.fitness.sdk.domain.model.ExerciseSet

/**
 * Mapper functions for converting between ExerciseSet domain model and ExerciseSetEntity.
 */
object ExerciseSetMapper {

    /**
     * Convert domain ExerciseSet to database ExerciseSetEntity.
     */
    fun toEntity(exerciseSet: ExerciseSet, exerciseId: Long): ExerciseSetEntity {
        return ExerciseSetEntity(
            id = exerciseSet.id,
            exerciseId = exerciseId,
            setNumber = exerciseSet.setNumber,
            reps = exerciseSet.reps,
            weight = exerciseSet.weight,
            isWarmupSet = exerciseSet.isWarmupSet,
            completedAt = exerciseSet.completedAt
        )
    }

    /**
     * Convert database ExerciseSetEntity to domain ExerciseSet.
     */
    fun toDomain(entity: ExerciseSetEntity): ExerciseSet {
        return ExerciseSet(
            id = entity.id,
            exerciseId = entity.exerciseId,
            setNumber = entity.setNumber,
            reps = entity.reps,
            weight = entity.weight,
            isWarmupSet = entity.isWarmupSet,
            completedAt = entity.completedAt
        )
    }

    /**
     * Convert list of ExerciseSetEntity to list of ExerciseSet.
     */
    fun toDomainList(entities: List<ExerciseSetEntity>): List<ExerciseSet> {
        return entities.map { toDomain(it) }
    }

    /**
     * Convert list of ExerciseSet to list of ExerciseSetEntity.
     */
    fun toEntityList(exerciseSets: List<ExerciseSet>, exerciseId: Long): List<ExerciseSetEntity> {
        return exerciseSets.map { toEntity(it, exerciseId) }
    }
}
