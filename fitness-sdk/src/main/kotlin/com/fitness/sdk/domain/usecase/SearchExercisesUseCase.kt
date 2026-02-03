package com.fitness.sdk.domain.usecase

import com.fitness.sdk.data.library.ExerciseLibraryProvider
import com.fitness.sdk.domain.model.ExerciseDefinition

/**
 * Use case for searching exercises in the library.
 */
class SearchExercisesUseCase(
    private val libraryProvider: ExerciseLibraryProvider
) {
    /**
     * Search exercises by name.
     * @param query Search query (case-insensitive partial match)
     * @return List of matching exercises
     */
    operator fun invoke(query: String): List<ExerciseDefinition> =
        libraryProvider.searchExercises(query)
}
