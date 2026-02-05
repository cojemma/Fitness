package com.fitness.sdk.domain.model

/**
 * Aggregated history statistics for an exercise across all workouts.
 *
 * @property totalSessions Number of workouts where this exercise was performed
 * @property totalSets Total number of sets recorded
 * @property maxWeight Highest weight used (kg), null for bodyweight or no data
 * @property estimated1RM Estimated one-rep max (kg) from best set using Epley formula, null if not calculable
 * @property historyByDate List of session summaries ordered by date descending
 */
data class ExerciseHistory(
    val totalSessions: Int,
    val totalSets: Int,
    val maxWeight: Float?,
    val estimated1RM: Float?,
    val historyByDate: List<ExerciseSessionSummary> = emptyList()
)

/**
 * Summary of an exercise performed in a single workout session.
 */
data class ExerciseSessionSummary(
    val workoutId: Long,
    val workoutDate: Long,
    val bestSet: String,
    val setsCount: Int,
    val totalVolume: Float
)
