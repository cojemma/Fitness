package com.fitness.sdk.data.library

import com.fitness.sdk.domain.model.ExerciseCategory
import com.fitness.sdk.domain.model.MuscleGroup
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DefaultExerciseLibraryTest {

    private lateinit var library: DefaultExerciseLibrary

    @Before
    fun setup() {
        library = DefaultExerciseLibrary()
    }

    @Test
    fun `getAllExercises returns all exercises`() {
        // When
        val exercises = library.getAllExercises()

        // Then
        assertTrue("Library should have at least 50 exercises", exercises.size >= 50)
    }

    @Test
    fun `getExerciseById returns correct exercise`() {
        // When
        val exercise = library.getExerciseById("chest_bench_press")

        // Then
        assertNotNull(exercise)
        assertEquals("Barbell Bench Press", exercise?.name)
        assertEquals(ExerciseCategory.STRENGTH, exercise?.category)
        assertEquals(MuscleGroup.CHEST, exercise?.primaryMuscle)
    }

    @Test
    fun `getExerciseById returns null for unknown id`() {
        // When
        val exercise = library.getExerciseById("unknown_exercise")

        // Then
        assertNull(exercise)
    }

    @Test
    fun `getExercisesByCategory returns only exercises of that category`() {
        // When
        val strengthExercises = library.getExercisesByCategory(ExerciseCategory.STRENGTH)
        val cardioExercises = library.getExercisesByCategory(ExerciseCategory.CARDIO)

        // Then
        assertTrue(strengthExercises.isNotEmpty())
        assertTrue(strengthExercises.all { it.category == ExerciseCategory.STRENGTH })
        
        assertTrue(cardioExercises.isNotEmpty())
        assertTrue(cardioExercises.all { it.category == ExerciseCategory.CARDIO })
    }

    @Test
    fun `getExercisesByMuscleGroup returns exercises targeting that muscle`() {
        // When
        val chestExercises = library.getExercisesByMuscleGroup(MuscleGroup.CHEST)

        // Then
        assertTrue(chestExercises.isNotEmpty())
        assertTrue(chestExercises.all { 
            it.primaryMuscle == MuscleGroup.CHEST || MuscleGroup.CHEST in it.secondaryMuscles 
        })
    }

    @Test
    fun `getExercisesByMuscleGroup includes secondary muscle exercises`() {
        // Triceps is secondary in bench press
        val tricepsExercises = library.getExercisesByMuscleGroup(MuscleGroup.TRICEPS)

        // Should include exercises where triceps is primary OR secondary
        val hasBenchPress = tricepsExercises.any { it.id == "chest_bench_press" }
        assertTrue("Bench press should be included (triceps is secondary)", hasBenchPress)
    }

    @Test
    fun `searchExercises finds exercises by partial name match`() {
        // When
        val results = library.searchExercises("press")

        // Then
        assertTrue(results.isNotEmpty())
        assertTrue(results.all { it.name.lowercase().contains("press") })
    }

    @Test
    fun `searchExercises is case insensitive`() {
        // When
        val lowerResults = library.searchExercises("squat")
        val upperResults = library.searchExercises("SQUAT")
        val mixedResults = library.searchExercises("Squat")

        // Then
        assertEquals(lowerResults.size, upperResults.size)
        assertEquals(lowerResults.size, mixedResults.size)
    }

    @Test
    fun `searchExercises with blank query returns all exercises`() {
        // When
        val allExercises = library.getAllExercises()
        val blankResults = library.searchExercises("")

        // Then
        assertEquals(allExercises.size, blankResults.size)
    }

    @Test
    fun `library covers all major muscle groups`() {
        // Then
        MuscleGroup.entries.forEach { muscle ->
            val exercises = library.getExercisesByMuscleGroup(muscle)
            assertTrue(
                "Should have exercises for $muscle",
                exercises.isNotEmpty()
            )
        }
    }

    @Test
    fun `library covers all exercise categories`() {
        // Then
        ExerciseCategory.entries.forEach { category ->
            val exercises = library.getExercisesByCategory(category)
            assertTrue(
                "Should have exercises for $category",
                exercises.isNotEmpty()
            )
        }
    }
}
