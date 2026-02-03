package com.fitness.sdk.domain.usecase

import com.fitness.sdk.domain.model.Workout
import com.fitness.sdk.domain.repository.WorkoutRepository

/**
 * Use case for updating an existing workout.
 */
class UpdateWorkoutUseCase(
    private val repository: WorkoutRepository
) {
    /**
     * Update an existing workout.
     *
     * @param workout The workout with updated values
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(workout: Workout): Result<Unit> {
        return runCatching {
            // Validate workout data
            require(workout.id > 0) { "Workout ID must be valid for update" }
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

            // Update with current timestamp
            val updatedWorkout = workout.copy(updatedAt = System.currentTimeMillis())
            repository.updateWorkout(updatedWorkout)
        }
    }
}
