package com.fitness.sample.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * In-process bridge between ActiveWorkoutViewModel and WorkoutForegroundService.
 * Both run in the same process, so a shared StateFlow is enough to keep the
 * notification in sync without a Binder/Messenger round-trip.
 */
object WorkoutSessionNotifier {

    data class Info(
        val isResting: Boolean,
        val restSecondsRemaining: Int,
        val exerciseName: String,
        val isUpcomingExercise: Boolean
    )

    private val _state = MutableStateFlow<Info?>(null)
    val state: StateFlow<Info?> = _state.asStateFlow()

    fun update(info: Info?) {
        _state.value = info
    }
}
