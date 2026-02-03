package com.fitness.sdk.domain.model

/**
 * Domain model representing an exercise within a workout template.
 * Contains the exercise configuration and target sets.
 *
 * @property id Unique identifier for the template exercise (0 for new)
 * @property templateId ID of the parent workout template
 * @property exerciseName Name of the exercise
 * @property orderIndex Order of this exercise within the template (0-indexed)
 * @property supersetGroupId Group ID for superset exercises (null if not in superset)
 * @property sets List of target sets for this exercise
 * @property restSeconds Rest time between sets in seconds
 * @property notes Optional notes about this exercise
 */
data class TemplateExercise(
    val id: Long = 0,
    val templateId: Long = 0,
    val exerciseName: String,
    val orderIndex: Int = 0,
    val supersetGroupId: Int? = null,
    val sets: List<TemplateSet> = emptyList(),
    val restSeconds: Int = 60,
    val notes: String? = null
) {
    /**
     * Check if this exercise is part of a superset.
     */
    fun isInSuperset(): Boolean = supersetGroupId != null

    /**
     * Get the total number of sets (including warm-up sets).
     */
    fun getTotalSets(): Int = sets.size

    /**
     * Get the number of working sets (excluding warm-up sets).
     */
    fun getWorkingSets(): Int = sets.count { !it.isWarmupSet }

    /**
     * Get the number of warm-up sets.
     */
    fun getWarmupSets(): Int = sets.count { it.isWarmupSet }

    /**
     * Get a summary string for display (e.g., "4 sets × 8-12 reps").
     */
    fun getSummary(): String {
        val workingSets = getWorkingSets()
        val repsRange = sets
            .filter { !it.isWarmupSet }
            .mapNotNull { it.targetReps }
        
        return when {
            repsRange.isEmpty() -> "$workingSets sets"
            repsRange.distinct().size == 1 -> "$workingSets sets × ${repsRange.first()} reps"
            else -> "$workingSets sets × ${repsRange.minOrNull()}-${repsRange.maxOrNull()} reps"
        }
    }
}
