package com.fitness.sdk.domain.usecase

import com.fitness.sdk.data.library.ExerciseLibraryProvider
import com.fitness.sdk.domain.model.ExerciseDefinition
import com.fitness.sdk.domain.repository.CustomExerciseRepository

/**
 * Use case for saving a custom exercise with validation.
 * Validates name uniqueness against both predefined and existing custom exercises.
 */
class SaveCustomExerciseUseCase(
    private val customExerciseRepository: CustomExerciseRepository,
    private val libraryProvider: ExerciseLibraryProvider
) {

    suspend operator fun invoke(exercise: ExerciseDefinition): Result<Unit> {
        return try {
            // Validate name not blank
            if (exercise.name.isBlank()) {
                return Result.failure(IllegalArgumentException("Exercise name cannot be blank"))
            }

            // Check uniqueness against predefined exercises
            val predefinedMatch = libraryProvider.searchExercises(exercise.name)
                .any { it.name.equals(exercise.name, ignoreCase = true) }
            if (predefinedMatch) {
                return Result.failure(IllegalArgumentException("An exercise with this name already exists in the library"))
            }

            // Check uniqueness against existing custom exercises
            val existingCustom = customExerciseRepository.getCustomExerciseByName(exercise.name)
            if (existingCustom != null) {
                return Result.failure(IllegalArgumentException("A custom exercise with this name already exists"))
            }

            customExerciseRepository.saveCustomExercise(exercise.copy(isCustom = true))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
