package com.fitness.sdk.data.repository

import com.fitness.sdk.data.local.dao.CustomExerciseDao
import com.fitness.sdk.data.mapper.CustomExerciseMapper
import com.fitness.sdk.domain.model.ExerciseDefinition
import com.fitness.sdk.domain.repository.CustomExerciseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Implementation of CustomExerciseRepository using Room database.
 */
class CustomExerciseRepositoryImpl(
    private val customExerciseDao: CustomExerciseDao
) : CustomExerciseRepository {

    override suspend fun saveCustomExercise(exercise: ExerciseDefinition) = withContext(Dispatchers.IO) {
        val entity = CustomExerciseMapper.toEntity(exercise)
        customExerciseDao.insert(entity)
    }

    override suspend fun deleteCustomExercise(id: String) = withContext(Dispatchers.IO) {
        customExerciseDao.deleteById(id)
    }

    override suspend fun getAllCustomExercises(): List<ExerciseDefinition> = withContext(Dispatchers.IO) {
        CustomExerciseMapper.toDomainList(customExerciseDao.getAll())
    }

    override suspend fun getCustomExerciseByName(name: String): ExerciseDefinition? = withContext(Dispatchers.IO) {
        customExerciseDao.getByName(name)?.let { CustomExerciseMapper.toDomain(it) }
    }

    override fun observeAllCustomExercises(): Flow<List<ExerciseDefinition>> {
        return customExerciseDao.observeAll().map { entities ->
            CustomExerciseMapper.toDomainList(entities)
        }
    }
}
