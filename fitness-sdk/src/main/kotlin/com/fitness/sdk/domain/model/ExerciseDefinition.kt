package com.fitness.sdk.domain.model

/**
 * Domain model representing a predefined exercise in the exercise library.
 * This is different from [Exercise] which represents an exercise performed in a workout.
 *
 * @property id Unique identifier for the exercise definition
 * @property name Display name of the exercise (e.g., "Bench Press")
 * @property category The training category this exercise belongs to
 * @property primaryMuscle The main muscle group targeted by this exercise
 * @property secondaryMuscles Additional muscle groups worked during this exercise
 * @property description Brief description of the exercise
 * @property instructions Step-by-step instructions for performing the exercise
 * @property isTimeBased True if exercise is measured by duration (e.g., planks, running)
 * @property defaultSets Suggested number of sets
 * @property defaultReps Suggested number of reps per set (for rep-based exercises)
 * @property defaultDurationSeconds Suggested duration in seconds (for time-based exercises)
 * @property isCustom True if this exercise was created by the user (not a predefined library exercise)
 */
data class ExerciseDefinition(
    val id: String,
    val name: String,
    val category: ExerciseCategory,
    val primaryMuscle: MuscleGroup,
    val secondaryMuscles: List<MuscleGroup> = emptyList(),
    val description: String? = null,
    val instructions: String? = null,
    val isTimeBased: Boolean = false,
    val defaultSets: Int? = 3,
    val defaultReps: Int? = 10,
    val defaultDurationSeconds: Int? = null,
    val isCustom: Boolean = false
) {
    /**
     * Convert this exercise definition to a workout [Exercise].
     *
     * @param sets Number of sets (defaults to [defaultSets] or 3)
     * @param reps Number of reps per set (defaults to [defaultReps] or 10)
     * @param weight Weight in kg (null for bodyweight exercises)
     * @param durationSeconds Duration in seconds (defaults to [defaultDurationSeconds] or 0)
     * @param restSeconds Rest time between sets in seconds
     * @param notes Optional notes about the exercise
     * @return A new [Exercise] instance ready to be added to a workout
     */
    fun toExercise(
        sets: Int = defaultSets ?: 3,
        reps: Int = defaultReps ?: 10,
        weight: Float? = null,
        durationSeconds: Int = defaultDurationSeconds ?: 0,
        restSeconds: Int = 60,
        notes: String? = null
    ): Exercise = Exercise(
        name = name,
        sets = sets,
        reps = if (isTimeBased) 0 else reps,
        weight = weight,
        durationSeconds = if (isTimeBased) durationSeconds else 0,
        restSeconds = restSeconds,
        notes = notes
    )
}
