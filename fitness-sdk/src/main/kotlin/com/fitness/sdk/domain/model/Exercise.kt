package com.fitness.sdk.domain.model

/**
 * Domain model representing an individual exercise within a workout.
 *
 * @property id Unique identifier for the exercise (0 for new exercises)
 * @property workoutId ID of the parent workout this exercise belongs to
 * @property name Name of the exercise (e.g., "Bench Press", "Squats")
 * @property sets Number of sets performed (aggregate count)
 * @property reps Number of repetitions per set (average or target)
 * @property weight Weight used in kilograms (null for bodyweight exercises)
 * @property durationSeconds Duration in seconds (for timed exercises like planks)
 * @property restSeconds Rest time between sets in seconds
 * @property notes Optional notes about the exercise
 * @property setRecords Individual set records with actual performance data
 */
data class Exercise(
    val id: Long = 0,
    val workoutId: Long = 0,
    val name: String,
    val sets: Int = 0,
    val reps: Int = 0,
    val weight: Float? = null,
    val durationSeconds: Int = 0,
    val restSeconds: Int = 0,
    val notes: String? = null,
    val setRecords: List<ExerciseSet> = emptyList()
) {
    /**
     * Calculate the total volume (sets × reps × weight) for this exercise.
     * If setRecords are present, calculates from individual sets.
     * Returns 0 if weight is null.
     */
    fun calculateVolume(): Float {
        return if (setRecords.isNotEmpty()) {
            setRecords.sumOf { it.calculateVolume().toDouble() }.toFloat()
        } else {
            weight?.let { sets * reps * it } ?: 0f
        }
    }

    /**
     * Check if this is a timed exercise (duration-based rather than rep-based).
     */
    fun isTimedExercise(): Boolean = durationSeconds > 0 && reps == 0
}

