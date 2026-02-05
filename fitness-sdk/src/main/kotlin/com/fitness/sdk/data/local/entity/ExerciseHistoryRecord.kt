package com.fitness.sdk.data.local.entity

/**
 * Record of a single set performed for an exercise, with workout context.
 * Used for exercise history queries. Maps to Room query result columns.
 */
data class ExerciseHistoryRecord(
    val workoutId: Long,
    val workoutDate: Long,
    val weight: Float?,
    val reps: Int,
    val completedAt: Long,
    val isWarmupSet: Boolean
)
