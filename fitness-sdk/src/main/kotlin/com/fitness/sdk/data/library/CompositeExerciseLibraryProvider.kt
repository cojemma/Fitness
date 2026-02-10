package com.fitness.sdk.data.library

import com.fitness.sdk.data.local.dao.CustomExerciseDao
import com.fitness.sdk.data.mapper.CustomExerciseMapper
import com.fitness.sdk.domain.model.ExerciseCategory
import com.fitness.sdk.domain.model.ExerciseDefinition
import com.fitness.sdk.domain.model.MuscleGroup
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Composite exercise library provider that merges predefined exercises
 * from [DefaultExerciseLibrary] with user-created custom exercises from the database.
 *
 * Synchronous methods use a cached snapshot of custom exercises.
 * Call [refreshCustomExercises] to update the cache.
 */
class CompositeExerciseLibraryProvider(
    private val defaultLibrary: DefaultExerciseLibrary,
    private val customExerciseDao: CustomExerciseDao
) : ExerciseLibraryProvider {

    @Volatile
    private var cachedCustomExercises: List<ExerciseDefinition> = emptyList()

    /**
     * Refresh the in-memory cache of custom exercises from the database.
     * Should be called during SDK initialization and whenever custom exercises change.
     */
    suspend fun refreshCustomExercises() {
        cachedCustomExercises = CustomExerciseMapper.toDomainList(customExerciseDao.getAll())
    }

    override fun getAllExercises(): List<ExerciseDefinition> {
        return defaultLibrary.getAllExercises() + cachedCustomExercises
    }

    override fun getExerciseById(id: String): ExerciseDefinition? {
        return defaultLibrary.getExerciseById(id)
            ?: cachedCustomExercises.find { it.id == id }
    }

    override fun getExercisesByCategory(category: ExerciseCategory): List<ExerciseDefinition> {
        return defaultLibrary.getExercisesByCategory(category) +
            cachedCustomExercises.filter { it.category == category }
    }

    override fun getExercisesByMuscleGroup(muscleGroup: MuscleGroup): List<ExerciseDefinition> {
        return defaultLibrary.getExercisesByMuscleGroup(muscleGroup) +
            cachedCustomExercises.filter {
                it.primaryMuscle == muscleGroup || muscleGroup in it.secondaryMuscles
            }
    }

    override fun searchExercises(query: String): List<ExerciseDefinition> {
        if (query.isBlank()) return getAllExercises()
        val lowerQuery = query.lowercase()
        return getAllExercises().filter { it.name.lowercase().contains(lowerQuery) }
    }

    /**
     * Observe all exercises (predefined + custom) as a reactive Flow.
     * Emits a new list whenever custom exercises in the database change.
     */
    fun observeAllExercises(): Flow<List<ExerciseDefinition>> {
        return customExerciseDao.observeAll().map { entities ->
            val customExercises = CustomExerciseMapper.toDomainList(entities)
            cachedCustomExercises = customExercises
            defaultLibrary.getAllExercises() + customExercises
        }
    }
}
