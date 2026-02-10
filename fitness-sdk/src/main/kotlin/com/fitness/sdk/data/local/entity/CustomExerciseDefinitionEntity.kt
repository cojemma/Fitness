package com.fitness.sdk.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for storing user-created custom exercise definitions.
 */
@Entity(tableName = "custom_exercise_definitions")
data class CustomExerciseDefinitionEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val category: String,
    val primaryMuscle: String,
    val secondaryMuscles: String,
    val description: String?,
    val instructions: String?,
    val isTimeBased: Boolean = false,
    val defaultSets: Int? = 3,
    val defaultReps: Int? = 10,
    val defaultDurationSeconds: Int? = null,
    val createdAt: Long = System.currentTimeMillis()
)
