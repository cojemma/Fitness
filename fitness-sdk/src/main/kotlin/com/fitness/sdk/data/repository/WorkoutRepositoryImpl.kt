package com.fitness.sdk.data.repository

import com.fitness.sdk.data.local.dao.ExerciseDao
import com.fitness.sdk.data.local.dao.WorkoutDao
import com.fitness.sdk.data.mapper.ExerciseMapper
import com.fitness.sdk.data.mapper.ExerciseSetMapper
import com.fitness.sdk.data.mapper.WorkoutMapper
import com.fitness.sdk.domain.model.Exercise
import com.fitness.sdk.domain.model.ExerciseHistory
import com.fitness.sdk.domain.model.ExerciseSessionSummary
import com.fitness.sdk.domain.model.Workout
import com.fitness.sdk.domain.repository.WorkoutRepository
import com.fitness.sdk.data.local.entity.WorkoutWithExercises
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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

    /**
     * Maps WorkoutWithExercises to domain Workout including set records.
     * Must be called from a coroutine context (e.g. withContext(Dispatchers.IO)).
     */
    private suspend fun mapToWorkoutWithSetRecords(workoutWithExercises: WorkoutWithExercises): Workout {
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
        return WorkoutMapper.toDomain(workoutWithExercises.workout, exercises)
    }

    override suspend fun getWorkouts(): List<Workout> = withContext(Dispatchers.IO) {
        workoutDao.getAllWorkouts().map { mapToWorkoutWithSetRecords(it) }
    }

    override suspend fun getWorkoutById(id: Long): Workout? = withContext(Dispatchers.IO) {
        workoutDao.getWorkoutById(id)?.let { mapToWorkoutWithSetRecords(it) }
    }

    override suspend fun getWorkoutsByDateRange(startTime: Long, endTime: Long): List<Workout> =
        withContext(Dispatchers.IO) {
            workoutDao.getWorkoutsByDateRange(startTime, endTime).map { mapToWorkoutWithSetRecords(it) }
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

    override suspend fun addExerciseToWorkout(workoutId: Long, exercise: Exercise) = withContext(Dispatchers.IO) {
        // Map exercise to entity and insert
        val exerciseEntity = ExerciseMapper.toEntity(exercise, workoutId)
        val exerciseId = exerciseDao.insertExercise(exerciseEntity)

        // Insert set records if any
        if (exercise.setRecords.isNotEmpty()) {
            val setEntities = ExerciseSetMapper.toEntityList(exercise.setRecords, exerciseId)
            exerciseDao.insertExerciseSets(setEntities)
        }
    }

    override fun observeWorkouts(): Flow<List<Workout>> = flow {
        workoutDao.observeAllWorkouts().collect { workoutsWithExercises ->
            val workouts = withContext(Dispatchers.IO) {
                workoutsWithExercises.map { mapToWorkoutWithSetRecords(it) }
            }
            emit(workouts)
        }
    }

    override fun observeWorkoutById(id: Long): Flow<Workout?> = flow {
        workoutDao.observeWorkoutById(id).collect { workoutWithExercises ->
            val workout = workoutWithExercises?.let {
                withContext(Dispatchers.IO) { mapToWorkoutWithSetRecords(it) }
            }
            emit(workout)
        }
    }

    override suspend fun getExerciseHistory(exerciseName: String): ExerciseHistory = withContext(Dispatchers.IO) {
        val records = exerciseDao.getExerciseHistoryByName(exerciseName)
        computeExerciseHistory(records)
    }

    /**
     * Compute aggregated exercise history from raw records.
     * Uses Epley formula for 1RM: 1RM = weight × (1 + reps/30)
     */
    private fun computeExerciseHistory(records: List<com.fitness.sdk.data.local.entity.ExerciseHistoryRecord>): ExerciseHistory {
        if (records.isEmpty()) {
            return ExerciseHistory(
                totalSessions = 0,
                totalSets = 0,
                maxWeight = null,
                estimated1RM = null,
                historyByDate = emptyList()
            )
        }

        val workingSets = records.filter { !it.isWarmupSet }
        val totalSessions = records.map { it.workoutId }.distinct().size
        val totalSets = records.size

        val maxWeight = workingSets
            .mapNotNull { it.weight }
            .maxOrNull()

        val estimated1RM = workingSets
            .filter { it.weight != null && it.reps >= 1 }
            .mapNotNull { record ->
                record.weight?.let { w ->
                    calculateEpley1RM(w, record.reps)
                }
            }
            .maxOrNull()

        val sessionsByWorkout = records.groupBy { it.workoutId }
        val historyByDate = sessionsByWorkout.map { (workoutId, sessionRecords) ->
            val workoutDate = sessionRecords.first().workoutDate
            val sessionWorkingSets = sessionRecords.filter { !it.isWarmupSet }
            val bestSet = sessionWorkingSets
                .filter { it.weight != null && it.reps >= 1 }
                .maxByOrNull { it.weight ?: 0f }
                ?.let { "${it.reps} × ${it.weight}kg" }
                ?: sessionWorkingSets.firstOrNull()?.let { "${it.reps} reps" }
                ?: "—"
            val totalVolume = sessionRecords.sumOf { ((it.weight ?: 0f) * it.reps).toDouble() }.toFloat()

            ExerciseSessionSummary(
                workoutId = workoutId,
                workoutDate = workoutDate,
                bestSet = bestSet,
                setsCount = sessionRecords.size,
                totalVolume = totalVolume
            )
        }.sortedByDescending { it.workoutDate }
            .take(5)

        return ExerciseHistory(
            totalSessions = totalSessions,
            totalSets = totalSets,
            maxWeight = maxWeight,
            estimated1RM = estimated1RM,
            historyByDate = historyByDate
        )
    }

    override suspend fun getExerciseSessionCounts(): Map<String, Int> = withContext(Dispatchers.IO) {
        exerciseDao.getExerciseSessionCounts().associate { it.exerciseName to it.sessionCount }
    }

    override fun observeExerciseSessionCounts(): Flow<Map<String, Int>> {
        return exerciseDao.observeExerciseSessionCounts().map { list ->
            list.associate { it.exerciseName to it.sessionCount }
        }
    }

    private fun calculateEpley1RM(weight: Float, reps: Int): Float {
        return if (reps == 1) weight
        else weight * (1 + reps / 30f)
    }
}

