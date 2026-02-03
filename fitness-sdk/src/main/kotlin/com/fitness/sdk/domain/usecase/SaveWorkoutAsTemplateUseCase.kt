package com.fitness.sdk.domain.usecase

import com.fitness.sdk.domain.model.TemplateExercise
import com.fitness.sdk.domain.model.TemplateSet
import com.fitness.sdk.domain.model.WorkoutTemplate
import com.fitness.sdk.domain.repository.TemplateRepository
import com.fitness.sdk.domain.repository.WorkoutRepository

/**
 * Use case for saving a workout as a new template.
 * This allows users to capture a workout they liked and reuse it.
 */
class SaveWorkoutAsTemplateUseCase(
    private val workoutRepository: WorkoutRepository,
    private val templateRepository: TemplateRepository
) {
    /**
     * Save a workout as a new template.
     *
     * @param workoutId The ID of the workout to save as template
     * @param templateName The name for the new template
     * @param description Optional description for the template
     * @return Result containing the new template ID on success
     */
    suspend operator fun invoke(
        workoutId: Long,
        templateName: String,
        description: String? = null
    ): Result<Long> {
        if (workoutId <= 0) {
            return Result.failure(IllegalArgumentException("Invalid workout ID: $workoutId"))
        }

        if (templateName.isBlank()) {
            return Result.failure(IllegalArgumentException("Template name cannot be empty"))
        }

        return try {
            // Get the workout
            val workout = workoutRepository.getWorkoutById(workoutId)
                ?: return Result.failure(NoSuchElementException("Workout not found: $workoutId"))

            // Convert workout exercises to template exercises
            val templateExercises = workout.exercises.mapIndexed { index, exercise ->
                // Create template sets from the exercise
                val templateSets = (1..exercise.sets).map { setNumber ->
                    TemplateSet(
                        id = 0,
                        templateExerciseId = 0,
                        setNumber = setNumber,
                        targetReps = exercise.reps,
                        targetWeight = exercise.weight,
                        isWarmupSet = false
                    )
                }

                TemplateExercise(
                    id = 0,
                    templateId = 0,
                    exerciseName = exercise.name,
                    orderIndex = index,
                    sets = templateSets,
                    restSeconds = exercise.restSeconds,
                    notes = exercise.notes
                )
            }

            // Create the template
            val template = WorkoutTemplate(
                id = 0,
                name = templateName,
                description = description ?: "Created from workout: ${workout.name}",
                estimatedDurationMinutes = workout.durationMinutes,
                exercises = templateExercises,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )

            // Save the template
            templateRepository.saveTemplate(template)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
