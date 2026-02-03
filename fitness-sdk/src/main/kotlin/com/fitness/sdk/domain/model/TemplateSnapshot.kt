package com.fitness.sdk.domain.model

/**
 * A snapshot of a workout template at a specific point in time.
 * Used for version control - historical workouts store the template snapshot
 * so modifications to the template don't affect past records.
 *
 * @property templateId Original template ID
 * @property templateVersion Version of the template when snapshot was taken
 * @property name Template name at time of snapshot
 * @property exercises Snapshot of exercises and their sets
 * @property snapshotTime Time when the snapshot was created
 */
data class TemplateSnapshot(
    val templateId: Long,
    val templateVersion: Int,
    val name: String,
    val exercises: List<ExerciseSnapshot>,
    val snapshotTime: Long = System.currentTimeMillis()
) {
    companion object {
        /**
         * Create a snapshot from a WorkoutTemplate.
         */
        fun fromTemplate(template: WorkoutTemplate): TemplateSnapshot {
            return TemplateSnapshot(
                templateId = template.id,
                templateVersion = template.version,
                name = template.name,
                exercises = template.exercises.map { exercise ->
                    ExerciseSnapshot(
                        exerciseName = exercise.exerciseName,
                        orderIndex = exercise.orderIndex,
                        sets = exercise.sets.map { set ->
                            SetSnapshot(
                                setNumber = set.setNumber,
                                targetReps = set.targetReps,
                                targetWeight = set.targetWeight,
                                targetRpe = set.targetRpe,
                                isWarmupSet = set.isWarmupSet
                            )
                        },
                        restSeconds = exercise.restSeconds
                    )
                },
                snapshotTime = System.currentTimeMillis()
            )
        }
    }
}

/**
 * Snapshot of an exercise within a template snapshot.
 */
data class ExerciseSnapshot(
    val exerciseName: String,
    val orderIndex: Int,
    val sets: List<SetSnapshot>,
    val restSeconds: Int
)

/**
 * Snapshot of a set within an exercise snapshot.
 */
data class SetSnapshot(
    val setNumber: Int,
    val targetReps: Int?,
    val targetWeight: Float?,
    val targetRpe: Float?,
    val isWarmupSet: Boolean
)
