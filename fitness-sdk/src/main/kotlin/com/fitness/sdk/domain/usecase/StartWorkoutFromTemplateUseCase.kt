package com.fitness.sdk.domain.usecase

import com.fitness.sdk.domain.model.Exercise
import com.fitness.sdk.domain.model.Workout
import com.fitness.sdk.domain.model.WorkoutType
import com.fitness.sdk.domain.repository.TemplateRepository

/**
 * Use case for starting a workout from a template.
 * Creates a new Workout object pre-populated with the template's exercises.
 */
class StartWorkoutFromTemplateUseCase(
    private val templateRepository: TemplateRepository,
    private val getLastSessionDataUseCase: GetLastSessionDataUseCase
) {
    /**
     * Create a new workout from a template.
     * The workout will have exercises pre-populated from the template,
     * with weights and reps from the last session if available.
     *
     * @param templateId The ID of the template to start from
     * @param preloadLastSession Whether to preload data from the last session
     * @return Result containing a new Workout object
     */
    suspend operator fun invoke(
        templateId: Long,
        preloadLastSession: Boolean = true
    ): Result<Workout> {
        if (templateId <= 0) {
            return Result.failure(IllegalArgumentException("Invalid template ID: $templateId"))
        }

        return try {
            // Get the template
            val templateResult = templateRepository.getTemplateById(templateId)
            val template = templateResult.getOrNull()
                ?: return Result.failure(NoSuchElementException("Template not found: $templateId"))

            // Get last session data if requested
            val lastSessionData = if (preloadLastSession) {
                getLastSessionDataUseCase(templateId).getOrNull()
            } else {
                null
            }

            // Convert template exercises to workout exercises
            val exercises = template.exercises.mapIndexed { index, templateExercise ->
                // Get last set data for this exercise
                val lastExerciseData = lastSessionData?.getExerciseData(templateExercise.exerciseName)
                val lastSetData = lastExerciseData?.firstOrNull()

                // Determine the values to use
                val targetSet = templateExercise.sets.firstOrNull()
                val targetReps = targetSet?.targetReps ?: 10
                val targetWeight = lastSetData?.actualWeight 
                    ?: targetSet?.targetWeight

                Exercise(
                    id = 0,
                    workoutId = 0,
                    name = templateExercise.exerciseName,
                    sets = templateExercise.getWorkingSets(),
                    reps = targetReps,
                    weight = targetWeight,
                    restSeconds = templateExercise.restSeconds,
                    notes = templateExercise.notes
                )
            }

            // Create the workout
            val workout = Workout(
                id = 0,
                name = template.name,
                type = WorkoutType.STRENGTH,
                templateId = templateId,
                startTime = System.currentTimeMillis(),
                exercises = exercises,
                notes = "From template: ${template.name}"
            )

            Result.success(workout)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
