package com.fitness.sdk.domain.usecase

import com.fitness.sdk.domain.model.Workout
import com.fitness.sdk.domain.repository.WorkoutRepository

/**
 * Use case for saving a new workout.
 * Validates the workout data before saving.
 */
class SaveWorkoutUseCase(
    private val repository: WorkoutRepository
) {
    /**
     * Execute the use case to save a workout.
     *
     * @param workout The workout to save
     * @return Result containing the new workout ID on success, or exception on failure
     */
    suspend operator fun invoke(workout: Workout): Result<Long> {
        return runCatching {
            // Validate workout data
            require(workout.name.isNotBlank()) { "Workout name cannot be empty" }
            require(workout.startTime > 0) { "Start time must be valid" }
            require(workout.durationMinutes >= 0) { "Duration cannot be negative" }
            require(workout.caloriesBurned >= 0) { "Calories burned cannot be negative" }

            // Validate exercises if present
            workout.exercises.forEach { exercise ->
                require(exercise.name.isNotBlank()) { "Exercise name cannot be empty" }
                require(exercise.sets >= 0) { "Sets cannot be negative" }
                require(exercise.reps >= 0) { "Reps cannot be negative" }
            }

            repository.saveWorkout(workout)
        }
    }
}
