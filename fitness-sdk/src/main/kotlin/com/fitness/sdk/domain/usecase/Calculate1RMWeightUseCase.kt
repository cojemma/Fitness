package com.fitness.sdk.domain.usecase

import com.fitness.sdk.domain.model.TemplateSet
import com.fitness.sdk.domain.model.UserProfile

/**
 * Use case for calculating actual weights from percentage-based training.
 * Converts template sets with percentageOf1RM to actual weight values.
 */
class Calculate1RMWeightUseCase {

    /**
     * Calculate the actual weight for a template set based on user's 1RM.
     *
     * @param set The template set with percentage or weight target
     * @param exerciseName Name of the exercise
     * @param userProfile User's profile containing 1RM data
     * @return Calculated weight in kg, or null if cannot be calculated
     */
    operator fun invoke(
        set: TemplateSet,
        exerciseName: String,
        userProfile: UserProfile?
    ): Float? {
        // If set has a direct weight target, use it
        if (set.targetWeight != null) {
            return set.targetWeight
        }

        // If set has a percentage and we have user's 1RM, calculate
        if (set.percentageOf1RM != null && userProfile != null) {
            return userProfile.calculatePercentageWeight(exerciseName, set.percentageOf1RM)
        }

        return null
    }

    /**
     * Calculate weights for all sets of an exercise.
     *
     * @param sets List of template sets
     * @param exerciseName Name of the exercise
     * @param userProfile User's profile containing 1RM data
     * @return Map of set number to calculated weight
     */
    fun calculateForExercise(
        sets: List<TemplateSet>,
        exerciseName: String,
        userProfile: UserProfile?
    ): Map<Int, Float?> {
        return sets.associate { set ->
            set.setNumber to invoke(set, exerciseName, userProfile)
        }
    }

    /**
     * Check if any sets in the list require 1RM data for calculation.
     *
     * @param sets List of template sets to check
     * @return True if any set uses percentage-based training
     */
    fun requiresUserProfile(sets: List<TemplateSet>): Boolean {
        return sets.any { it.isPercentageBased() }
    }
}
