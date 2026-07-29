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
    val isCountingDown: StateFlow<Boolean> = timerManager.isCountingDown
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

    /** The finished workout with set records, kept in memory to avoid re-fetching from DB. */
    private var finishedWorkoutCache: com.fitness.sdk.domain.model.Workout? = null

    private val _templateSaved = MutableStateFlow(false)
    val templateSaved: StateFlow<Boolean> = _templateSaved.asStateFlow()

    /**
     * Guards against re-loading the workout when the screen re-enters composition
     * (e.g. returning from the exercise picker). Re-running startWorkout() would reset
     * the workout to the template's exercise list while completedSets still maps to the
     * previous order, scrambling logged sets and progress.
     */
    private var workoutStarted = false

    fun startWorkout(templateId: Long) {
        if (workoutStarted) {
            // Already loaded; the screen merely re-entered composition. Keep current session state.
            _isLoading.value = false
            return
        }
        workoutStarted = true
        viewModelScope.launch {
            _isLoading.value = true

            // Start workout from template (internally uses last session for weight/rep population)
            templateManager.startWorkout(templateId, preloadLastSession = true)
                .onSuccess { workout ->
                    sessionStateManager.setWorkout(workout)
                    timerManager.startWorkoutTimer()

                    // Load last session data in background for "Last:" display hints.
                    // Non-blocking — the workout UI shows immediately.
                    launch {
                        templateManager.getLastSessionData(templateId)
                            .onSuccess { data ->
                                sessionStateManager.setLastSessionData(data)
                            }
                    }
                }
                .onFailure { e ->
                    workoutStarted = false
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
        val exerciseIndex = sessionStateManager.currentExerciseIndex.value
        val currentExercise = sessionStateManager.getCurrentExercise()
        sessionStateManager.logSet(reps, weight)
        if (currentExercise != null) {
            timerManager.startRestTimer(currentExercise.restSeconds) { actualRestSeconds ->
                sessionStateManager.recordRestForSet(exerciseIndex, actualRestSeconds)
            }
        }
    }

    fun skipRest() {
        timerManager.skipRest()
    }

    fun adjustRestTime(deltaSeconds: Int) {
        timerManager.adjustRestTime(deltaSeconds)
    }

    fun goToExercise(index: Int) {
        sessionStateManager.goToExercise(index)
    }

    fun reorderExercises(fromIndex: Int, toIndex: Int) {
        sessionStateManager.reorderExercises(fromIndex, toIndex)
    }

    fun previousExercise() {
        sessionStateManager.previousExercise()
    }

    fun nextExercise() {
        sessionStateManager.nextExercise()
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
                        completedAt = System.currentTimeMillis(),
                        restSeconds = entry.restSeconds
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
                    finishedWorkoutCache = finishedWorkout
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
        val cachedWorkout = finishedWorkoutCache
        val templateId = workout.value?.templateId ?: return

        viewModelScope.launch {
            val result = if (cachedWorkout != null) {
                // Fast path: use in-memory workout, avoids re-fetching from DB
                templateManager.updateTemplateFromWorkout(templateId, cachedWorkout)
            } else {
                // Fallback: fetch from DB (should not normally happen)
                val workoutId = _savedWorkoutId.value ?: return@launch
                templateManager.updateTemplateFromWorkout(templateId, workoutId)
            }
            result
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

    /**
     * Replaces the exercise at [index] with [exerciseDefinition] (e.g. swapping "Barbell Bench
     * Press" for "Dumbbell Bench Press" mid-workout). Sets/reps/weight are prefilled from the
     * new exercise's most recent performance; if it's never been performed before, falls back
     * to the sets/reps/weight of the exercise being replaced.
     */
    fun replaceExercise(index: Int, exerciseDefinition: ExerciseDefinition) {
        val originalExercise = sessionStateManager.workout.value?.exercises?.getOrNull(index)
        viewModelScope.launch {
            val lastPerformance = workoutManager.getLastExercisePerformance(exerciseDefinition.name)
                .getOrNull()

            val newExercise = when {
                lastPerformance != null -> exerciseDefinition.toExercise(
                    sets = lastPerformance.sets,
                    reps = lastPerformance.reps,
                    weight = lastPerformance.weight,
                    durationSeconds = lastPerformance.durationSeconds,
                    restSeconds = originalExercise?.restSeconds ?: 60
                )
                originalExercise != null -> exerciseDefinition.toExercise(
                    sets = originalExercise.sets,
                    reps = originalExercise.reps,
                    weight = originalExercise.weight,
                    durationSeconds = originalExercise.durationSeconds,
                    restSeconds = originalExercise.restSeconds
                )
                else -> exerciseDefinition.toExercise()
            }

            sessionStateManager.replaceExercise(index, newExercise)
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerManager.cancelAll()
    }
}
