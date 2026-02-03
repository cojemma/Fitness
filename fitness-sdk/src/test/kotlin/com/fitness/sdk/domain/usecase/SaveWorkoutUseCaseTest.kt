package com.fitness.sdk.domain.usecase

import com.fitness.sdk.domain.model.Exercise
import com.fitness.sdk.domain.model.Workout
import com.fitness.sdk.domain.model.WorkoutType
import com.fitness.sdk.domain.repository.WorkoutRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SaveWorkoutUseCaseTest {

    private lateinit var repository: WorkoutRepository
    private lateinit var saveWorkoutUseCase: SaveWorkoutUseCase

    @Before
    fun setup() {
        repository = mockk()
        saveWorkoutUseCase = SaveWorkoutUseCase(repository)
    }

    @Test
    fun `invoke with valid workout saves and returns id`() = runTest {
        // Given
        val workout = createValidWorkout()
        coEvery { repository.saveWorkout(any()) } returns 1L

        // When
        val result = saveWorkoutUseCase(workout)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1L, result.getOrNull())
        coVerify { repository.saveWorkout(workout) }
    }

    @Test
    fun `invoke with blank name returns failure`() = runTest {
        // Given
        val workout = createValidWorkout().copy(name = "")

        // When
        val result = saveWorkoutUseCase(workout)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `invoke with invalid start time returns failure`() = runTest {
        // Given
        val workout = createValidWorkout().copy(startTime = 0)

        // When
        val result = saveWorkoutUseCase(workout)

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun `invoke with negative duration returns failure`() = runTest {
        // Given
        val workout = createValidWorkout().copy(durationMinutes = -10)

        // When
        val result = saveWorkoutUseCase(workout)

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun `invoke with negative calories returns failure`() = runTest {
        // Given
        val workout = createValidWorkout().copy(caloriesBurned = -100)

        // When
        val result = saveWorkoutUseCase(workout)

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun `invoke with blank exercise name returns failure`() = runTest {
        // Given
        val exercise = Exercise(name = "", sets = 3, reps = 10)
        val workout = createValidWorkout().copy(exercises = listOf(exercise))

        // When
        val result = saveWorkoutUseCase(workout)

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun `invoke with valid exercises saves successfully`() = runTest {
        // Given
        val exercises = listOf(
            Exercise(name = "Bench Press", sets = 3, reps = 10, weight = 60f),
            Exercise(name = "Squats", sets = 4, reps = 8, weight = 80f)
        )
        val workout = createValidWorkout().copy(exercises = exercises)
        coEvery { repository.saveWorkout(any()) } returns 1L

        // When
        val result = saveWorkoutUseCase(workout)

        // Then
        assertTrue(result.isSuccess)
    }

    private fun createValidWorkout(): Workout {
        return Workout(
            name = "Morning Workout",
            type = WorkoutType.STRENGTH,
            startTime = System.currentTimeMillis(),
            durationMinutes = 60,
            caloriesBurned = 300
        )
    }
}
