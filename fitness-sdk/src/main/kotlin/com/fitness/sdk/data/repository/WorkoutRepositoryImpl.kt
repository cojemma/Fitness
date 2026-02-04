package com.fitness.sdk.data.repository

import com.fitness.sdk.data.local.dao.ExerciseDao
import com.fitness.sdk.data.local.dao.WorkoutDao
import com.fitness.sdk.data.mapper.ExerciseMapper
import com.fitness.sdk.data.mapper.ExerciseSetMapper
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
            val exerciseIds = exerciseDao.insertExercises(exerciseEntities)

            // Insert set records for each exercise
            workout.exercises.forEachIndexed { index, exercise ->
                if (exercise.setRecords.isNotEmpty()) {
                    val exerciseId = exerciseIds[index]
                    val setEntities = ExerciseSetMapper.toEntityList(exercise.setRecords, exerciseId)
                    exerciseDao.insertExerciseSets(setEntities)
                }
            }
        }

        workoutId
    }

    override suspend fun getWorkouts(): List<Workout> = withContext(Dispatchers.IO) {
        val workoutsWithExercises = workoutDao.getAllWorkouts()
        workoutsWithExercises.map { workoutWithExercises ->
            val exerciseIds = workoutWithExercises.exercises.map { it.id }
            val allSetRecords = if (exerciseIds.isNotEmpty()) {
                exerciseDao.getExerciseSetsByExerciseIds(exerciseIds)
            } else {
                emptyList()
            }
            val setsByExercise = allSetRecords.groupBy { it.exerciseId }

            val exercises = workoutWithExercises.exercises.map { entity ->
                val setRecords = setsByExercise[entity.id]?.let { 
                    ExerciseSetMapper.toDomainList(it) 
                } ?: emptyList()
                ExerciseMapper.toDomain(entity, setRecords)
            }

            WorkoutMapper.toDomain(workoutWithExercises.workout, exercises)
        }
    }

    override suspend fun getWorkoutById(id: Long): Workout? = withContext(Dispatchers.IO) {
        val workoutWithExercises = workoutDao.getWorkoutById(id) ?: return@withContext null
        
        val exerciseIds = workoutWithExercises.exercises.map { it.id }
        val allSetRecords = if (exerciseIds.isNotEmpty()) {
            exerciseDao.getExerciseSetsByExerciseIds(exerciseIds)
        } else {
            emptyList()
        }
        val setsByExercise = allSetRecords.groupBy { it.exerciseId }

        val exercises = workoutWithExercises.exercises.map { entity ->
            val setRecords = setsByExercise[entity.id]?.let { 
                ExerciseSetMapper.toDomainList(it) 
            } ?: emptyList()
            ExerciseMapper.toDomain(entity, setRecords)
        }

        WorkoutMapper.toDomain(workoutWithExercises.workout, exercises)
    }

    override suspend fun getWorkoutsByDateRange(startTime: Long, endTime: Long): List<Workout> =
        withContext(Dispatchers.IO) {
            val workoutsWithExercises = workoutDao.getWorkoutsByDateRange(startTime, endTime)
            workoutsWithExercises.map { workoutWithExercises ->
                val exerciseIds = workoutWithExercises.exercises.map { it.id }
                val allSetRecords = if (exerciseIds.isNotEmpty()) {
                    exerciseDao.getExerciseSetsByExerciseIds(exerciseIds)
                } else {
                    emptyList()
                }
                val setsByExercise = allSetRecords.groupBy { it.exerciseId }

                val exercises = workoutWithExercises.exercises.map { entity ->
                    val setRecords = setsByExercise[entity.id]?.let { 
                        ExerciseSetMapper.toDomainList(it) 
                    } ?: emptyList()
                    ExerciseMapper.toDomain(entity, setRecords)
                }

                WorkoutMapper.toDomain(workoutWithExercises.workout, exercises)
            }
        }

    override suspend fun updateWorkout(workout: Workout) = withContext(Dispatchers.IO) {
        // Update the workout
        val workoutEntity = WorkoutMapper.toEntity(workout)
        workoutDao.updateWorkout(workoutEntity)

        // Delete existing exercises (set records will cascade delete)
        exerciseDao.deleteExercisesByWorkoutId(workout.id)
        
        // Insert updated exercises with set records
        if (workout.exercises.isNotEmpty()) {
            val exerciseEntities = ExerciseMapper.toEntityList(workout.exercises, workout.id)
            val exerciseIds = exerciseDao.insertExercises(exerciseEntities)

            // Insert set records for each exercise
            workout.exercises.forEachIndexed { index, exercise ->
                if (exercise.setRecords.isNotEmpty()) {
                    val exerciseId = exerciseIds[index]
                    val setEntities = ExerciseSetMapper.toEntityList(exercise.setRecords, exerciseId)
                    exerciseDao.insertExerciseSets(setEntities)
                }
            }
        }
    }

    override suspend fun deleteWorkout(id: Long) = withContext(Dispatchers.IO) {
        // Exercises and set records are deleted automatically via CASCADE
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

