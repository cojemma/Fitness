package com.fitness.sdk.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing a workout in the database.
 */
@Entity(
    tableName = "workouts",
    indices = [
        Index(value = ["startTime"]),
        Index(value = ["type"]),
        Index(value = ["templateId"])
    ]
)
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: String,
    val templateId: Long?,
    val startTime: Long,
    val endTime: Long?,
    val durationMinutes: Int,
    val caloriesBurned: Int,
    val notes: String?,
    val createdAt: Long,
    val updatedAt: Long
)
