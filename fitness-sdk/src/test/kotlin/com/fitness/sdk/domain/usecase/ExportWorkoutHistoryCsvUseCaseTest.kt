package com.fitness.sdk.domain.usecase

import com.fitness.sdk.domain.model.Exercise
import com.fitness.sdk.domain.model.ExerciseSet
import com.fitness.sdk.domain.model.Workout
import com.fitness.sdk.domain.model.WorkoutType
import com.fitness.sdk.domain.repository.WorkoutRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ExportWorkoutHistoryCsvUseCaseTest {

    private lateinit var repository: WorkoutRepository
    private lateinit var useCase: ExportWorkoutHistoryCsvUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = ExportWorkoutHistoryCsvUseCase(repository)
    }

    @Test
    fun `csv contains header row`() = runTest {
        coEvery { repository.getWorkoutsByDateRange(any(), any()) } returns emptyList()

        val csv = useCase(0L, System.currentTimeMillis())
        val lines = csv.trim().lines()

        assertEquals(1, lines.size)
        assertEquals(
            "Date,Workout Name,Workout Type,Duration (min),Exercise Name,Set #,Weight (kg),Reps,Warmup,Volume (kg)",
            lines[0]
        )
    }

    @Test
    fun `csv generates one row per set record`() = runTest {
        val workout = Workout(
            id = 1,
            name = "Leg Day",
            type = WorkoutType.STRENGTH,
            startTime = 1709337600000L, // 2024-03-02 00:00 UTC
            durationMinutes = 60,
            exercises = listOf(
                Exercise(
                    name = "Squat",
                    sets = 3,
                    reps = 5,
                    weight = 100f,
                    setRecords = listOf(
                        ExerciseSet(setNumber = 1, reps = 5, weight = 100f, isWarmupSet = false),
                        ExerciseSet(setNumber = 2, reps = 5, weight = 100f, isWarmupSet = false),
                        ExerciseSet(setNumber = 3, reps = 4, weight = 100f, isWarmupSet = false)
                    )
                )
            )
        )
        coEvery { repository.getWorkoutsByDateRange(any(), any()) } returns listOf(workout)

        val csv = useCase(0L, System.currentTimeMillis())
        val lines = csv.trim().lines()

        // Header + 3 set rows
        assertEquals(4, lines.size)
        // Verify set 3 has 4 reps
        assertTrue(lines[3].contains(",3,100.0,4,No,400.0"))
    }

    @Test
    fun `csv uses summary row when no set records`() = runTest {
        val workout = Workout(
            id = 1,
            name = "Quick Workout",
            type = WorkoutType.STRENGTH,
            startTime = 1709337600000L,
            durationMinutes = 30,
            exercises = listOf(
                Exercise(
                    name = "Push-ups",
                    sets = 3,
                    reps = 20,
                    weight = null,
                    setRecords = emptyList()
                )
            )
        )
        coEvery { repository.getWorkoutsByDateRange(any(), any()) } returns listOf(workout)

        val csv = useCase(0L, System.currentTimeMillis())
        val lines = csv.trim().lines()

        assertEquals(2, lines.size)
        assertTrue(lines[1].contains("Push-ups,1-3,0.0,20,No,0.0"))
    }

    @Test
    fun `csv escapes fields with commas`() {
        val workout = Workout(
            id = 1,
            name = "Push, Pull, Legs",
            type = WorkoutType.STRENGTH,
            startTime = 1709337600000L,
            durationMinutes = 90,
            exercises = listOf(
                Exercise(
                    name = "Bench Press",
                    sets = 1,
                    reps = 10,
                    weight = 60f,
                    setRecords = listOf(
                        ExerciseSet(setNumber = 1, reps = 10, weight = 60f)
                    )
                )
            )
        )

        val csv = useCase.buildCsv(listOf(workout))
        val lines = csv.trim().lines()

        // Workout name with comma should be quoted
        assertTrue(lines[1].contains("\"Push, Pull, Legs\""))
    }

    @Test
    fun `csv handles warmup sets`() = runTest {
        val workout = Workout(
            id = 1,
            name = "Warmup Test",
            type = WorkoutType.STRENGTH,
            startTime = 1709337600000L,
            durationMinutes = 20,
            exercises = listOf(
                Exercise(
                    name = "Deadlift",
                    sets = 2,
                    reps = 5,
                    weight = 60f,
                    setRecords = listOf(
                        ExerciseSet(setNumber = 1, reps = 5, weight = 40f, isWarmupSet = true),
                        ExerciseSet(setNumber = 2, reps = 5, weight = 60f, isWarmupSet = false)
                    )
                )
            )
        )
        coEvery { repository.getWorkoutsByDateRange(any(), any()) } returns listOf(workout)

        val csv = useCase(0L, System.currentTimeMillis())
        val lines = csv.trim().lines()

        assertEquals(3, lines.size)
        assertTrue(lines[1].contains(",Yes,")) // Warmup set
        assertTrue(lines[2].contains(",No,"))  // Working set
    }

    @Test(expected = IllegalArgumentException::class)
    fun `throws when startTime greater than endTime`() = runTest {
        useCase(2000L, 1000L)
    }
}
