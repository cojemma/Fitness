package com.fitness.sdk.data.mapper

import com.fitness.sdk.data.local.entity.ExerciseEntity
import com.fitness.sdk.data.local.entity.WorkoutEntity
import com.fitness.sdk.data.local.entity.WorkoutWithExercises
import com.fitness.sdk.domain.model.WorkoutType
import org.junit.Assert.assertEquals
import org.junit.Test

class WorkoutMapperTest {

    @Test
    fun `toDomain converts WorkoutWithExercises correctly`() {
        // Given
        val workoutEntity = WorkoutEntity(
            id = 1,
            name = "Morning Run",
            type = "CARDIO",
            templateId = null,
            startTime = 1000L,
            endTime = 2000L,
            durationMinutes = 30,
            caloriesBurned = 250,
            notes = "Great workout!",
            createdAt = 1000L,
            updatedAt = 1500L
        )
        val exerciseEntities = listOf(
            ExerciseEntity(1, 1, "Running", 0, 0, null, 1800, 0, null)
        )
        val workoutWithExercises = WorkoutWithExercises(workoutEntity, exerciseEntities)

        // When
        val workout = WorkoutMapper.toDomain(workoutWithExercises)

        // Then
        assertEquals(1L, workout.id)
        assertEquals("Morning Run", workout.name)
        assertEquals(WorkoutType.CARDIO, workout.type)
        assertEquals(1000L, workout.startTime)
        assertEquals(2000L, workout.endTime)
        assertEquals(30, workout.durationMinutes)
        assertEquals(250, workout.caloriesBurned)
        assertEquals("Great workout!", workout.notes)
        assertEquals(1, workout.exercises.size)
    }

    @Test
    fun `toDomain handles unknown workout type gracefully`() {
        // Given
        val workoutEntity = WorkoutEntity(
            id = 1,
            name = "Unknown Workout",
            type = "UNKNOWN_TYPE",
            templateId = null,
            startTime = 1000L,
            endTime = null,
            durationMinutes = 30,
            caloriesBurned = 100,
            notes = null,
            createdAt = 1000L,
            updatedAt = 1000L
        )
        val workoutWithExercises = WorkoutWithExercises(workoutEntity, emptyList())

        // When
        val workout = WorkoutMapper.toDomain(workoutWithExercises)

        // Then
        assertEquals(WorkoutType.OTHER, workout.type)
    }

    @Test
    fun `toDomainList converts list correctly`() {
        // Given
        val list = listOf(
            WorkoutWithExercises(
                WorkoutEntity(
                    id = 1,
                    name = "W1",
                    type = "STRENGTH",
                    templateId = null,
                    startTime = 1000L,
                    endTime = null,
                    durationMinutes = 60,
                    caloriesBurned = 300,
                    notes = null,
                    createdAt = 1000L,
                    updatedAt = 1000L
                ),
                emptyList()
            ),
            WorkoutWithExercises(
                WorkoutEntity(
                    id = 2,
                    name = "W2",
                    type = "CARDIO",
                    templateId = null,
                    startTime = 2000L,
                    endTime = null,
                    durationMinutes = 30,
                    caloriesBurned = 200,
                    notes = null,
                    createdAt = 2000L,
                    updatedAt = 2000L
                ),
                emptyList()
            )
        )

        // When
        val workouts = WorkoutMapper.toDomainList(list)

        // Then
        assertEquals(2, workouts.size)
        assertEquals("W1", workouts[0].name)
        assertEquals("W2", workouts[1].name)
    }
}
