package com.fitness.sample.ui.template

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitness.sdk.FitnessSDK
import com.fitness.sdk.domain.model.Exercise
import com.fitness.sdk.domain.model.ExerciseDefinition
import com.fitness.sdk.domain.model.LastSessionData
import com.fitness.sdk.domain.model.Workout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing an active workout session.
 * Delegates logic to TimerManager and SessionStateManager.
 */
class ActiveWorkoutViewModel : ViewModel() {

    private val templateManager = FitnessSDK.getTemplateManager()
    private val workoutManager = FitnessSDK.getWorkoutManager()

    // MANAGERS
    private val timerManager = TimerManager(viewModelScope)
    private val sessionStateManager = SessionStateManager()

    // DELEGATED STATE
    val workout: StateFlow<Workout?> = sessionStateManager.workout
    val lastSessionData: StateFlow<LastSessionData?> = sessionStateManager.lastSessionData
    val currentExerciseIndex: StateFlow<Int> = sessionStateManager.currentExerciseIndex
    val currentSetIndex: StateFlow<Int> = sessionStateManager.currentSetIndex
    val completedSets: StateFlow<Map<Int, List<SetLogEntry>>> = sessionStateManager.completedSets

    val restTimeRemaining: StateFlow<Int> = timerManager.restTimeRemaining
    val isResting: StateFlow<Boolean> = timerManager.isResting
    val elapsedSeconds: StateFlow<Int> = timerManager.elapsedSeconds

    // UI state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _workoutCompleted = MutableStateFlow(false)
    val workoutCompleted: StateFlow<Boolean> = _workoutCompleted.asStateFlow()

    private val _savedWorkoutId = MutableStateFlow<Long?>(null)
    val savedWorkoutId: StateFlow<Long?> = _savedWorkoutId.asStateFlow()

    private val _templateSaved = MutableStateFlow(false)
    val templateSaved: StateFlow<Boolean> = _templateSaved.asStateFlow()

    fun startWorkout(templateId: Long) {
        viewModelScope.launch {
            _isLoading.value = true

            // Get last session data first
            templateManager.getLastSessionData(templateId)
                .onSuccess { data ->
                    sessionStateManager.setLastSessionData(data)
                }

            // Start workout from template
            templateManager.startWorkout(templateId, preloadLastSession = true)
                .onSuccess { workout ->
                    sessionStateManager.setWorkout(workout)
                    timerManager.startWorkoutTimer()
                }
                .onFailure { e ->
                    _error.value = e.message ?: "Failed to start workout"
                }

            _isLoading.value = false
        }
    }

    fun getCurrentExercise(): Exercise? = sessionStateManager.getCurrentExercise()
    fun getTargetReps(): Int = sessionStateManager.getTargetReps()
    fun getTargetWeight(): Float? = sessionStateManager.getTargetWeight()
    fun getLastSetData(exerciseName: String, setNumber: Int): String? = 
        sessionStateManager.getLastSetData(exerciseName, setNumber)

    fun logSet(reps: Int, weight: Float?) {
        val shouldRest = sessionStateManager.logSet(reps, weight)
        if (shouldRest) {
            val exercise = sessionStateManager.getCurrentExercise()
            if (exercise != null) {
                timerManager.startRestTimer(exercise.restSeconds)
            }
        } else {
            // Moved to next exercise without rest (or last set of last exercise)
            timerManager.skipRest() 
        }
    }

    fun skipRest() {
        timerManager.skipRest()
    }

    fun goToExercise(index: Int) {
        sessionStateManager.goToExercise(index)
        timerManager.skipRest()
    }

    fun reorderExercises(fromIndex: Int, toIndex: Int) {
        sessionStateManager.reorderExercises(fromIndex, toIndex)
    }

    fun previousExercise() {
        sessionStateManager.previousExercise()
        timerManager.skipRest() // Cancel rest if navigating
    }

    fun nextExercise() {
        sessionStateManager.nextExercise()
        timerManager.skipRest() // Cancel rest if navigating
    }

    fun finishWorkout() {
        viewModelScope.launch {
            timerManager.stopWorkoutTimer()
            timerManager.skipRest()

            val workout = workout.value ?: return@launch
            val setsMap = completedSets.value

            // Build exercises with actual logged set data
            // Note: This logic could move to a specialized mapper or helper if it grows
            val updatedExercises = workout.exercises.mapIndexed { index, exercise ->
                val loggedSets = setsMap[index] ?: emptyList()
                
                val setRecords = loggedSets.map { entry ->
                    com.fitness.sdk.domain.model.ExerciseSet(
                        setNumber = entry.setNumber,
                        reps = entry.reps,
                        weight = entry.weight,
                        isWarmupSet = false,
                        completedAt = System.currentTimeMillis()
                    )
                }

                val avgReps = if (loggedSets.isNotEmpty()) {
                    loggedSets.map { it.reps }.average().toInt()
                } else exercise.reps
                
                val avgWeight = if (loggedSets.isNotEmpty() && loggedSets.any { it.weight != null }) {
                    loggedSets.mapNotNull { it.weight }.average().toFloat()
                } else exercise.weight

                exercise.copy(
                    sets = loggedSets.size.coerceAtLeast(1),
                    reps = avgReps,
                    weight = avgWeight,
                    setRecords = setRecords
                )
            }

            val finishedWorkout = workout.copy(
                exercises = updatedExercises,
                durationMinutes = (elapsedSeconds.value / 60).coerceAtLeast(1),
                endTime = System.currentTimeMillis()
            )

            workoutManager.createWorkout(finishedWorkout)
                .onSuccess { workoutId ->
                    _savedWorkoutId.value = workoutId
                    _workoutCompleted.value = true
                }
                .onFailure { e ->
                    _error.value = e.message ?: "Failed to save workout"
                }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun saveAsTemplate(name: String, description: String? = null) {
        val workoutId = _savedWorkoutId.value ?: return
        viewModelScope.launch {
            templateManager.saveWorkoutAsTemplate(workoutId, name, description)
                .onSuccess {
                    _templateSaved.value = true
                }
                .onFailure { e ->
                    _error.value = e.message ?: "Failed to save as template"
                }
        }
    }

    fun updateOriginalTemplate() {
        val workoutId = _savedWorkoutId.value ?: return
        val workout = workout.value ?: return
        val templateId = workout.templateId ?: return
        
        viewModelScope.launch {
            templateManager.updateTemplateFromWorkout(templateId, workoutId)
                .onSuccess {
                    _templateSaved.value = true
                }
                .onFailure { e ->
                    _error.value = e.message ?: "Failed to update template"
                }
        }
    }

    fun getOriginalTemplateId(): Long? {
        return workout.value?.templateId
    }

    /**
     * Adds an exercise to the current active workout.
     * Converts ExerciseDefinition to Exercise and adds it to the session state.
     */
    fun addExercise(exerciseDefinition: ExerciseDefinition) {
        val exercise = exerciseDefinition.toExercise()
        sessionStateManager.addExercise(exercise)
    }

    override fun onCleared() {
        super.onCleared()
        timerManager.cancelAll()
    }
}
