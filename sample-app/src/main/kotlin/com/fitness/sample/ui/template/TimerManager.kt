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

    private val _isCountingDown = MutableStateFlow(false)
    val isCountingDown: StateFlow<Boolean> = _isCountingDown.asStateFlow()

    private var restTimerJob: Job? = null

    /** Actual seconds elapsed during the current/last rest, tracked independently of adjustments. */
    private var restElapsedSeconds = 0

    /** Invoked with the actual elapsed rest seconds once the current rest ends (naturally or via skip). */
    private var onRestEnded: ((Int) -> Unit)? = null

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

    /**
     * Starts the rest timer. [onRestEnded] fires with the actual elapsed rest seconds
     * once rest ends, whether by counting down naturally, being skipped, or being
     * superseded by a new rest starting before it finished.
     */
    fun startRestTimer(seconds: Int, onRestEnded: ((Int) -> Unit)? = null) {
        if (seconds <= 0) {
            onRestEnded?.invoke(0)
            return
        }

        // Flush any still-running previous rest before starting a new one.
        if (restTimerJob?.isActive == true) {
            this.onRestEnded?.invoke(restElapsedSeconds)
        }
        restTimerJob?.cancel()

        this.onRestEnded = onRestEnded
        restElapsedSeconds = 0
        _restTimeRemaining.value = seconds
        _isResting.value = true

        restTimerJob = scope.launch {
            while (_restTimeRemaining.value > 0) {
                delay(1000)
                _restTimeRemaining.value--
                restElapsedSeconds++
                _isCountingDown.value = _restTimeRemaining.value in 0..5
            }
            delay(500) // Keep 0 visible and give time for the final beep
            _isResting.value = false
            _isCountingDown.value = false
            this@TimerManager.onRestEnded?.invoke(restElapsedSeconds)
            this@TimerManager.onRestEnded = null
        }
    }

    fun skipRest() {
        val wasActive = restTimerJob?.isActive == true
        restTimerJob?.cancel()
        _restTimeRemaining.value = 0
        _isResting.value = false
        _isCountingDown.value = false
        if (wasActive) {
            onRestEnded?.invoke(restElapsedSeconds)
        }
        onRestEnded = null
    }

    /**
     * Adjusts the currently running rest countdown by [deltaSeconds] (positive to add,
     * negative to subtract). Reducing remaining time to zero or below ends the rest.
     */
    fun adjustRestTime(deltaSeconds: Int) {
        if (!_isResting.value) return
        val newValue = _restTimeRemaining.value + deltaSeconds
        if (newValue <= 0) {
            skipRest()
        } else {
            _restTimeRemaining.value = newValue
            _isCountingDown.value = newValue in 0..5
        }
    }

    fun cancelAll() {
        workoutTimerJob?.cancel()
        restTimerJob?.cancel()
    }
}
