package com.fitness.sdk.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WorkoutTest {

    @Test
    fun `isInProgress returns true when endTime is null`() {
        // Given
        val workout = Workout(
            name = "Workout",
            type = WorkoutType.STRENGTH,
            startTime = System.currentTimeMillis(),
            endTime = null
        )

        // Then
        assertTrue(workout.isInProgress())
    }

    @Test
    fun `isInProgress returns false when endTime is set`() {
        // Given
        val workout = Workout(
            name = "Workout",
            type = WorkoutType.STRENGTH,
            startTime = System.currentTimeMillis(),
            endTime = System.currentTimeMillis() + 3600000
        )

        // Then
        assertFalse(workout.isInProgress())
    }

    @Test
    fun `calculateTotalVolume sums all exercise volumes`() {
        // Given
        val exercises = listOf(
            Exercise(name = "Bench Press", sets = 3, reps = 10, weight = 60f),  // 1800
            Exercise(name = "Squats", sets = 4, reps = 8, weight = 100f)         // 3200
        )
        val workout = Workout(
            name = "Strength Training",
            type = WorkoutType.STRENGTH,
            exercises = exercises
        )

        // When
        val totalVolume = workout.calculateTotalVolume()

        // Then
        assertEquals(5000f, totalVolume, 0.01f)
    }

    @Test
    fun `getTotalSets returns sum of all sets`() {
        // Given
        val exercises = listOf(
            Exercise(name = "Exercise 1", sets = 3, reps = 10),
            Exercise(name = "Exercise 2", sets = 4, reps = 8)
        )
        val workout = Workout(
            name = "Workout",
            type = WorkoutType.STRENGTH,
            exercises = exercises
        )

        // Then
        assertEquals(7, workout.getTotalSets())
    }

    @Test
    fun `getTotalReps returns sum of all reps`() {
        // Given
        val exercises = listOf(
            Exercise(name = "Exercise 1", sets = 3, reps = 10), // 30
            Exercise(name = "Exercise 2", sets = 4, reps = 8)   // 32
        )
        val workout = Workout(
            name = "Workout",
            type = WorkoutType.STRENGTH,
            exercises = exercises
        )

        // Then
        assertEquals(62, workout.getTotalReps())
    }

    @Test
    fun `finish sets endTime and calculates duration`() {
        // Given
        val startTime = System.currentTimeMillis() - 3600000 // 1 hour ago
        val workout = Workout(
            name = "Workout",
            type = WorkoutType.CARDIO,
            startTime = startTime
        )

        // When
        val finishedWorkout = workout.finish()

        // Then
        assertNotNull(finishedWorkout.endTime)
        assertFalse(finishedWorkout.isInProgress())
        assertTrue(finishedWorkout.durationMinutes >= 59) // Allow for small timing differences
    }
}
