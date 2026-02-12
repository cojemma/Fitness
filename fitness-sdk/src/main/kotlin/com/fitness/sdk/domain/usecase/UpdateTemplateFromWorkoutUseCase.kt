package com.fitness.sdk.domain.usecase

import com.fitness.sdk.domain.model.TemplateExercise
import com.fitness.sdk.domain.model.TemplateSet
import com.fitness.sdk.domain.repository.TemplateRepository
import com.fitness.sdk.domain.repository.WorkoutRepository

/**
 * Use case for updating an existing template from a completed workout.
 * This overwrites the template's exercises with the workout's actual performance.
 */
class UpdateTemplateFromWorkoutUseCase(
    private val workoutRepository: WorkoutRepository,
    private val templateRepository: TemplateRepository
) {
    /**
     * Update an existing template using data from a completed workout.
     *
     * @param templateId The ID of the template to update
     * @param workoutId The ID of the workout to use as source
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(
        templateId: Long,
        workoutId: Long
    ): Result<Unit> {
        if (templateId <= 0) {
            return Result.failure(IllegalArgumentException("Invalid template ID: $templateId"))
        }

        return try {
            // Fetch completed workout from DB
            val workout = workoutRepository.getWorkoutById(workoutId)
                ?: return Result.failure(NoSuchElementException("Workout not found: $workoutId"))

            updateFromWorkout(templateId, workout)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Update an existing template using an in-memory workout object.
     * Faster than the workoutId overload — avoids re-fetching workout data from DB.
     *
     * @param templateId The ID of the template to update
     * @param workout The workout object already in memory
     * @return Result indicating success or failure
     */
    suspend fun updateFromWorkout(
        templateId: Long,
        workout: com.fitness.sdk.domain.model.Workout
    ): Result<Unit> {
        if (templateId <= 0) {
            return Result.failure(IllegalArgumentException("Invalid template ID: $templateId"))
        }

        return try {
            // Fetch only the template metadata (needed for name, description, etc.)
            val originalTemplateResult = templateRepository.getTemplateById(templateId)
            val originalTemplate = originalTemplateResult.getOrNull()
                ?: return Result.failure(NoSuchElementException("Template not found: $templateId"))

            // Convert workout exercises to template exercises
            val newExercises = buildTemplateExercises(templateId, workout)

            // Create updated template object
            val updatedTemplate = originalTemplate.copy(
                exercises = newExercises,
                updatedAt = System.currentTimeMillis()
            )

            // Save (Replace)
            templateRepository.saveTemplate(updatedTemplate).map { }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Converts workout exercises to template exercises.
     */
    private fun buildTemplateExercises(
        templateId: Long,
        workout: com.fitness.sdk.domain.model.Workout
    ): List<TemplateExercise> {
        return workout.exercises.mapIndexed { index, exercise ->
            val templateSets = if (exercise.setRecords.isNotEmpty()) {
                exercise.setRecords.map { record ->
                    TemplateSet(
                        id = 0,
                        templateExerciseId = 0,
                        setNumber = record.setNumber,
                        targetReps = record.reps,
                        targetWeight = record.weight,
                        isWarmupSet = record.isWarmupSet
                    )
                }
            } else {
                // Fallback to aggregate data
                (1..exercise.sets).map { setNumber ->
                    TemplateSet(
                        id = 0,
                        templateExerciseId = 0,
                        setNumber = setNumber,
                        targetReps = exercise.reps,
                        targetWeight = exercise.weight,
                        isWarmupSet = false
                    )
                }
            }

            TemplateExercise(
                id = 0,
                templateId = templateId,
                exerciseName = exercise.name,
                orderIndex = index,
                sets = templateSets.ifEmpty {
                    listOf(TemplateSet(
                        id = 0,
                        templateExerciseId = 0,
                        setNumber = 1,
                        targetReps = exercise.reps,
                        targetWeight = exercise.weight,
                        isWarmupSet = false
                    ))
                },
                restSeconds = exercise.restSeconds,
                notes = exercise.notes
            )
        }
    }
}
