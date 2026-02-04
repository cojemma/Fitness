package com.fitness.sdk.domain.usecase

import com.fitness.sdk.data.local.dao.ExerciseDao
import com.fitness.sdk.data.local.dao.WorkoutDao
import com.fitness.sdk.data.local.entity.ExerciseEntity
import com.fitness.sdk.data.local.entity.ExerciseSetEntity
import com.fitness.sdk.data.local.entity.WorkoutEntity
import com.fitness.sdk.data.local.entity.WorkoutWithExercises
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetLastSessionDataUseCaseTest {

    private lateinit var workoutDao: WorkoutDao
    private lateinit var exerciseDao: ExerciseDao
    private lateinit var useCase: GetLastSessionDataUseCase

    @Before
    fun setup() {
        workoutDao = mockk()
        exerciseDao = mockk()
        useCase = GetLastSessionDataUseCase(workoutDao, exerciseDao)
    }

    @Test
    fun `invoke returns null when no previous workout exists`() = runTest {
        // Given
        val templateId = 1L
        coEvery { workoutDao.getLastWorkoutByTemplateId(templateId) } returns null

        // When
        val result = useCase(templateId)

        // Then
        assertTrue(result.isSuccess)
        assertNull(result.getOrNull())
    }

    @Test
    fun `invoke returns failure for invalid templateId`() = runTest {
        // When
        val result = useCase(-1L)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `invoke returns correct LastSetData from setRecords when available`() = runTest {
        // Given
        val templateId = 1L
        val workoutEntity = WorkoutEntity(
            id = 1L,
            name = "Test Workout",
            type = "STRENGTH",
            templateId = templateId,
            startTime = 1000L,
            endTime = 2000L,
            durationMinutes = 60,
            caloriesBurned = 300,
            notes = null,
            createdAt = 1000L,
            updatedAt = 1000L
        )
        val exerciseEntity = ExerciseEntity(
            id = 1L,
            workoutId = 1L,
            name = "Bench Press",
            sets = 3,
            reps = 10,
            weight = 60f,
            durationSeconds = 0,
            restSeconds = 90,
            notes = null
        )
        val setRecords = listOf(
            ExerciseSetEntity(id = 1L, exerciseId = 1L, setNumber = 1, reps = 8, weight = 50f, isWarmupSet = false, completedAt = 1001L),
            ExerciseSetEntity(id = 2L, exerciseId = 1L, setNumber = 2, reps = 10, weight = 55f, isWarmupSet = false, completedAt = 1002L),
            ExerciseSetEntity(id = 3L, exerciseId = 1L, setNumber = 3, reps = 12, weight = 60f, isWarmupSet = false, completedAt = 1003L)
        )
        val workoutWithExercises = WorkoutWithExercises(
            workout = workoutEntity,
            exercises = listOf(exerciseEntity)
        )
        coEvery { workoutDao.getLastWorkoutByTemplateId(templateId) } returns workoutWithExercises
        coEvery { exerciseDao.getExerciseSetsByExerciseIds(listOf(1L)) } returns setRecords

        // When
        val result = useCase(templateId)

        // Then
        assertTrue(result.isSuccess)
        val data = result.getOrNull()
        assertNotNull(data)
        assertEquals(templateId, data!!.templateId)
        assertEquals(1L, data.lastWorkoutId)

        val benchPressData = data.getExerciseData("Bench Press")
        assertEquals(3, benchPressData.size)

        // Verify first set
        val set1 = data.getSetData("Bench Press", 1)
        assertNotNull(set1)
        assertEquals(8, set1!!.actualReps)
        assertEquals(50f, set1.actualWeight)

        // Verify second set
        val set2 = data.getSetData("Bench Press", 2)
        assertNotNull(set2)
        assertEquals(10, set2!!.actualReps)
        assertEquals(55f, set2.actualWeight)

        // Verify third set
        val set3 = data.getSetData("Bench Press", 3)
        assertNotNull(set3)
        assertEquals(12, set3!!.actualReps)
        assertEquals(60f, set3.actualWeight)
    }

    @Test
    fun `invoke falls back to aggregate data when setRecords is empty`() = runTest {
        // Given
        val templateId = 1L
        val workoutEntity = WorkoutEntity(
            id = 1L,
            name = "Test Workout",
            type = "STRENGTH",
            templateId = templateId,
            startTime = 1000L,
            endTime = 2000L,
            durationMinutes = 60,
            caloriesBurned = 300,
            notes = null,
            createdAt = 1000L,
            updatedAt = 1000L
        )
        val exerciseEntity = ExerciseEntity(
            id = 1L,
            workoutId = 1L,
            name = "Squat",
            sets = 4,
            reps = 8,
            weight = 100f,
            durationSeconds = 0,
            restSeconds = 120,
            notes = null
        )
        val workoutWithExercises = WorkoutWithExercises(
            workout = workoutEntity,
            exercises = listOf(exerciseEntity)
        )
        coEvery { workoutDao.getLastWorkoutByTemplateId(templateId) } returns workoutWithExercises
        coEvery { exerciseDao.getExerciseSetsByExerciseIds(listOf(1L)) } returns emptyList()

        // When
        val result = useCase(templateId)

        // Then
        assertTrue(result.isSuccess)
        val data = result.getOrNull()
        assertNotNull(data)

        val squatData = data!!.getExerciseData("Squat")
        assertEquals(4, squatData.size) // 4 sets from aggregate

        // All sets should have the aggregate values
        squatData.forEach { setData ->
            assertEquals(8, setData.actualReps)
            assertEquals(100f, setData.actualWeight)
        }
    }
}
