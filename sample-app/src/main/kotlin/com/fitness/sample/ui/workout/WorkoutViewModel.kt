package com.fitness.sample.ui.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitness.sdk.FitnessSDK
import com.fitness.sdk.domain.model.Exercise
import com.fitness.sdk.domain.model.Workout
import com.fitness.sdk.domain.model.WorkoutType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WorkoutViewModel : ViewModel() {

    private val workoutManager = FitnessSDK.getWorkoutManager()

    private val _uiState = MutableStateFlow(WorkoutUiState())
    val uiState: StateFlow<WorkoutUiState> = _uiState.asStateFlow()

    private val _exercises = MutableStateFlow<List<Exercise>>(emptyList())
    val exercises: StateFlow<List<Exercise>> = _exercises.asStateFlow()

    private val _savedWorkoutId = MutableStateFlow<Long?>(null)
    val savedWorkoutId: StateFlow<Long?> = _savedWorkoutId.asStateFlow()

    // Track if we've already loaded a workout to prevent overwriting user changes
    private var loadedWorkoutId: Long? = null

    fun loadWorkout(workoutId: Long) {
        // Prevent reloading if we've already loaded this workout
        // This preserves user's changes (like added exercises) when navigating back
        if (loadedWorkoutId == workoutId) {
            return
        }

        viewModelScope.launch {
            workoutManager.getWorkout(workoutId)
                .onSuccess { workout ->
                    workout?.let {
                        _uiState.value = WorkoutUiState(
                            name = it.name,
                            type = it.type,
                            durationMinutes = it.durationMinutes.toString(),
                            caloriesBurned = it.caloriesBurned.toString(),
                            notes = it.notes ?: "",
                            isEditing = true,
                            workoutId = it.id
                        )
                        _exercises.value = it.exercises
                        loadedWorkoutId = workoutId
                    }
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(error = e.message)
                }
        }
    }

    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }

    fun updateType(type: WorkoutType) {
        _uiState.value = _uiState.value.copy(type = type)
    }

    fun updateDuration(duration: String) {
        _uiState.value = _uiState.value.copy(durationMinutes = duration)
    }

    fun updateCalories(calories: String) {
        _uiState.value = _uiState.value.copy(caloriesBurned = calories)
    }

    fun updateNotes(notes: String) {
        _uiState.value = _uiState.value.copy(notes = notes)
    }

    fun addExercise(exercise: Exercise) {
        _exercises.value = _exercises.value + exercise
    }

    fun removeExercise(index: Int) {
        _exercises.value = _exercises.value.toMutableList().apply {
            removeAt(index)
        }
    }

    fun saveWorkout() {
        val state = _uiState.value

        if (state.name.isBlank()) {
            _uiState.value = state.copy(error = "Please enter a workout name")
            return
        }

        val duration = state.durationMinutes.toIntOrNull() ?: 0
        val calories = state.caloriesBurned.toIntOrNull() ?: 0

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true)

            val workout = Workout(
                id = state.workoutId ?: 0,
                name = state.name,
                type = state.type,
                startTime = System.currentTimeMillis(),
                durationMinutes = duration,
                caloriesBurned = calories,
                exercises = _exercises.value,
                notes = state.notes.ifBlank { null }
            )

            val result = if (state.isEditing) {
                workoutManager.updateWorkout(workout).map { state.workoutId!! }
            } else {
                workoutManager.createWorkout(workout)
            }

            result
                .onSuccess { id ->
                    _savedWorkoutId.value = id
                    _uiState.value = state.copy(isLoading = false)
                }
                .onFailure { e ->
                    _uiState.value = state.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class WorkoutUiState(
    val name: String = "",
    val type: WorkoutType = WorkoutType.STRENGTH,
    val durationMinutes: String = "",
    val caloriesBurned: String = "",
    val notes: String = "",
    val isLoading: Boolean = false,
    val isEditing: Boolean = false,
    val workoutId: Long? = null,
    val error: String? = null
)
