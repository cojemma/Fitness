package com.fitness.sample.ui.template

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitness.sdk.FitnessSDK
import com.fitness.sdk.domain.model.Exercise
import com.fitness.sdk.domain.model.LastSessionData
import com.fitness.sdk.domain.model.Workout
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing an active workout session.
 */
class ActiveWorkoutViewModel : ViewModel() {

    private val templateManager = FitnessSDK.getTemplateManager()
    private val workoutManager = FitnessSDK.getWorkoutManager()

    // Workout state
    private val _workout = MutableStateFlow<Workout?>(null)
    val workout: StateFlow<Workout?> = _workout.asStateFlow()

    private val _lastSessionData = MutableStateFlow<LastSessionData?>(null)
    val lastSessionData: StateFlow<LastSessionData?> = _lastSessionData.asStateFlow()

    private val _currentExerciseIndex = MutableStateFlow(0)
    val currentExerciseIndex: StateFlow<Int> = _currentExerciseIndex.asStateFlow()

    private val _currentSetIndex = MutableStateFlow(0)
    val currentSetIndex: StateFlow<Int> = _currentSetIndex.asStateFlow()

    // Set logging state per exercise
    private val _completedSets = MutableStateFlow<Map<Int, List<SetLogEntry>>>(emptyMap())
    val completedSets: StateFlow<Map<Int, List<SetLogEntry>>> = _completedSets.asStateFlow()

    // Rest timer
    private val _restTimeRemaining = MutableStateFlow(0)
    val restTimeRemaining: StateFlow<Int> = _restTimeRemaining.asStateFlow()

    private val _isResting = MutableStateFlow(false)
    val isResting: StateFlow<Boolean> = _isResting.asStateFlow()

    private var restTimerJob: Job? = null

    // Workout timer
    private val _elapsedSeconds = MutableStateFlow(0)
    val elapsedSeconds: StateFlow<Int> = _elapsedSeconds.asStateFlow()

    private var workoutTimerJob: Job? = null

    // UI state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _workoutCompleted = MutableStateFlow(false)
    val workoutCompleted: StateFlow<Boolean> = _workoutCompleted.asStateFlow()

    fun startWorkout(templateId: Long) {
        viewModelScope.launch {
            _isLoading.value = true

            // Get last session data first
            templateManager.getLastSessionData(templateId)
                .onSuccess { data ->
                    _lastSessionData.value = data
                }

            // Start workout from template
            templateManager.startWorkout(templateId, preloadLastSession = true)
                .onSuccess { workout ->
                    _workout.value = workout
                    startWorkoutTimer()
                }
                .onFailure { e ->
                    _error.value = e.message ?: "Failed to start workout"
                }

            _isLoading.value = false
        }
    }

    private fun startWorkoutTimer() {
        workoutTimerJob?.cancel()
        workoutTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _elapsedSeconds.value++
            }
        }
    }

    fun getCurrentExercise(): Exercise? {
        val workout = _workout.value ?: return null
        val index = _currentExerciseIndex.value
        return workout.exercises.getOrNull(index)
    }

    fun getTargetReps(): Int {
        return getCurrentExercise()?.reps ?: 10
    }

    fun getTargetWeight(): Float? {
        return getCurrentExercise()?.weight
    }

    fun getLastSetData(exerciseName: String, setNumber: Int): String? {
        val lastData = _lastSessionData.value?.getSetData(exerciseName, setNumber)
        return lastData?.getDisplayString()
    }

    fun logSet(reps: Int, weight: Float?) {
        val exerciseIndex = _currentExerciseIndex.value
        val setIndex = _currentSetIndex.value
        
        val entry = SetLogEntry(
            setNumber = setIndex + 1,
            reps = reps,
            weight = weight
        )

        val current = _completedSets.value.toMutableMap()
        val exerciseSets = current[exerciseIndex]?.toMutableList() ?: mutableListOf()
        exerciseSets.add(entry)
        current[exerciseIndex] = exerciseSets
        _completedSets.value = current

        // Move to next set or exercise
        val exercise = getCurrentExercise()
        if (exercise != null && setIndex + 1 < exercise.sets) {
            _currentSetIndex.value++
            startRestTimer(exercise.restSeconds)
        } else {
            // Move to next exercise
            nextExercise()
        }
    }

    private fun startRestTimer(seconds: Int) {
        if (seconds <= 0) return
        
        restTimerJob?.cancel()
        _restTimeRemaining.value = seconds
        _isResting.value = true

        restTimerJob = viewModelScope.launch {
            while (_restTimeRemaining.value > 0) {
                delay(1000)
                _restTimeRemaining.value--
            }
            _isResting.value = false
        }
    }

    fun skipRest() {
        restTimerJob?.cancel()
        _restTimeRemaining.value = 0
        _isResting.value = false
    }

    fun previousExercise() {
        if (_currentExerciseIndex.value > 0) {
            _currentExerciseIndex.value--
            _currentSetIndex.value = 0
        }
    }

    fun nextExercise() {
        val workout = _workout.value ?: return
        if (_currentExerciseIndex.value < workout.exercises.size - 1) {
            _currentExerciseIndex.value++
            _currentSetIndex.value = 0
        }
    }

    fun finishWorkout() {
        viewModelScope.launch {
            workoutTimerJob?.cancel()
            restTimerJob?.cancel()

            val workout = _workout.value ?: return@launch

            // Build exercises with actual logged data
            val updatedExercises = workout.exercises.mapIndexed { index, exercise ->
                val loggedSets = _completedSets.value[index] ?: emptyList()
                
                // Calculate averages from logged data
                val avgReps = if (loggedSets.isNotEmpty()) {
                    loggedSets.map { it.reps }.average().toInt()
                } else exercise.reps
                
                val avgWeight = if (loggedSets.isNotEmpty() && loggedSets.any { it.weight != null }) {
                    loggedSets.mapNotNull { it.weight }.average().toFloat()
                } else exercise.weight

                exercise.copy(
                    sets = loggedSets.size.coerceAtLeast(1),
                    reps = avgReps,
                    weight = avgWeight
                )
            }

            val finishedWorkout = workout.copy(
                exercises = updatedExercises,
                durationMinutes = (_elapsedSeconds.value / 60).coerceAtLeast(1),
                endTime = System.currentTimeMillis()
            )

            workoutManager.createWorkout(finishedWorkout)
                .onSuccess {
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

    override fun onCleared() {
        super.onCleared()
        workoutTimerJob?.cancel()
        restTimerJob?.cancel()
    }
}

/**
 * Entry for a logged set.
 */
data class SetLogEntry(
    val setNumber: Int,
    val reps: Int,
    val weight: Float?
)
