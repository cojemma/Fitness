package com.fitness.sdk.domain.usecase

import com.fitness.sdk.data.local.dao.WorkoutDao
import com.fitness.sdk.data.mapper.WorkoutMapper
import com.fitness.sdk.domain.model.LastSessionData
import com.fitness.sdk.domain.model.LastSetData

/**
 * Use case for getting the last session data for a template.
 * This is used to pre-populate the workout form with the user's last performance.
 */
class GetLastSessionDataUseCase(
    private val workoutDao: WorkoutDao
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
                
                // Build exercise data map
                val exerciseData = workout.exercises.groupBy { it.name }
                    .mapValues { (_, exercises) ->
                        exercises.flatMapIndexed { exerciseIndex, exercise ->
                            // Each exercise has sets * reps, we represent each set
                            (1..exercise.sets).map { setNumber ->
                                LastSetData(
                                    exerciseName = exercise.name,
                                    setNumber = setNumber,
                                    actualReps = exercise.reps,
                                    actualWeight = exercise.weight,
                                    completedAt = workout.startTime
                                )
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
