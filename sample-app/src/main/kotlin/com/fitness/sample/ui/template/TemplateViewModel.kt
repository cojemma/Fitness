package com.fitness.sample.ui.template

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitness.sdk.FitnessSDK
import com.fitness.sdk.domain.model.MuscleGroup
import com.fitness.sdk.domain.model.TemplateExercise
import com.fitness.sdk.domain.model.TemplateSet
import com.fitness.sdk.domain.model.WorkoutTemplate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for creating and editing templates.
 */
class TemplateViewModel : ViewModel() {

    private val templateManager = FitnessSDK.getTemplateManager()

    // Template state
    private val _templateId = MutableStateFlow<Long?>(null)
    val templateId: StateFlow<Long?> = _templateId.asStateFlow()

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _targetMuscleGroups = MutableStateFlow<List<MuscleGroup>>(emptyList())
    val targetMuscleGroups: StateFlow<List<MuscleGroup>> = _targetMuscleGroups.asStateFlow()

    private val _exercises = MutableStateFlow<List<TemplateExerciseState>>(emptyList())
    val exercises: StateFlow<List<TemplateExerciseState>> = _exercises.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()

    fun loadTemplate(id: Long) {
        viewModelScope.launch {
            templateManager.getTemplateById(id)
                .onSuccess { template ->
                    _templateId.value = template.id
                    _name.value = template.name
                    _description.value = template.description ?: ""
                    _targetMuscleGroups.value = template.targetMuscleGroups
                    _exercises.value = template.exercises.map { exercise ->
                        TemplateExerciseState(
                            exerciseName = exercise.exerciseName,
                            sets = exercise.sets.map { set ->
                                TemplateSetState(
                                    setNumber = set.setNumber,
                                    targetReps = set.targetReps ?: 10,
                                    targetWeight = set.targetWeight,
                                    isWarmupSet = set.isWarmupSet
                                )
                            }.toMutableList(),
                            restSeconds = exercise.restSeconds,
                            supersetGroupId = exercise.supersetGroupId
                        )
                    }
                }
                .onFailure { e ->
                    _error.value = e.message ?: "Failed to load template"
                }
        }
    }

    fun updateName(name: String) {
        _name.value = name
    }

    fun updateDescription(description: String) {
        _description.value = description
    }

    fun toggleMuscleGroup(muscleGroup: MuscleGroup) {
        val current = _targetMuscleGroups.value.toMutableList()
        if (current.contains(muscleGroup)) {
            current.remove(muscleGroup)
        } else {
            current.add(muscleGroup)
        }
        _targetMuscleGroups.value = current
    }

    fun addExercise(exerciseName: String) {
        val current = _exercises.value.toMutableList()
        current.add(
            TemplateExerciseState(
                exerciseName = exerciseName,
                sets = mutableListOf(
                    TemplateSetState(setNumber = 1, targetReps = 10, targetWeight = null, isWarmupSet = false),
                    TemplateSetState(setNumber = 2, targetReps = 10, targetWeight = null, isWarmupSet = false),
                    TemplateSetState(setNumber = 3, targetReps = 10, targetWeight = null, isWarmupSet = false)
                ),
                restSeconds = 90
            )
        )
        _exercises.value = current
    }

    fun removeExercise(index: Int) {
        val current = _exercises.value.toMutableList()
        if (index in current.indices) {
            current.removeAt(index)
            _exercises.value = current
        }
    }

    fun updateExercise(index: Int, exercise: TemplateExerciseState) {
        val current = _exercises.value.toMutableList()
        if (index in current.indices) {
            current[index] = exercise
            _exercises.value = current
        }
    }

    fun addSet(exerciseIndex: Int) {
        val exercises = _exercises.value.toMutableList()
        if (exerciseIndex in exercises.indices) {
            val exercise = exercises[exerciseIndex]
            val newSetNumber = exercise.sets.size + 1
            val newSets = exercise.sets.toMutableList()
            newSets.add(
                TemplateSetState(
                    setNumber = newSetNumber,
                    targetReps = 10,
                    targetWeight = null,
                    isWarmupSet = false
                )
            )
            exercises[exerciseIndex] = exercise.copy(sets = newSets)
            _exercises.value = exercises
        }
    }

    fun removeSet(exerciseIndex: Int, setIndex: Int) {
        val exercises = _exercises.value.toMutableList()
        if (exerciseIndex in exercises.indices) {
            val exercise = exercises[exerciseIndex]
            if (setIndex in exercise.sets.indices && exercise.sets.size > 1) {
                val newSets = exercise.sets.toMutableList()
                newSets.removeAt(setIndex)
                // Renumber sets
                newSets.forEachIndexed { i, set ->
                    newSets[i] = set.copy(setNumber = i + 1)
                }
                exercises[exerciseIndex] = exercise.copy(sets = newSets)
                _exercises.value = exercises
            }
        }
    }

    fun updateSet(exerciseIndex: Int, setIndex: Int, set: TemplateSetState) {
        val exercises = _exercises.value.toMutableList()
        if (exerciseIndex in exercises.indices) {
            val exercise = exercises[exerciseIndex]
            if (setIndex in exercise.sets.indices) {
                val newSets = exercise.sets.toMutableList()
                newSets[setIndex] = set
                exercises[exerciseIndex] = exercise.copy(sets = newSets)
                _exercises.value = exercises
            }
        }
    }

    fun updateRestSeconds(exerciseIndex: Int, restSeconds: Int) {
        val exercises = _exercises.value.toMutableList()
        if (exerciseIndex in exercises.indices) {
            exercises[exerciseIndex] = exercises[exerciseIndex].copy(restSeconds = restSeconds)
            _exercises.value = exercises
        }
    }

    fun saveTemplate() {
        if (_name.value.isBlank()) {
            _error.value = "Please enter a template name"
            return
        }
        if (_exercises.value.isEmpty()) {
            _error.value = "Please add at least one exercise"
            return
        }

        viewModelScope.launch {
            _isSaving.value = true

            val template = WorkoutTemplate(
                id = _templateId.value ?: 0,
                name = _name.value,
                description = _description.value.ifBlank { null },
                targetMuscleGroups = _targetMuscleGroups.value,
                exercises = _exercises.value.mapIndexed { index, exerciseState ->
                    TemplateExercise(
                        id = 0,
                        templateId = _templateId.value ?: 0,
                        exerciseName = exerciseState.exerciseName,
                        orderIndex = index,
                        sets = exerciseState.sets.map { setState ->
                            TemplateSet(
                                id = 0,
                                templateExerciseId = 0,
                                setNumber = setState.setNumber,
                                targetReps = setState.targetReps,
                                targetWeight = setState.targetWeight,
                                isWarmupSet = setState.isWarmupSet
                            )
                        },
                        restSeconds = exerciseState.restSeconds,
                        supersetGroupId = exerciseState.supersetGroupId
                    )
                }
            )

            templateManager.saveTemplate(template)
                .onSuccess {
                    _saveSuccess.value = true
                }
                .onFailure { e ->
                    _error.value = e.message ?: "Failed to save template"
                }

            _isSaving.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
}

/**
 * State holder for a template exercise being edited.
 */
data class TemplateExerciseState(
    val exerciseName: String,
    val sets: MutableList<TemplateSetState>,
    val restSeconds: Int = 90,
    val supersetGroupId: Int? = null
)

/**
 * State holder for a template set being edited.
 */
data class TemplateSetState(
    val setNumber: Int,
    val targetReps: Int,
    val targetWeight: Float?,
    val isWarmupSet: Boolean = false
)
