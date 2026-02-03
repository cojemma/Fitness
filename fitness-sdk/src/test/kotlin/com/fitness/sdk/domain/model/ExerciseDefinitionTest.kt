package com.fitness.sdk.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExerciseDefinitionTest {

    @Test
    fun `toExercise converts to workout Exercise with default values`() {
        // Given
        val definition = ExerciseDefinition(
            id = "chest_bench_press",
            name = "Bench Press",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.CHEST,
            defaultSets = 4,
            defaultReps = 8
        )

        // When
        val exercise = definition.toExercise()

        // Then
        assertEquals("Bench Press", exercise.name)
        assertEquals(4, exercise.sets)
        assertEquals(8, exercise.reps)
        assertEquals(0, exercise.durationSeconds)
    }

    @Test
    fun `toExercise with custom values overrides defaults`() {
        // Given
        val definition = ExerciseDefinition(
            id = "chest_bench_press",
            name = "Bench Press",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.CHEST,
            defaultSets = 4,
            defaultReps = 8
        )

        // When
        val exercise = definition.toExercise(
            sets = 5,
            reps = 5,
            weight = 100f,
            restSeconds = 120
        )

        // Then
        assertEquals(5, exercise.sets)
        assertEquals(5, exercise.reps)
        assertEquals(100f, exercise.weight)
        assertEquals(120, exercise.restSeconds)
    }

    @Test
    fun `toExercise for time-based exercise sets reps to zero`() {
        // Given
        val definition = ExerciseDefinition(
            id = "core_plank",
            name = "Plank",
            category = ExerciseCategory.BODYWEIGHT,
            primaryMuscle = MuscleGroup.CORE,
            isTimeBased = true,
            defaultSets = 3,
            defaultDurationSeconds = 60
        )

        // When
        val exercise = definition.toExercise()

        // Then
        assertEquals("Plank", exercise.name)
        assertEquals(3, exercise.sets)
        assertEquals(0, exercise.reps) // Time-based, no reps
        assertEquals(60, exercise.durationSeconds)
        assertTrue(exercise.isTimedExercise())
    }

    @Test
    fun `toExercise for rep-based exercise sets duration to zero`() {
        // Given
        val definition = ExerciseDefinition(
            id = "chest_pushup",
            name = "Push-Up",
            category = ExerciseCategory.BODYWEIGHT,
            primaryMuscle = MuscleGroup.CHEST,
            isTimeBased = false,
            defaultSets = 3,
            defaultReps = 15
        )

        // When
        val exercise = definition.toExercise()

        // Then
        assertEquals(15, exercise.reps)
        assertEquals(0, exercise.durationSeconds) // Rep-based, no duration
        assertFalse(exercise.isTimedExercise())
    }
}
