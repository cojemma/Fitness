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

class ExerciseLibraryViewModel : ViewModel() {

    private val exerciseLibrary = FitnessSDK.getExerciseLibraryManager()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedMuscleGroup = MutableStateFlow<MuscleGroup?>(null)
    val selectedMuscleGroup: StateFlow<MuscleGroup?> = _selectedMuscleGroup.asStateFlow()

    private val _exercises = MutableStateFlow<List<ExerciseDefinition>>(emptyList())
    val exercises: StateFlow<List<ExerciseDefinition>> = _exercises.asStateFlow()

    /** All exercises (predefined + custom), kept in sync via Flow */
    private var allExercises: List<ExerciseDefinition> = emptyList()

    init {
        loadAllExercises()
        observeExercises()
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

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        updateExerciseList()
    }

    fun onMuscleGroupSelect(muscleGroup: MuscleGroup?) {
        _selectedMuscleGroup.value = muscleGroup
        updateExerciseList()
    }

    private fun updateExerciseList() {
        val query = _searchQuery.value
        val muscleGroup = _selectedMuscleGroup.value

        _exercises.value = when {
            query.isNotBlank() -> allExercises.filter {
                it.name.lowercase().contains(query.lowercase())
            }
            muscleGroup != null -> allExercises.filter {
                it.primaryMuscle == muscleGroup || muscleGroup in it.secondaryMuscles
            }
            else -> allExercises
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
