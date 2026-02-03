package com.fitness.sdk.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.fitness.sdk.data.local.entity.WorkoutEntity
import com.fitness.sdk.data.local.entity.WorkoutWithExercises
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for workout operations.
 */
@Dao
interface WorkoutDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutEntity): Long

    @Update
    suspend fun updateWorkout(workout: WorkoutEntity)

    @Query("DELETE FROM workouts WHERE id = :id")
    suspend fun deleteWorkout(id: Long)

    @Transaction
    @Query("SELECT * FROM workouts ORDER BY startTime DESC")
    suspend fun getAllWorkouts(): List<WorkoutWithExercises>

    @Transaction
    @Query("SELECT * FROM workouts WHERE id = :id")
    suspend fun getWorkoutById(id: Long): WorkoutWithExercises?

    @Transaction
    @Query("SELECT * FROM workouts WHERE startTime >= :startTime AND startTime <= :endTime ORDER BY startTime DESC")
    suspend fun getWorkoutsByDateRange(startTime: Long, endTime: Long): List<WorkoutWithExercises>

    @Transaction
    @Query("SELECT * FROM workouts ORDER BY startTime DESC")
    fun observeAllWorkouts(): Flow<List<WorkoutWithExercises>>

    @Transaction
    @Query("SELECT * FROM workouts WHERE id = :id")
    fun observeWorkoutById(id: Long): Flow<WorkoutWithExercises?>
}
