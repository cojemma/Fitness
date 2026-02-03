package com.fitness.sdk.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Data class representing a template exercise with its sets.
 */
data class TemplateExerciseWithSets(
    @Embedded
    val exercise: TemplateExerciseEntity,
    
    @Relation(
        parentColumn = "id",
        entityColumn = "templateExerciseId"
    )
    val sets: List<TemplateSetEntity>
)

/**
 * Data class representing a complete workout template with all exercises and sets.
 */
data class WorkoutTemplateWithExercises(
    @Embedded
    val template: WorkoutTemplateEntity,
    
    @Relation(
        entity = TemplateExerciseEntity::class,
        parentColumn = "id",
        entityColumn = "templateId"
    )
    val exercisesWithSets: List<TemplateExerciseWithSets>
)
