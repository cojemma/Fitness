package com.fitness.sdk.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Data class representing a workout with all its associated exercises.
 * Used for Room's @Transaction queries to fetch related data.
 */
data class WorkoutWithExercises(
    @Embedded
    val workout: WorkoutEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "workoutId"
    )
    val exercises: List<ExerciseEntity>
)
