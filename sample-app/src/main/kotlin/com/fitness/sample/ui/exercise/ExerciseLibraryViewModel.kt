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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExerciseLibraryViewModel : ViewModel() {

    private val exerciseLibrary = FitnessSDK.getExerciseLibraryManager()
    private val workoutManager = FitnessSDK.getWorkoutManager()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedMuscleGroup = MutableStateFlow<MuscleGroup?>(null)
    val selectedMuscleGroup: StateFlow<MuscleGroup?> = _selectedMuscleGroup.asStateFlow()

    private val _exercises = MutableStateFlow<List<ExerciseDefinition>>(emptyList())
    val exercises: StateFlow<List<ExerciseDefinition>> = _exercises.asStateFlow()

    /** All exercises (predefined + custom), kept in sync via Flow */
    private var allExercises: List<ExerciseDefinition> = emptyList()

    /** Exercise name -> session count, used for default sorting (most-performed first) */
    private var sessionCounts: Map<String, Int> = emptyMap()

    /**
     * Name of the exercise currently being swapped out (if this picker was opened from the
     * "replace exercise" flow). Its primary muscle group is prioritized in sort order without
     * hiding other exercises, unlike the hard muscle-group filter chips.
     */
    private var priorityExerciseName: String? = null

    init {
        loadAllExercises()
        observeExercises()
        loadSessionCounts()
    }

    private fun loadAllExercises() {
        allExercises = exerciseLibrary.getAllExercises()
        _exercises.value = allExercises
    }

    private fun observeExercises() {
        viewModelScope.launch {
            exerciseLibrary.observeAllExercises().collect { exercises ->
                allExercises = exercises
                updateExerciseList()
            }
        }
    }

    private fun loadSessionCounts() {
        viewModelScope.launch {
            workoutManager.observeExerciseSessionCounts()
                .collect { counts ->
                    sessionCounts = counts
                    sortExercises()
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
        updateExerciseList()
    }

    fun onMuscleGroupSelect(muscleGroup: MuscleGroup?) {
        _selectedMuscleGroup.value = muscleGroup
        updateExerciseList()
    }

    /**
     * Sets the exercise being swapped out, so its primary muscle group is prioritized in the
     * (unfiltered) sort order. Pass null to clear (e.g. when the picker is used for a plain
     * add-exercise flow instead of a swap).
     */
    fun setPriorityExerciseName(exerciseName: String?) {
        priorityExerciseName = exerciseName
        updateExerciseList()
    }

    private fun updateExerciseList() {
        val query = _searchQuery.value
        val muscleGroup = _selectedMuscleGroup.value
        val priorityMuscle = priorityExerciseName?.let { name ->
            allExercises.find { it.name == name }?.primaryMuscle
        }

        val filtered = when {
            query.isNotBlank() -> allExercises.filter {
                it.name.lowercase().contains(query.lowercase())
            }
            muscleGroup != null -> allExercises.filter {
                it.primaryMuscle == muscleGroup || muscleGroup in it.secondaryMuscles
            }
            else -> allExercises
        }

        _exercises.value = when {
            muscleGroup != null && query.isBlank() -> filtered.sortedWith(
                compareByDescending<ExerciseDefinition> { it.primaryMuscle == muscleGroup }
                    .thenByDescending { sessionCounts[it.name] ?: 0 }
            )
            priorityMuscle != null && query.isBlank() -> filtered.sortedWith(
                compareByDescending<ExerciseDefinition> { it.primaryMuscle == priorityMuscle }
                    .thenByDescending { sessionCounts[it.name] ?: 0 }
            )
            else -> filtered.sortedByDescending { sessionCounts[it.name] ?: 0 }
        }
    }

    fun getExercisesByCategory(category: ExerciseCategory): List<ExerciseDefinition> {
        return exerciseLibrary.getExercisesByCategory(category)
    }

    // Group exercises by primary muscle for display
    fun getExercisesGroupedByMuscle(): Map<MuscleGroup, List<ExerciseDefinition>> {
        return _exercises.value.groupBy { it.primaryMuscle }
    }
}
