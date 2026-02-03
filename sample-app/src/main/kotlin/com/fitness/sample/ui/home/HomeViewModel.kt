package com.fitness.sample.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitness.sdk.FitnessSDK
import com.fitness.sdk.domain.model.Workout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class HomeViewModel : ViewModel() {

    private val workoutManager = FitnessSDK.getWorkoutManager()

    val workouts: StateFlow<List<Workout>> = workoutManager.observeWorkouts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun deleteWorkout(workoutId: Long) {
        viewModelScope.launch {
            workoutManager.deleteWorkout(workoutId)
                .onFailure { e ->
                    _error.value = e.message
                }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun getWeeklyStats(workouts: List<Workout>): WeeklyStats {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val weekStart = calendar.timeInMillis

        val thisWeekWorkouts = workouts.filter { it.startTime >= weekStart }

        return WeeklyStats(
            totalWorkouts = thisWeekWorkouts.size,
            totalCalories = thisWeekWorkouts.sumOf { it.caloriesBurned },
            totalMinutes = thisWeekWorkouts.sumOf { it.durationMinutes }
        )
    }
}

data class WeeklyStats(
    val totalWorkouts: Int,
    val totalCalories: Int,
    val totalMinutes: Int
)
