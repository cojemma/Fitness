package com.fitness.sample.ui.template

import com.fitness.sdk.domain.model.Exercise
import com.fitness.sdk.domain.model.LastSessionData
import com.fitness.sdk.domain.model.Workout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages the state of the active workout session, including:
 * - Current workout and exercise
 * - Navigation between exercises
 * - Logging sets
 * - Tracking completed sets
 */
class SessionStateManager {

    private val _workout = MutableStateFlow<Workout?>(null)
    val workout: StateFlow<Workout?> = _workout.asStateFlow()

    /**
     * Exercises added before the workout is fully loaded.
     * This can happen if the user opens the picker while the template workout is still starting.
     */
    private val pendingExercises = mutableListOf<Exercise>()

    private val _currentExerciseIndex = MutableStateFlow(0)
    val currentExerciseIndex: StateFlow<Int> = _currentExerciseIndex.asStateFlow()

    private val _currentSetIndex = MutableStateFlow(0)
    val currentSetIndex: StateFlow<Int> = _currentSetIndex.asStateFlow()

    private val _completedSets = MutableStateFlow<Map<Int, List<SetLogEntry>>>(emptyMap())
    val completedSets: StateFlow<Map<Int, List<SetLogEntry>>> = _completedSets.asStateFlow()

    private val _lastSessionData = MutableStateFlow<LastSessionData?>(null)
    val lastSessionData: StateFlow<LastSessionData?> = _lastSessionData.asStateFlow()

    /**
     * Sets the workout (e.g. when template load completes).
     * Merges in any exercises added during load to avoid race with addExercise():
     * - Exercises in pendingExercises (added while workout was null)
     * - Exercises already in current state but not in incoming template (added after UI showed, before startWorkout() returned)
     */
    fun setWorkout(workout: Workout) {
        val templateNames = workout.exercises.map { it.name }.toSet()
        val current = _workout.value
        val addedDuringLoad = if (current != null) {
            current.exercises.filter { it.name !in templateNames }
        } else {
            emptyList()
        }
        val pending = pendingExercises.toList()
        pendingExercises.clear()
        val allExtra = pending + addedDuringLoad
        val merged = if (allExtra.isEmpty()) workout else workout.copy(exercises = workout.exercises + allExtra)
        _workout.value = merged
    }

    fun setLastSessionData(data: LastSessionData?) {
        _lastSessionData.value = data
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

    /**
     * Logs a completed set.
     * @return true if the user should rest (i.e., more sets remain in this exercise),
     *         false if moved to next exercise or finished.
     */
    fun logSet(reps: Int, weight: Float?): Boolean {
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

        // Move logic
        val exercise = getCurrentExercise()
        if (exercise != null && setIndex + 1 < exercise.sets) {
            _currentSetIndex.value++
            return true // Should rest
        } else {
            nextExercise()
            return false // Moved to next exercise
        }
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

    fun getLastSetData(exerciseName: String, setNumber: Int): String? {
        val lastData = _lastSessionData.value?.getSetData(exerciseName, setNumber)
        return lastData?.getDisplayString()
    }

    /**
     * Jumps to a specific exercise by index.
     * Resumes at the next uncompleted set if the exercise was partially done.
     */
    fun goToExercise(index: Int) {
        val workout = _workout.value ?: return
        if (index in workout.exercises.indices) {
            _currentExerciseIndex.value = index
            val completedCount = _completedSets.value[index]?.size ?: 0
            val totalSets = workout.exercises[index].sets
            _currentSetIndex.value = completedCount.coerceAtMost(totalSets - 1)
        }
    }

    /**
     * Reorders exercises by moving one from [fromIndex] to [toIndex].
     * Remaps completedSets keys and adjusts currentExerciseIndex to follow the viewed exercise.
     */
    fun reorderExercises(fromIndex: Int, toIndex: Int) {
        val currentWorkout = _workout.value ?: return
        val exercises = currentWorkout.exercises.toMutableList()
        if (fromIndex !in exercises.indices || toIndex !in exercises.indices || fromIndex == toIndex) return

        val movedExercise = exercises.removeAt(fromIndex)
        exercises.add(toIndex, movedExercise)
        _workout.value = currentWorkout.copy(exercises = exercises)

        // Remap completedSets: build old-index-to-new-index mapping
        val oldSets = _completedSets.value
        val newSets = mutableMapOf<Int, List<SetLogEntry>>()
        for (newIdx in exercises.indices) {
            val oldIdx = when {
                newIdx == toIndex -> fromIndex
                fromIndex < toIndex && newIdx in fromIndex until toIndex -> newIdx + 1
                fromIndex > toIndex && newIdx in (toIndex + 1)..fromIndex -> newIdx - 1
                else -> newIdx
            }
            oldSets[oldIdx]?.let { newSets[newIdx] = it }
        }
        _completedSets.value = newSets

        // Adjust currentExerciseIndex to follow the exercise the user was viewing
        val currentIdx = _currentExerciseIndex.value
        val newCurrentIdx = when {
            currentIdx == fromIndex -> toIndex
            fromIndex < toIndex && currentIdx in (fromIndex + 1)..toIndex -> currentIdx - 1
            fromIndex > toIndex && currentIdx in toIndex until fromIndex -> currentIdx + 1
            else -> currentIdx
        }
        _currentExerciseIndex.value = newCurrentIdx
    }

    /**
     * Adds an exercise to the current workout.
     * The exercise is appended to the end of the exercise list.
     */
    fun addExercise(exercise: Exercise) {
        val currentWorkout = _workout.value
        if (currentWorkout == null) {
            pendingExercises.add(exercise)
            return
        }

        _workout.value = currentWorkout.copy(exercises = currentWorkout.exercises + exercise)
    }
}
