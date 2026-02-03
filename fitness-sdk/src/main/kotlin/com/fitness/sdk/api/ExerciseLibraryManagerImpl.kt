package com.fitness.sdk.api

import com.fitness.sdk.domain.model.ExerciseCategory
import com.fitness.sdk.domain.model.ExerciseDefinition
import com.fitness.sdk.domain.model.MuscleGroup
import com.fitness.sdk.domain.usecase.GetExerciseLibraryUseCase
import com.fitness.sdk.domain.usecase.SearchExercisesUseCase

/**
 * Implementation of [ExerciseLibraryManager] using use cases.
 */
internal class ExerciseLibraryManagerImpl(
    private val getExerciseLibraryUseCase: GetExerciseLibraryUseCase,
    private val searchExercisesUseCase: SearchExercisesUseCase
) : ExerciseLibraryManager {

    override fun getAllExercises(): List<ExerciseDefinition> =
        getExerciseLibraryUseCase.getAllExercises()

    override fun getExerciseById(id: String): ExerciseDefinition? =
        getExerciseLibraryUseCase.getExerciseById(id)

    override fun getExercisesByCategory(category: ExerciseCategory): List<ExerciseDefinition> =
        getExerciseLibraryUseCase.getExercisesByCategory(category)

    override fun getExercisesByMuscleGroup(muscleGroup: MuscleGroup): List<ExerciseDefinition> =
        getExerciseLibraryUseCase.getExercisesByMuscleGroup(muscleGroup)

    override fun searchExercises(query: String): List<ExerciseDefinition> =
        searchExercisesUseCase(query)
}
