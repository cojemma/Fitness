package com.fitness.sdk.domain.model

/**
 * Domain model representing a single set within an exercise.
 * Records the actual performance data for each set during a workout.
 *
 * @property id Unique identifier for the set (0 for new sets)
 * @property exerciseId ID of the parent exercise this set belongs to
 * @property setNumber The set number (1-indexed)
 * @property reps Number of repetitions completed
 * @property weight Weight used in kilograms (null for bodyweight exercises)
 * @property isWarmupSet Whether this is a warm-up set
 * @property completedAt Timestamp when this set was completed
 */
data class ExerciseSet(
    val id: Long = 0,
    val exerciseId: Long = 0,
    val setNumber: Int,
    val reps: Int,
    val weight: Float? = null,
    val isWarmupSet: Boolean = false,
    val completedAt: Long = System.currentTimeMillis()
) {
    /**
     * Calculate the volume (reps × weight) for this set.
     * Returns 0 if weight is null.
     */
    fun calculateVolume(): Float {
        return weight?.let { reps * it } ?: 0f
    }

    /**
     * Get a formatted display string for this set.
     */
    fun getDisplayString(): String {
        return if (weight != null) {
            "$reps reps × ${weight}kg"
        } else {
            "$reps reps"
        }
    }
}
