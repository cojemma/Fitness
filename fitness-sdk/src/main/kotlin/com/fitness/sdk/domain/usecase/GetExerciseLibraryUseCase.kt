package com.fitness.sdk.domain.usecase

import com.fitness.sdk.data.library.ExerciseLibraryProvider
import com.fitness.sdk.domain.model.ExerciseCategory
import com.fitness.sdk.domain.model.ExerciseDefinition
import com.fitness.sdk.domain.model.MuscleGroup

/**
 * Use case for retrieving exercises from the exercise library.
 */
class GetExerciseLibraryUseCase(
    private val libraryProvider: ExerciseLibraryProvider
) {
    /**
     * Get all exercises in the library.
     */
    fun getAllExercises(): List<ExerciseDefinition> = libraryProvider.getAllExercises()

    /**
     * Get an exercise by its ID.
     */
    fun getExerciseById(id: String): ExerciseDefinition? = libraryProvider.getExerciseById(id)

    /**
     * Get exercises filtered by category.
     */
    fun getExercisesByCategory(category: ExerciseCategory): List<ExerciseDefinition> =
        libraryProvider.getExercisesByCategory(category)

    /**
     * Get exercises filtered by muscle group (primary or secondary).
     */
    fun getExercisesByMuscleGroup(muscleGroup: MuscleGroup): List<ExerciseDefinition> =
        libraryProvider.getExercisesByMuscleGroup(muscleGroup)
}
