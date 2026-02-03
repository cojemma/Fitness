package com.fitness.sdk.domain.model

/**
 * User profile containing personal training data like one-rep maximums.
 * Used for percentage-based training calculations.
 *
 * @property userId Unique identifier for the user
 * @property oneRepMaxes Map of exercise names to their 1RM values in kg
 * @property updatedAt Timestamp when the profile was last updated
 */
data class UserProfile(
    val userId: Long = 0,
    val oneRepMaxes: Map<String, Float> = emptyMap(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * Get the 1RM for a specific exercise.
     *
     * @param exerciseName Name of the exercise
     * @return 1RM weight in kg, or null if not set
     */
    fun getOneRepMax(exerciseName: String): Float? {
        return oneRepMaxes[exerciseName]
    }

    /**
     * Calculate the weight for a given percentage of 1RM.
     *
     * @param exerciseName Name of the exercise
     * @param percentage Percentage of 1RM (0-100)
     * @return Calculated weight, or null if 1RM is not set
     */
    fun calculatePercentageWeight(exerciseName: String, percentage: Float): Float? {
        val oneRM = getOneRepMax(exerciseName) ?: return null
        return (oneRM * percentage / 100f).roundToNearestPlate()
    }

    /**
     * Update or add a 1RM for an exercise.
     *
     * @param exerciseName Name of the exercise
     * @param weight 1RM weight in kg
     * @return New UserProfile with updated 1RM
     */
    fun updateOneRepMax(exerciseName: String, weight: Float): UserProfile {
        val updatedMaxes = oneRepMaxes.toMutableMap()
        updatedMaxes[exerciseName] = weight
        return copy(
            oneRepMaxes = updatedMaxes,
            updatedAt = System.currentTimeMillis()
        )
    }

    /**
     * Estimate 1RM from a working set using Epley formula.
     * 1RM = weight × (1 + reps/30)
     *
     * @param weight Weight used in the set
     * @param reps Number of reps completed
     * @return Estimated 1RM
     */
    companion object {
        fun estimate1RM(weight: Float, reps: Int): Float {
            if (reps <= 0) return weight
            if (reps == 1) return weight
            return weight * (1 + reps / 30f)
        }

        /**
         * Round a weight to the nearest standard plate increment.
         * Common increments are 2.5kg or 5lb.
         *
         * @param increment The plate increment to round to (default: 2.5kg)
         * @return Rounded weight
         */
        fun Float.roundToNearestPlate(increment: Float = 2.5f): Float {
            return (this / increment).toInt() * increment
        }
    }
}

/**
 * Extension function to round weight to nearest plate.
 */
private fun Float.roundToNearestPlate(increment: Float = 2.5f): Float {
    return (this / increment).toInt() * increment
}
