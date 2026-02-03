package com.fitness.sdk.domain.model

/**
 * Domain model representing a workout session.
 *
 * @property id Unique identifier for the workout (0 for new workouts)
 * @property name Name/title of the workout (e.g., "Morning Run", "Leg Day")
 * @property type Type of workout from [WorkoutType] enum
 * @property startTime Timestamp when the workout started (milliseconds since epoch)
 * @property endTime Timestamp when the workout ended (null if still in progress)
 * @property durationMinutes Total duration of the workout in minutes
 * @property caloriesBurned Estimated calories burned during the workout
 * @property exercises List of exercises performed during the workout
 * @property notes Optional notes or comments about the workout
 * @property createdAt Timestamp when the workout was created
 * @property updatedAt Timestamp when the workout was last updated
 */
data class Workout(
    val id: Long = 0,
    val name: String,
    val type: WorkoutType = WorkoutType.OTHER,
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long? = null,
    val durationMinutes: Int = 0,
    val caloriesBurned: Int = 0,
    val exercises: List<Exercise> = emptyList(),
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * Check if the workout is currently in progress.
     */
    fun isInProgress(): Boolean = endTime == null

    /**
     * Calculate the total exercise volume for strength workouts.
     */
    fun calculateTotalVolume(): Float {
        return exercises.sumOf { it.calculateVolume().toDouble() }.toFloat()
    }

    /**
     * Get the total number of sets across all exercises.
     */
    fun getTotalSets(): Int = exercises.sumOf { it.sets }

    /**
     * Get the total number of reps across all exercises.
     */
    fun getTotalReps(): Int = exercises.sumOf { it.sets * it.reps }

    /**
     * Create a copy of this workout with updated endTime and calculated duration.
     */
    fun finish(): Workout {
        val now = System.currentTimeMillis()
        val duration = ((now - startTime) / 60000).toInt()
        return copy(
            endTime = now,
            durationMinutes = duration,
            updatedAt = now
        )
    }
}
