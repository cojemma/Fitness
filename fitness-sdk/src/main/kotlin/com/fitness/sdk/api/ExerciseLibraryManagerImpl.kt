package com.fitness.sdk.api

import com.fitness.sdk.data.library.CompositeExerciseLibraryProvider
import com.fitness.sdk.domain.model.ExerciseCategory
import com.fitness.sdk.domain.model.ExerciseDefinition
import com.fitness.sdk.domain.model.MuscleGroup
import com.fitness.sdk.domain.usecase.DeleteCustomExerciseUseCase
import com.fitness.sdk.domain.usecase.GetExerciseLibraryUseCase
import com.fitness.sdk.domain.usecase.SaveCustomExerciseUseCase
import com.fitness.sdk.domain.usecase.SearchExercisesUseCase
import kotlinx.coroutines.flow.Flow

/**
 * Implementation of [ExerciseLibraryManager] using use cases.
 */
internal class ExerciseLibraryManagerImpl(
    private val getExerciseLibraryUseCase: GetExerciseLibraryUseCase,
    private val searchExercisesUseCase: SearchExercisesUseCase,
    private val saveCustomExerciseUseCase: SaveCustomExerciseUseCase,
    private val deleteCustomExerciseUseCase: DeleteCustomExerciseUseCase,
    private val compositeProvider: CompositeExerciseLibraryProvider
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

    override suspend fun saveCustomExercise(exercise: ExerciseDefinition): Result<Unit> =
        saveCustomExerciseUseCase(exercise)

    override suspend fun deleteCustomExercise(id: String): Result<Unit> =
        deleteCustomExerciseUseCase(id)

    override fun observeAllExercises(): Flow<List<ExerciseDefinition>> =
        compositeProvider.observeAllExercises()
}
