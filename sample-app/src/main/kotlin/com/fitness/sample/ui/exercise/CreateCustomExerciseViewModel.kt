package com.fitness.sample.ui.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitness.sdk.FitnessSDK
import com.fitness.sdk.domain.model.ExerciseCategory
import com.fitness.sdk.domain.model.ExerciseDefinition
import com.fitness.sdk.domain.model.MuscleGroup
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class CreateCustomExerciseState(
    val name: String = "",
    val category: ExerciseCategory = ExerciseCategory.STRENGTH,
    val primaryMuscle: MuscleGroup = MuscleGroup.CHEST,
    val secondaryMuscles: Set<MuscleGroup> = emptySet(),
    val description: String = "",
    val instructions: String = "",
    val isTimeBased: Boolean = false,
    val defaultSets: String = "3",
    val defaultReps: String = "10",
    val defaultDurationSeconds: String = "30",
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val isSaved: Boolean = false
)

class CreateCustomExerciseViewModel : ViewModel() {

    private val exerciseLibrary = FitnessSDK.getExerciseLibraryManager()

    private val _state = MutableStateFlow(CreateCustomExerciseState())
    val state: StateFlow<CreateCustomExerciseState> = _state.asStateFlow()

    fun onNameChange(name: String) {
        _state.value = _state.value.copy(name = name, errorMessage = null)
    }

    fun onCategoryChange(category: ExerciseCategory) {
        _state.value = _state.value.copy(category = category)
    }

    fun onPrimaryMuscleChange(muscle: MuscleGroup) {
        _state.value = _state.value.copy(
            primaryMuscle = muscle,
            secondaryMuscles = _state.value.secondaryMuscles - muscle
        )
    }

    fun onSecondaryMuscleToggle(muscle: MuscleGroup) {
        val current = _state.value.secondaryMuscles
        _state.value = _state.value.copy(
            secondaryMuscles = if (muscle in current) current - muscle else current + muscle
        )
    }

    fun onDescriptionChange(description: String) {
        _state.value = _state.value.copy(description = description)
    }

    fun onInstructionsChange(instructions: String) {
        _state.value = _state.value.copy(instructions = instructions)
    }

    fun onTimeBasedChange(isTimeBased: Boolean) {
        _state.value = _state.value.copy(isTimeBased = isTimeBased)
    }

    fun onDefaultSetsChange(sets: String) {
        _state.value = _state.value.copy(defaultSets = sets)
    }

    fun onDefaultRepsChange(reps: String) {
        _state.value = _state.value.copy(defaultReps = reps)
    }

    fun onDefaultDurationChange(duration: String) {
        _state.value = _state.value.copy(defaultDurationSeconds = duration)
    }

    fun save() {
        val s = _state.value
        if (s.name.isBlank()) {
            _state.value = s.copy(errorMessage = "Exercise name cannot be empty")
            return
        }

        _state.value = s.copy(isSaving = true, errorMessage = null)

        val exercise = ExerciseDefinition(
            id = "custom_${UUID.randomUUID()}",
            name = s.name.trim(),
            category = s.category,
            primaryMuscle = s.primaryMuscle,
            secondaryMuscles = s.secondaryMuscles.toList(),
            description = s.description.takeIf { it.isNotBlank() },
            instructions = s.instructions.takeIf { it.isNotBlank() },
            isTimeBased = s.isTimeBased,
            defaultSets = s.defaultSets.toIntOrNull() ?: 3,
            defaultReps = s.defaultReps.toIntOrNull() ?: 10,
            defaultDurationSeconds = s.defaultDurationSeconds.toIntOrNull(),
            isCustom = true
        )

        viewModelScope.launch {
            exerciseLibrary.saveCustomExercise(exercise)
                .onSuccess {
                    _state.value = _state.value.copy(isSaving = false, isSaved = true)
                }
                .onFailure { e ->
                    _state.value = _state.value.copy(
                        isSaving = false,
                        errorMessage = e.message ?: "Failed to save exercise"
                    )
                }
        }
    }
}
