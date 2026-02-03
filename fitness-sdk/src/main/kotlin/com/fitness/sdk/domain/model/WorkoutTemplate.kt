package com.fitness.sdk.domain.model

/**
 * Domain model representing a workout template.
 * A template defines a reusable workout routine with target exercises and sets.
 *
 * @property id Unique identifier for the template (0 for new templates)
 * @property name Template name (e.g., "Push Day A", "Leg Day")
 * @property description Optional description or notes about the template
 * @property targetMuscleGroups List of primary muscle groups targeted
 * @property estimatedDurationMinutes Estimated workout duration in minutes
 * @property exercises List of template exercises with their target sets
 * @property createdAt Timestamp when the template was created
 * @property updatedAt Timestamp when the template was last updated
 * @property version Version number for tracking template changes
 */
data class WorkoutTemplate(
    val id: Long = 0,
    val name: String,
    val description: String? = null,
    val targetMuscleGroups: List<MuscleGroup> = emptyList(),
    val estimatedDurationMinutes: Int = 0,
    val exercises: List<TemplateExercise> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val version: Int = 1
) {
    /**
     * Get the total number of exercises in this template.
     */
    fun getTotalExercises(): Int = exercises.size

    /**
     * Get the total number of working sets across all exercises.
     */
    fun getTotalWorkingSets(): Int = exercises.sumOf { it.getWorkingSets() }

    /**
     * Get the total number of sets (including warm-up) across all exercises.
     */
    fun getTotalSets(): Int = exercises.sumOf { it.getTotalSets() }

    /**
     * Get all superset groups in this template.
     * Returns a map of groupId to list of exercises in that superset.
     */
    fun getSupersets(): Map<Int, List<TemplateExercise>> {
        return exercises
            .filter { it.isInSuperset() }
            .groupBy { it.supersetGroupId!! }
    }

    /**
     * Get exercises that are not part of any superset.
     */
    fun getSingleExercises(): List<TemplateExercise> {
        return exercises.filter { !it.isInSuperset() }
    }

    /**
     * Check if this template contains any supersets.
     */
    fun hasSupersets(): Boolean = exercises.any { it.isInSuperset() }

    /**
     * Get muscle groups as a formatted string for display.
     */
    fun getMuscleGroupsDisplay(): String {
        return targetMuscleGroups.joinToString(", ") { muscleGroup ->
            muscleGroup.name.lowercase().replaceFirstChar { it.uppercase() }.replace("_", " ")
        }
    }

    /**
     * Create a new version of this template with incremented version number.
     */
    fun createNewVersion(): WorkoutTemplate {
        return copy(
            version = version + 1,
            updatedAt = System.currentTimeMillis()
        )
    }

    /**
     * Get a summary of this template for display.
     */
    fun getSummary(): String {
        val exerciseCount = getTotalExercises()
        val setCount = getTotalWorkingSets()
        return "$exerciseCount exercises, $setCount sets"
    }

    /**
     * Calculate the total planned volume for working sets only.
     * Volume = sum of (sets × reps × weight) for all working sets.
     * Warm-up sets are excluded from this calculation.
     *
     * @return Total planned volume in kg
     */
    fun calculatePlannedVolume(): Float {
        return exercises.sumOf { exercise ->
            exercise.sets
                .filter { !it.isWarmupSet }
                .sumOf { set ->
                    val reps = set.targetReps ?: 0
                    val weight = set.targetWeight ?: 0f
                    (reps * weight).toDouble()
                }
        }.toFloat()
    }

    /**
     * Get the total number of warm-up sets across all exercises.
     */
    fun getTotalWarmupSets(): Int {
        return exercises.sumOf { it.getWarmupSets() }
    }

    /**
     * Check if any exercises use percentage-based training.
     */
    fun usesPercentageBasedTraining(): Boolean {
        return exercises.any { exercise ->
            exercise.sets.any { it.isPercentageBased() }
        }
    }
}

