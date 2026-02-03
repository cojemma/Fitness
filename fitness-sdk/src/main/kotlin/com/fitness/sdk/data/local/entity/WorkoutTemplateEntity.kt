package com.fitness.sdk.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing a workout template in the database.
 */
@Entity(
    tableName = "workout_templates",
    indices = [
        Index(value = ["name"]),
        Index(value = ["createdAt"])
    ]
)
data class WorkoutTemplateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String?,
    val targetMuscleGroups: String, // JSON array of muscle group names
    val estimatedDurationMinutes: Int,
    val createdAt: Long,
    val updatedAt: Long,
    val version: Int
)
