package com.fitness.sdk.domain.usecase

import com.fitness.sdk.domain.model.Workout
import com.fitness.sdk.domain.model.WorkoutType
import com.fitness.sdk.domain.repository.WorkoutRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetWorkoutsUseCaseTest {

    private lateinit var repository: WorkoutRepository
    private lateinit var getWorkoutsUseCase: GetWorkoutsUseCase

    @Before
    fun setup() {
        repository = mockk()
        getWorkoutsUseCase = GetWorkoutsUseCase(repository)
    }

    @Test
    fun `invoke returns all workouts`() = runTest {
        // Given
        val workouts = listOf(
            createWorkout(1, "Workout 1", WorkoutType.STRENGTH),
            createWorkout(2, "Workout 2", WorkoutType.CARDIO)
        )
        coEvery { repository.getWorkouts() } returns workouts

        // When
        val result = getWorkoutsUseCase()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
    }

    @Test
    fun `byType filters workouts correctly`() = runTest {
        // Given
        val workouts = listOf(
            createWorkout(1, "Workout 1", WorkoutType.STRENGTH),
            createWorkout(2, "Workout 2", WorkoutType.CARDIO),
            createWorkout(3, "Workout 3", WorkoutType.STRENGTH)
        )
        coEvery { repository.getWorkouts() } returns workouts

        // When
        val result = getWorkoutsUseCase.byType(WorkoutType.STRENGTH)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
        result.getOrNull()?.forEach { workout ->
            assertEquals(WorkoutType.STRENGTH, workout.type)
        }
    }

    @Test
    fun `byDateRange returns workouts in range`() = runTest {
        // Given
        val startTime = 1000L
        val endTime = 2000L
        val workouts = listOf(
            createWorkout(1, "Workout 1", WorkoutType.STRENGTH)
        )
        coEvery { repository.getWorkoutsByDateRange(startTime, endTime) } returns workouts

        // When
        val result = getWorkoutsUseCase.byDateRange(startTime, endTime)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
    }

    @Test
    fun `byDateRange with invalid range returns failure`() = runTest {
        // When
        val result = getWorkoutsUseCase.byDateRange(2000L, 1000L)

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun `observe returns flow of workouts`() = runTest {
        // Given
        val workouts = listOf(createWorkout(1, "Workout 1", WorkoutType.STRENGTH))
        coEvery { repository.observeWorkouts() } returns flowOf(workouts)

        // When
        val result = getWorkoutsUseCase.observe().toList()

        // Then
        assertEquals(1, result.size)
        assertEquals(1, result[0].size)
    }

    private fun createWorkout(id: Long, name: String, type: WorkoutType): Workout {
        return Workout(
            id = id,
            name = name,
            type = type,
            startTime = System.currentTimeMillis(),
            durationMinutes = 30,
            caloriesBurned = 200
        )
    }
}
