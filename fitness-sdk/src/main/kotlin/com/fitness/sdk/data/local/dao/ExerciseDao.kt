package com.fitness.sdk.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fitness.sdk.data.local.entity.ExerciseEntity
import com.fitness.sdk.data.local.entity.ExerciseHistoryRecord
import com.fitness.sdk.data.local.entity.ExerciseSetEntity

/**
 * Data Access Object for exercise operations.
 */
@Dao
interface ExerciseDao {

    @Insert
    suspend fun insertExercise(exercise: ExerciseEntity): Long

    @Insert
    suspend fun insertExercises(exercises: List<ExerciseEntity>): List<Long>

    @Query("DELETE FROM exercises WHERE workoutId = :workoutId")
    suspend fun deleteExercisesByWorkoutId(workoutId: Long)

    @Query("SELECT * FROM exercises WHERE workoutId = :workoutId")
    suspend fun getExercisesByWorkoutId(workoutId: Long): List<ExerciseEntity>

    // ExerciseSet operations

    @Insert
    suspend fun insertExerciseSet(exerciseSet: ExerciseSetEntity): Long

    @Insert
    suspend fun insertExerciseSets(exerciseSets: List<ExerciseSetEntity>)

    @Query("DELETE FROM exercise_sets WHERE exerciseId = :exerciseId")
    suspend fun deleteExerciseSetsByExerciseId(exerciseId: Long)

    @Query("SELECT * FROM exercise_sets WHERE exerciseId = :exerciseId ORDER BY setNumber")
    suspend fun getExerciseSetsByExerciseId(exerciseId: Long): List<ExerciseSetEntity>

    @Query("SELECT * FROM exercise_sets WHERE exerciseId IN (:exerciseIds) ORDER BY exerciseId, setNumber")
    suspend fun getExerciseSetsByExerciseIds(exerciseIds: List<Long>): List<ExerciseSetEntity>

    /**
     * Get all set records for exercises with the given name, joined with workout dates.
     * Used for exercise history and stats (total sessions, max weight, 1RM).
     */
    @Query("""
        SELECT w.id AS workoutId, w.startTime AS workoutDate, es.weight AS weight,
               es.reps AS reps, es.completedAt AS completedAt, es.isWarmupSet AS isWarmupSet
        FROM exercise_sets es
        INNER JOIN exercises e ON es.exerciseId = e.id
        INNER JOIN workouts w ON e.workoutId = w.id
        WHERE e.name = :exerciseName
        ORDER BY w.startTime DESC, es.setNumber
    """)
    suspend fun getExerciseHistoryByName(exerciseName: String): List<ExerciseHistoryRecord>
}

