package com.fitness.sdk.domain.usecase

import com.fitness.sdk.data.local.dao.ExerciseDao
import com.fitness.sdk.data.local.dao.WorkoutDao
import com.fitness.sdk.data.mapper.WorkoutMapper
import com.fitness.sdk.domain.model.LastSessionData
import com.fitness.sdk.domain.model.LastSetData

/**
 * Use case for getting the last session data for a template.
 * This is used to pre-populate the workout form with the user's last performance.
 */
class GetLastSessionDataUseCase(
    private val workoutDao: WorkoutDao,
    private val exerciseDao: ExerciseDao
) {
    /**
     * Get the last session data for a template.
     *
     * @param templateId The ID of the template
     * @return Result containing LastSessionData, or null if no previous session exists
     */
    suspend operator fun invoke(templateId: Long): Result<LastSessionData?> {
        if (templateId <= 0) {
            return Result.failure(IllegalArgumentException("Invalid template ID: $templateId"))
        }

        return try {
            val lastWorkout = workoutDao.getLastWorkoutByTemplateId(templateId)
            
            if (lastWorkout == null) {
                Result.success(null)
            } else {
                val workout = WorkoutMapper.toDomain(lastWorkout)
                
                // Fetch set records for all exercises in this workout
                val exerciseIds = lastWorkout.exercises.map { it.id }
                val allSetRecords = if (exerciseIds.isNotEmpty()) {
                    exerciseDao.getExerciseSetsByExerciseIds(exerciseIds)
                        .groupBy { it.exerciseId }
                } else {
                    emptyMap()
                }
                
                // Build exercise data map using actual setRecords when available
                val exerciseData = lastWorkout.exercises.groupBy { it.name }
                    .mapValues { (_, exercises) ->
                        exercises.flatMap { exerciseEntity ->
                            val setRecords = allSetRecords[exerciseEntity.id] ?: emptyList()
                            if (setRecords.isNotEmpty()) {
                                // Use actual per-set records for accurate progressive overload
                                setRecords.map { set ->
                                    LastSetData(
                                        exerciseName = exerciseEntity.name,
                                        setNumber = set.setNumber,
                                        actualReps = set.reps,
                                        actualWeight = set.weight,
                                        completedAt = set.completedAt
                                    )
                                }
                            } else {
                                // Fallback for legacy data without setRecords
                                (1..exerciseEntity.sets).map { setNumber ->
                                    LastSetData(
                                        exerciseName = exerciseEntity.name,
                                        setNumber = setNumber,
                                        actualReps = exerciseEntity.reps,
                                        actualWeight = exerciseEntity.weight,
                                        completedAt = workout.startTime
                                    )
                                }
                            }
                        }
                    }

                val lastSessionData = LastSessionData(
                    templateId = templateId,
                    lastWorkoutId = workout.id,
                    lastWorkoutDate = workout.startTime,
                    exerciseData = exerciseData
                )
                
                Result.success(lastSessionData)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

