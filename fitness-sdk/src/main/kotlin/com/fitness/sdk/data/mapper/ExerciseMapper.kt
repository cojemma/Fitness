package com.fitness.sdk.data.mapper

import com.fitness.sdk.data.local.entity.ExerciseEntity
import com.fitness.sdk.domain.model.Exercise

/**
 * Mapper functions for converting between Exercise domain model and ExerciseEntity.
 */
object ExerciseMapper {

    /**
     * Convert domain Exercise to database ExerciseEntity.
     */
    fun toEntity(exercise: Exercise, workoutId: Long): ExerciseEntity {
        return ExerciseEntity(
            id = exercise.id,
            workoutId = workoutId,
            name = exercise.name,
            sets = exercise.sets,
            reps = exercise.reps,
            weight = exercise.weight,
            durationSeconds = exercise.durationSeconds,
            restSeconds = exercise.restSeconds,
            notes = exercise.notes
        )
    }

    /**
     * Convert database ExerciseEntity to domain Exercise.
     */
    fun toDomain(entity: ExerciseEntity): Exercise {
        return Exercise(
            id = entity.id,
            workoutId = entity.workoutId,
            name = entity.name,
            sets = entity.sets,
            reps = entity.reps,
            weight = entity.weight,
            durationSeconds = entity.durationSeconds,
            restSeconds = entity.restSeconds,
            notes = entity.notes
        )
    }

    /**
     * Convert list of ExerciseEntity to list of Exercise.
     */
    fun toDomainList(entities: List<ExerciseEntity>): List<Exercise> {
        return entities.map { toDomain(it) }
    }

    /**
     * Convert list of Exercise to list of ExerciseEntity.
     */
    fun toEntityList(exercises: List<Exercise>, workoutId: Long): List<ExerciseEntity> {
        return exercises.map { toEntity(it, workoutId) }
    }
}
