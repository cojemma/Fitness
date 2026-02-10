package com.fitness.sample.ui.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitness.sdk.FitnessSDK
import com.fitness.sdk.domain.model.ExerciseDefinition
import com.fitness.sdk.domain.model.ExerciseHistory
import com.fitness.sdk.domain.model.MuscleGroup
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ExerciseWithHistory(
    val definition: ExerciseDefinition,
    val history: ExerciseHistory?,
    val isLoading: Boolean
)

class ExerciseListViewModel : ViewModel() {

    private val exerciseLibrary = FitnessSDK.getExerciseLibraryManager()
    private val workoutManager = FitnessSDK.getWorkoutManager()

    private val _exercises = MutableStateFlow<List<ExerciseDefinition>>(emptyList())
    val exercises: StateFlow<List<ExerciseDefinition>> = _exercises.asStateFlow()

    private val _historyCache = MutableStateFlow<Map<String, ExerciseHistory>>(emptyMap())
    val historyCache: StateFlow<Map<String, ExerciseHistory>> = _historyCache.asStateFlow()

    private val _loadingExercises = MutableStateFlow<Set<String>>(emptySet())
    val loadingExercises: StateFlow<Set<String>> = _loadingExercises.asStateFlow()

    /** Per-exercise load errors: exercise name -> error message. Cleared on retry or success. */
    private val _exerciseLoadErrors = MutableStateFlow<Map<String, String>>(emptyMap())
    val exerciseLoadErrors: StateFlow<Map<String, String>> = _exerciseLoadErrors.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedMuscleGroup = MutableStateFlow<MuscleGroup?>(null)
    val selectedMuscleGroup: StateFlow<MuscleGroup?> = _selectedMuscleGroup.asStateFlow()

    /** Exercise name -> session count, used for default sorting */
    private var sessionCounts: Map<String, Int> = emptyMap()

    init {
        loadExercises()
        loadSessionCounts()
    }

    private fun loadExercises() {
        _exercises.value = exerciseLibrary.getAllExercises()
    }

    private fun loadSessionCounts() {
        viewModelScope.launch {
            workoutManager.observeExerciseSessionCounts()
                .collect { counts ->
                    val changed = sessionCounts != counts
                    sessionCounts = counts
                    sortExercises()
                    if (changed) {
                        _historyCache.value = emptyMap()
                    }
                }
        }
    }

    private fun sortExercises() {
        _exercises.update { list ->
            list.sortedByDescending { sessionCounts[it.name] ?: 0 }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        updateFilteredExercises()
    }

    fun onMuscleGroupSelect(muscleGroup: MuscleGroup?) {
        _selectedMuscleGroup.value = muscleGroup
        updateFilteredExercises()
    }

    private fun updateFilteredExercises() {
        val query = _searchQuery.value
        val muscleGroup = _selectedMuscleGroup.value

        _exercises.value = when {
            query.isNotBlank() -> exerciseLibrary.searchExercises(query)
            muscleGroup != null -> exerciseLibrary.getExercisesByMuscleGroup(muscleGroup)
            else -> exerciseLibrary.getAllExercises()
        }.sortedByDescending { sessionCounts[it.name] ?: 0 }
    }

    fun loadExerciseHistory(exerciseName: String) {
        if (_historyCache.value.containsKey(exerciseName)) return

        viewModelScope.launch {
            _loadingExercises.update { it + exerciseName }
            _exerciseLoadErrors.update { it - exerciseName }

            workoutManager.getExerciseHistory(exerciseName)
                .onSuccess { history ->
                    _historyCache.update { it + (exerciseName to history) }
                    _exerciseLoadErrors.update { it - exerciseName }
                }
                .onFailure { e ->
                    _exerciseLoadErrors.update {
                        it + (exerciseName to (e.message ?: "Failed to load history"))
                    }
                }

            _loadingExercises.update { it - exerciseName }
        }
    }

    fun getHistoryFor(exerciseName: String): ExerciseHistory? = _historyCache.value[exerciseName]

    fun isExpanded(exerciseName: String): Boolean = _historyCache.value.containsKey(exerciseName)
}
