package com.fitness.sdk.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExerciseTest {

    @Test
    fun `calculateVolume returns correct value for weighted exercise`() {
        // Given
        val exercise = Exercise(
            name = "Bench Press",
            sets = 3,
            reps = 10,
            weight = 60f
        )

        // When
        val volume = exercise.calculateVolume()

        // Then
        assertEquals(1800f, volume, 0.01f)
    }

    @Test
    fun `calculateVolume returns zero for bodyweight exercise`() {
        // Given
        val exercise = Exercise(
            name = "Push Ups",
            sets = 3,
            reps = 20,
            weight = null
        )

        // When
        val volume = exercise.calculateVolume()

        // Then
        assertEquals(0f, volume, 0.01f)
    }

    @Test
    fun `isTimedExercise returns true for duration-based exercise`() {
        // Given
        val exercise = Exercise(
            name = "Plank",
            sets = 3,
            reps = 0,
            durationSeconds = 60
        )

        // Then
        assertTrue(exercise.isTimedExercise())
    }

    @Test
    fun `isTimedExercise returns false for rep-based exercise`() {
        // Given
        val exercise = Exercise(
            name = "Squats",
            sets = 3,
            reps = 10,
            durationSeconds = 0
        )

        // Then
        assertFalse(exercise.isTimedExercise())
    }
}
