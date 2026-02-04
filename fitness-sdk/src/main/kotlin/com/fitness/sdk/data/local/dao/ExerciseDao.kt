package com.fitness.sdk.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fitness.sdk.data.local.entity.ExerciseEntity
import com.fitness.sdk.data.local.entity.ExerciseSetEntity

/**
 * Data Access Object for exercise operations.
 */
@Dao
interface ExerciseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: ExerciseEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<ExerciseEntity>): List<Long>

    @Query("DELETE FROM exercises WHERE workoutId = :workoutId")
    suspend fun deleteExercisesByWorkoutId(workoutId: Long)

    @Query("SELECT * FROM exercises WHERE workoutId = :workoutId")
    suspend fun getExercisesByWorkoutId(workoutId: Long): List<ExerciseEntity>

    // ExerciseSet operations

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExerciseSet(exerciseSet: ExerciseSetEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExerciseSets(exerciseSets: List<ExerciseSetEntity>)

    @Query("DELETE FROM exercise_sets WHERE exerciseId = :exerciseId")
    suspend fun deleteExerciseSetsByExerciseId(exerciseId: Long)

    @Query("SELECT * FROM exercise_sets WHERE exerciseId = :exerciseId ORDER BY setNumber")
    suspend fun getExerciseSetsByExerciseId(exerciseId: Long): List<ExerciseSetEntity>

    @Query("SELECT * FROM exercise_sets WHERE exerciseId IN (:exerciseIds) ORDER BY exerciseId, setNumber")
    suspend fun getExerciseSetsByExerciseIds(exerciseIds: List<Long>): List<ExerciseSetEntity>
}

