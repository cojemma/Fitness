package com.fitness.sdk.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.fitness.sdk.data.local.entity.TemplateExerciseEntity
import com.fitness.sdk.data.local.entity.TemplateSetEntity
import com.fitness.sdk.data.local.entity.WorkoutTemplateEntity
import com.fitness.sdk.data.local.entity.WorkoutTemplateWithExercises
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for workout template operations.
 */
@Dao
interface TemplateDao {

    // ==================== Template Operations ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: WorkoutTemplateEntity): Long

    @Update
    suspend fun updateTemplate(template: WorkoutTemplateEntity)

    @Delete
    suspend fun deleteTemplate(template: WorkoutTemplateEntity)

    @Query("DELETE FROM workout_templates WHERE id = :templateId")
    suspend fun deleteTemplateById(templateId: Long)

    @Query("SELECT * FROM workout_templates WHERE id = :templateId")
    suspend fun getTemplateById(templateId: Long): WorkoutTemplateEntity?

    @Query("SELECT * FROM workout_templates ORDER BY updatedAt DESC")
    fun observeAllTemplates(): Flow<List<WorkoutTemplateEntity>>

    @Query("SELECT * FROM workout_templates ORDER BY updatedAt DESC")
    suspend fun getAllTemplates(): List<WorkoutTemplateEntity>

    @Transaction
    @Query("SELECT * FROM workout_templates WHERE id = :templateId")
    suspend fun getTemplateWithExercises(templateId: Long): WorkoutTemplateWithExercises?

    @Transaction
    @Query("SELECT * FROM workout_templates ORDER BY updatedAt DESC")
    fun observeAllTemplatesWithExercises(): Flow<List<WorkoutTemplateWithExercises>>

    @Transaction
    @Query("SELECT * FROM workout_templates ORDER BY updatedAt DESC")
    suspend fun getAllTemplatesWithExercises(): List<WorkoutTemplateWithExercises>

    // ==================== Exercise Operations ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: TemplateExerciseEntity): Long

    @Insert
    suspend fun insertExercises(exercises: List<TemplateExerciseEntity>): List<Long>

    @Update
    suspend fun updateExercise(exercise: TemplateExerciseEntity)

    @Delete
    suspend fun deleteExercise(exercise: TemplateExerciseEntity)

    @Query("DELETE FROM template_exercises WHERE templateId = :templateId")
    suspend fun deleteExercisesByTemplateId(templateId: Long)

    @Query("SELECT * FROM template_exercises WHERE templateId = :templateId ORDER BY orderIndex")
    suspend fun getExercisesByTemplateId(templateId: Long): List<TemplateExerciseEntity>

    // ==================== Set Operations ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSet(set: TemplateSetEntity): Long

    @Insert
    suspend fun insertSets(sets: List<TemplateSetEntity>): List<Long>

    @Update
    suspend fun updateSet(set: TemplateSetEntity)

    @Delete
    suspend fun deleteSet(set: TemplateSetEntity)

    @Query("DELETE FROM template_sets WHERE templateExerciseId = :exerciseId")
    suspend fun deleteSetsByExerciseId(exerciseId: Long)

    @Query("SELECT * FROM template_sets WHERE templateExerciseId = :exerciseId ORDER BY setNumber")
    suspend fun getSetsByExerciseId(exerciseId: Long): List<TemplateSetEntity>

    // ==================== Complex Operations ====================

    /**
     * Save a complete template with all exercises and sets.
     * This is a transaction that handles the complete save operation.
     */
    @Transaction
    suspend fun saveCompleteTemplate(
        template: WorkoutTemplateEntity,
        exercises: List<TemplateExerciseEntity>,
        setsByExerciseIndex: Map<Int, List<TemplateSetEntity>>
    ): Long {
        // Insert or update template
        val templateId = insertTemplate(template)

        // Delete existing exercises and sets (cascade will delete sets)
        deleteExercisesByTemplateId(templateId)

        // Insert all exercises and collect their generated IDs
        val exercisesWithTemplateId = exercises.map { it.copy(templateId = templateId) }
        val exerciseIds = insertExercises(exercisesWithTemplateId)

        // Batch all sets into a single insert
        val allSets = mutableListOf<TemplateSetEntity>()
        exerciseIds.forEachIndexed { index, exerciseId ->
            setsByExerciseIndex[index]?.let { sets ->
                allSets.addAll(sets.map { it.copy(templateExerciseId = exerciseId) })
            }
        }
        if (allSets.isNotEmpty()) {
            insertSets(allSets)
        }

        return templateId
    }
}
