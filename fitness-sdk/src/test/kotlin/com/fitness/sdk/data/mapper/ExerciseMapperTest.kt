package com.fitness.sdk.data.mapper

import com.fitness.sdk.data.local.entity.ExerciseEntity
import com.fitness.sdk.domain.model.Exercise
import org.junit.Assert.assertEquals
import org.junit.Test

class ExerciseMapperTest {

    @Test
    fun `toEntity converts Exercise to ExerciseEntity correctly`() {
        // Given
        val exercise = Exercise(
            id = 1,
            workoutId = 10,
            name = "Bench Press",
            sets = 3,
            reps = 10,
            weight = 60f,
            durationSeconds = 0,
            restSeconds = 90,
            notes = "Warm up properly"
        )

        // When
        val entity = ExerciseMapper.toEntity(exercise, 10)

        // Then
        assertEquals(1L, entity.id)
        assertEquals(10L, entity.workoutId)
        assertEquals("Bench Press", entity.name)
        assertEquals(3, entity.sets)
        assertEquals(10, entity.reps)
        assertEquals(60f, entity.weight)
        assertEquals(0, entity.durationSeconds)
        assertEquals(90, entity.restSeconds)
        assertEquals("Warm up properly", entity.notes)
    }

    @Test
    fun `toDomain converts ExerciseEntity to Exercise correctly`() {
        // Given
        val entity = ExerciseEntity(
            id = 1,
            workoutId = 10,
            name = "Squats",
            sets = 4,
            reps = 8,
            weight = 100f,
            durationSeconds = 0,
            restSeconds = 120,
            notes = null
        )

        // When
        val exercise = ExerciseMapper.toDomain(entity)

        // Then
        assertEquals(1L, exercise.id)
        assertEquals(10L, exercise.workoutId)
        assertEquals("Squats", exercise.name)
        assertEquals(4, exercise.sets)
        assertEquals(8, exercise.reps)
        assertEquals(100f, exercise.weight)
        assertEquals(0, exercise.durationSeconds)
        assertEquals(120, exercise.restSeconds)
        assertEquals(null, exercise.notes)
    }

    @Test
    fun `toDomainList converts list correctly`() {
        // Given
        val entities = listOf(
            ExerciseEntity(1, 10, "Exercise 1", 3, 10, null, 0, 60, null),
            ExerciseEntity(2, 10, "Exercise 2", 4, 12, 50f, 0, 90, null)
        )

        // When
        val exercises = ExerciseMapper.toDomainList(entities)

        // Then
        assertEquals(2, exercises.size)
        assertEquals("Exercise 1", exercises[0].name)
        assertEquals("Exercise 2", exercises[1].name)
    }

    @Test
    fun `toEntityList converts list correctly`() {
        // Given
        val exercises = listOf(
            Exercise(name = "Exercise 1", sets = 3, reps = 10),
            Exercise(name = "Exercise 2", sets = 4, reps = 12, weight = 50f)
        )

        // When
        val entities = ExerciseMapper.toEntityList(exercises, 10)

        // Then
        assertEquals(2, entities.size)
        assertEquals(10L, entities[0].workoutId)
        assertEquals(10L, entities[1].workoutId)
    }
}
