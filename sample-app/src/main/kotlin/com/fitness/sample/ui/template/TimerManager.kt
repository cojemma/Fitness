package com.fitness.sample.ui.template

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Manages workout and rest timers.
 */
class TimerManager(private val scope: CoroutineScope) {

    // Rest timer state
    private val _restTimeRemaining = MutableStateFlow(0)
    val restTimeRemaining: StateFlow<Int> = _restTimeRemaining.asStateFlow()

    private val _isResting = MutableStateFlow(false)
    val isResting: StateFlow<Boolean> = _isResting.asStateFlow()

    private var restTimerJob: Job? = null

    // Workout timer state
    private val _elapsedSeconds = MutableStateFlow(0)
    val elapsedSeconds: StateFlow<Int> = _elapsedSeconds.asStateFlow()

    private var workoutTimerJob: Job? = null

    fun startWorkoutTimer() {
        workoutTimerJob?.cancel()
        workoutTimerJob = scope.launch {
            while (true) {
                delay(1000)
                _elapsedSeconds.value++
            }
        }
    }

    fun stopWorkoutTimer() {
        workoutTimerJob?.cancel()
    }

    fun startRestTimer(seconds: Int) {
        if (seconds <= 0) return
        
        restTimerJob?.cancel()
        _restTimeRemaining.value = seconds
        _isResting.value = true

        restTimerJob = scope.launch {
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

    fun cancelAll() {
        workoutTimerJob?.cancel()
        restTimerJob?.cancel()
    }
}
