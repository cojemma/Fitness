package com.fitness.sdk.domain.model

/**
 * Domain model representing a single set within a template exercise.
 * Defines the target goals for each set.
 *
 * @property id Unique identifier for the set (0 for new sets)
 * @property templateExerciseId ID of the parent template exercise
 * @property setNumber The set number (1-indexed)
 * @property targetReps Target number of repetitions for this set
 * @property targetWeight Target weight in kilograms
 * @property targetRpe Target RPE (Rate of Perceived Exertion, 1-10 scale)
 * @property percentageOf1RM Target weight as percentage of 1RM (0-100)
 * @property isWarmupSet Whether this is a warm-up set (excluded from volume calculations)
 */
data class TemplateSet(
    val id: Long = 0,
    val templateExerciseId: Long = 0,
    val setNumber: Int,
    val targetReps: Int? = null,
    val targetWeight: Float? = null,
    val targetRpe: Float? = null,
    val percentageOf1RM: Float? = null,
    val isWarmupSet: Boolean = false
) {
    /**
     * Check if this set has a weight target (either direct weight or percentage-based).
     */
    fun hasWeightTarget(): Boolean = targetWeight != null || percentageOf1RM != null

    /**
     * Check if this set uses percentage-based training.
     */
    fun isPercentageBased(): Boolean = percentageOf1RM != null

    /**
     * Calculate the actual weight based on a given 1RM value.
     * Returns targetWeight if not percentage-based.
     *
     * @param oneRepMax The user's 1RM for this exercise
     * @return Calculated weight, or null if no weight target
     */
    fun calculateWeight(oneRepMax: Float? = null): Float? {
        return when {
            percentageOf1RM != null && oneRepMax != null -> {
                (oneRepMax * percentageOf1RM / 100f)
            }
            targetWeight != null -> targetWeight
            else -> null
        }
    }
}
