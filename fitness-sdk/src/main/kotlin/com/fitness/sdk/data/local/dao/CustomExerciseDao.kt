package com.fitness.sdk.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.fitness.sdk.data.local.entity.CustomExerciseDefinitionEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for custom exercise definition operations.
 */
@Dao
interface CustomExerciseDao {

    @Insert
    suspend fun insert(entity: CustomExerciseDefinitionEntity)

    @Query("DELETE FROM custom_exercise_definitions WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM custom_exercise_definitions WHERE id = :id")
    suspend fun getById(id: String): CustomExerciseDefinitionEntity?

    @Query("SELECT * FROM custom_exercise_definitions WHERE name = :name LIMIT 1")
    suspend fun getByName(name: String): CustomExerciseDefinitionEntity?

    @Query("SELECT * FROM custom_exercise_definitions ORDER BY name ASC")
    suspend fun getAll(): List<CustomExerciseDefinitionEntity>

    @Query("SELECT * FROM custom_exercise_definitions ORDER BY name ASC")
    fun observeAll(): Flow<List<CustomExerciseDefinitionEntity>>
}
