package com.fitness.sdk.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing a set within a template exercise.
 */
@Entity(
    tableName = "template_sets",
    foreignKeys = [
        ForeignKey(
            entity = TemplateExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["templateExerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["templateExerciseId"])
    ]
)
data class TemplateSetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val templateExerciseId: Long,
    val setNumber: Int,
    val targetReps: Int?,
    val targetWeight: Float?,
    val targetRpe: Float?,
    val percentageOf1RM: Float?,
    val isWarmupSet: Boolean
)
