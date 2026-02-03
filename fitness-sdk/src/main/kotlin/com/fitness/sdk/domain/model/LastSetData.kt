package com.fitness.sdk.domain.model

/**
 * Data class representing the last recorded data for a set.
 * Used to pre-populate workout forms with the most recent performance data.
 *
 * @property exerciseName Name of the exercise
 * @property setNumber The set number (1-indexed)
 * @property actualReps The reps completed in the last session
 * @property actualWeight The weight used in the last session
 * @property completedAt Timestamp when this set was completed
 */
data class LastSetData(
    val exerciseName: String,
    val setNumber: Int,
    val actualReps: Int,
    val actualWeight: Float?,
    val completedAt: Long
) {
    /**
     * Get a formatted display string for the last performance.
     */
    fun getDisplayString(): String {
        return if (actualWeight != null) {
            "$actualReps reps × ${actualWeight}kg"
        } else {
            "$actualReps reps"
        }
    }
}

/**
 * Container for all last session data for a template.
 */
data class LastSessionData(
    val templateId: Long,
    val lastWorkoutId: Long,
    val lastWorkoutDate: Long,
    val exerciseData: Map<String, List<LastSetData>> // exerciseName -> list of set data
) {
    /**
     * Get the last set data for a specific exercise and set number.
     */
    fun getSetData(exerciseName: String, setNumber: Int): LastSetData? {
        return exerciseData[exerciseName]?.find { it.setNumber == setNumber }
    }

    /**
     * Get all set data for a specific exercise.
     */
    fun getExerciseData(exerciseName: String): List<LastSetData> {
        return exerciseData[exerciseName] ?: emptyList()
    }
}
