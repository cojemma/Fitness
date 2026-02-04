package com.fitness.sdk.data.mapper

import com.fitness.sdk.data.local.entity.WorkoutEntity
import com.fitness.sdk.data.local.entity.WorkoutWithExercises
import com.fitness.sdk.domain.model.Exercise
import com.fitness.sdk.domain.model.Workout
import com.fitness.sdk.domain.model.WorkoutType

/**
 * Mapper functions for converting between Workout domain model and WorkoutEntity.
 */
object WorkoutMapper {

    /**
     * Convert domain Workout to database WorkoutEntity.
     */
    fun toEntity(workout: Workout): WorkoutEntity {
        return WorkoutEntity(
            id = workout.id,
            name = workout.name,
            type = workout.type.name,
            templateId = workout.templateId,
            startTime = workout.startTime,
            endTime = workout.endTime,
            durationMinutes = workout.durationMinutes,
            caloriesBurned = workout.caloriesBurned,
            notes = workout.notes,
            createdAt = workout.createdAt,
            updatedAt = workout.updatedAt
        )
    }

    /**
     * Convert database WorkoutWithExercises to domain Workout.
     */
    fun toDomain(workoutWithExercises: WorkoutWithExercises): Workout {
        val entity = workoutWithExercises.workout
        return Workout(
            id = entity.id,
            name = entity.name,
            type = parseWorkoutType(entity.type),
            templateId = entity.templateId,
            startTime = entity.startTime,
            endTime = entity.endTime,
            durationMinutes = entity.durationMinutes,
            caloriesBurned = entity.caloriesBurned,
            exercises = ExerciseMapper.toDomainList(workoutWithExercises.exercises),
            notes = entity.notes,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    /**
     * Convert WorkoutEntity to domain Workout with pre-built exercises list.
     * Use this when exercises have already been mapped with set records.
     */
    fun toDomain(entity: WorkoutEntity, exercises: List<Exercise>): Workout {
        return Workout(
            id = entity.id,
            name = entity.name,
            type = parseWorkoutType(entity.type),
            templateId = entity.templateId,
            startTime = entity.startTime,
            endTime = entity.endTime,
            durationMinutes = entity.durationMinutes,
            caloriesBurned = entity.caloriesBurned,
            exercises = exercises,
            notes = entity.notes,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    /**
     * Convert list of WorkoutWithExercises to list of Workout.
     */
    fun toDomainList(entities: List<WorkoutWithExercises>): List<Workout> {
        return entities.map { toDomain(it) }
    }

    /**
     * Parse WorkoutType from string, defaulting to OTHER if unknown.
     */
    private fun parseWorkoutType(type: String): WorkoutType {
        return try {
            WorkoutType.valueOf(type)
        } catch (e: IllegalArgumentException) {
            WorkoutType.OTHER
        }
    }
}

