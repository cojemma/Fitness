package com.fitness.sdk.data.local.entity

/**
 * Maps to Room query result for exercise session counts.
 */
data class ExerciseSessionCount(
    val exerciseName: String,
    val sessionCount: Int
)
