package com.fitness.sdk.api

import com.fitness.sdk.domain.model.Exercise
import com.fitness.sdk.domain.model.ExerciseHistory
import com.fitness.sdk.domain.model.Workout
import com.fitness.sdk.domain.model.WorkoutType
import com.fitness.sdk.domain.usecase.DeleteWorkoutUseCase
import com.fitness.sdk.domain.usecase.GetExerciseHistoryUseCase
import com.fitness.sdk.domain.usecase.GetWorkoutByIdUseCase
import com.fitness.sdk.domain.usecase.GetWorkoutsUseCase
import com.fitness.sdk.domain.usecase.SaveWorkoutUseCase
import com.fitness.sdk.domain.usecase.UpdateWorkoutUseCase
import com.fitness.sdk.domain.usecase.AddExerciseToWorkoutUseCase
import kotlinx.coroutines.flow.Flow

/**
 * Implementation of WorkoutManager interface.
 * Delegates to use cases for business logic.
 */
internal class WorkoutManagerImpl(
    private val saveWorkoutUseCase: SaveWorkoutUseCase,
    private val getWorkoutsUseCase: GetWorkoutsUseCase,
    private val getWorkoutByIdUseCase: GetWorkoutByIdUseCase,
    private val updateWorkoutUseCase: UpdateWorkoutUseCase,
    private val deleteWorkoutUseCase: DeleteWorkoutUseCase,
    private val addExerciseToWorkoutUseCase: AddExerciseToWorkoutUseCase,
    private val getExerciseHistoryUseCase: GetExerciseHistoryUseCase
) : WorkoutManager {

    override suspend fun createWorkout(workout: Workout): Result<Long> {
        return saveWorkoutUseCase(workout)
    }

    override suspend fun getAllWorkouts(): Result<List<Workout>> {
        return getWorkoutsUseCase()
    }

    override suspend fun getWorkoutsByType(type: WorkoutType): Result<List<Workout>> {
        return getWorkoutsUseCase.byType(type)
    }

    override suspend fun getWorkoutsByDateRange(startTime: Long, endTime: Long): Result<List<Workout>> {
        return getWorkoutsUseCase.byDateRange(startTime, endTime)
    }

    override suspend fun getWorkout(id: Long): Result<Workout?> {
        return getWorkoutByIdUseCase(id)
    }

    override suspend fun updateWorkout(workout: Workout): Result<Unit> {
        return updateWorkoutUseCase(workout)
    }

    override suspend fun deleteWorkout(id: Long): Result<Unit> {
        return deleteWorkoutUseCase(id)
    }

    override fun observeWorkouts(): Flow<List<Workout>> {
        return getWorkoutsUseCase.observe()
    }

    override fun observeWorkout(id: Long): Flow<Workout?> {
        return getWorkoutByIdUseCase.observe(id)
    }

    override suspend fun addExerciseToWorkout(workoutId: Long, exercise: Exercise): Result<Unit> {
        return addExerciseToWorkoutUseCase(workoutId, exercise)
    }

    override suspend fun getExerciseHistory(exerciseName: String): Result<ExerciseHistory> {
        return try {
            Result.success(getExerciseHistoryUseCase(exerciseName))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
