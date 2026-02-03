package com.fitness.sdk.data.repository

import com.fitness.sdk.data.local.dao.ExerciseDao
import com.fitness.sdk.data.local.dao.WorkoutDao
import com.fitness.sdk.data.mapper.ExerciseMapper
import com.fitness.sdk.data.mapper.WorkoutMapper
import com.fitness.sdk.domain.model.Workout
import com.fitness.sdk.domain.repository.WorkoutRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Implementation of WorkoutRepository using Room database.
 */
class WorkoutRepositoryImpl(
    private val workoutDao: WorkoutDao,
    private val exerciseDao: ExerciseDao
) : WorkoutRepository {

    override suspend fun saveWorkout(workout: Workout): Long = withContext(Dispatchers.IO) {
        // Insert workout and get the generated ID
        val workoutEntity = WorkoutMapper.toEntity(workout)
        val workoutId = workoutDao.insertWorkout(workoutEntity)

        // Insert exercises with the workout ID
        if (workout.exercises.isNotEmpty()) {
            val exerciseEntities = ExerciseMapper.toEntityList(workout.exercises, workoutId)
            exerciseDao.insertExercises(exerciseEntities)
        }

        workoutId
    }

    override suspend fun getWorkouts(): List<Workout> = withContext(Dispatchers.IO) {
        val workoutsWithExercises = workoutDao.getAllWorkouts()
        WorkoutMapper.toDomainList(workoutsWithExercises)
    }

    override suspend fun getWorkoutById(id: Long): Workout? = withContext(Dispatchers.IO) {
        val workoutWithExercises = workoutDao.getWorkoutById(id)
        workoutWithExercises?.let { WorkoutMapper.toDomain(it) }
    }

    override suspend fun getWorkoutsByDateRange(startTime: Long, endTime: Long): List<Workout> =
        withContext(Dispatchers.IO) {
            val workoutsWithExercises = workoutDao.getWorkoutsByDateRange(startTime, endTime)
            WorkoutMapper.toDomainList(workoutsWithExercises)
        }

    override suspend fun updateWorkout(workout: Workout) = withContext(Dispatchers.IO) {
        // Update the workout
        val workoutEntity = WorkoutMapper.toEntity(workout)
        workoutDao.updateWorkout(workoutEntity)

        // Delete existing exercises and insert updated ones
        exerciseDao.deleteExercisesByWorkoutId(workout.id)
        if (workout.exercises.isNotEmpty()) {
            val exerciseEntities = ExerciseMapper.toEntityList(workout.exercises, workout.id)
            exerciseDao.insertExercises(exerciseEntities)
        }
    }

    override suspend fun deleteWorkout(id: Long) = withContext(Dispatchers.IO) {
        // Exercises are deleted automatically via CASCADE
        workoutDao.deleteWorkout(id)
    }

    override fun observeWorkouts(): Flow<List<Workout>> {
        return workoutDao.observeAllWorkouts().map { workoutsWithExercises ->
            WorkoutMapper.toDomainList(workoutsWithExercises)
        }
    }

    override fun observeWorkoutById(id: Long): Flow<Workout?> {
        return workoutDao.observeWorkoutById(id).map { workoutWithExercises ->
            workoutWithExercises?.let { WorkoutMapper.toDomain(it) }
        }
    }
}
