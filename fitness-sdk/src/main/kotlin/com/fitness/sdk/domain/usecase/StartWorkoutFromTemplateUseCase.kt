package com.fitness.sdk.domain.usecase

import com.fitness.sdk.domain.model.Exercise
import com.fitness.sdk.domain.model.ExerciseSet
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
                // Get last session set data for this exercise (list of per-set records)
                val lastExerciseData = lastSessionData?.getExerciseData(templateExercise.exerciseName)

                // Build working sets only (exclude warm-up)
                val workingSets = templateExercise.sets.filter { !it.isWarmupSet }

                // Convert each TemplateSet into an ExerciseSet record.
                // Priority: template per-set target > last session data > default
                val setRecords = workingSets.mapIndexed { setIdx, templateSet ->
                    val lastSetForIndex = lastExerciseData?.getOrNull(setIdx)
                    ExerciseSet(
                        setNumber = setIdx + 1,
                        reps = templateSet.targetReps
                            ?: lastSetForIndex?.actualReps ?: 10,
                        weight = templateSet.targetWeight
                            ?: lastSetForIndex?.actualWeight
                    )
                }

                // Fallback aggregate values use the first set
                val firstRecord = setRecords.firstOrNull()

                Exercise(
                    id = 0,
                    workoutId = 0,
                    name = templateExercise.exerciseName,
                    sets = workingSets.size,
                    reps = firstRecord?.reps ?: 10,
                    weight = firstRecord?.weight,
                    restSeconds = templateExercise.restSeconds,
                    notes = templateExercise.notes,
                    setRecords = setRecords,
                    supersetGroupId = templateExercise.supersetGroupId
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
