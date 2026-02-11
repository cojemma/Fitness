package com.fitness.sample.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fitness.sample.data.CalendarViewType
import com.fitness.sample.data.PreferencesManager
import com.fitness.sdk.FitnessSDK
import com.fitness.sdk.domain.model.Workout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val workoutManager = FitnessSDK.getWorkoutManager()
    private val preferencesManager = PreferencesManager(application)

    val workouts: StateFlow<List<Workout>> = workoutManager.observeWorkouts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _calendarViewType = MutableStateFlow(preferencesManager.getCalendarViewType())
    val calendarViewType: StateFlow<CalendarViewType> = _calendarViewType.asStateFlow()

    private val _selectedDate = MutableStateFlow<LocalDate?>(null)
    val selectedDate: StateFlow<LocalDate?> = _selectedDate.asStateFlow()

    val filteredWorkouts: StateFlow<List<Workout>> = combine(
        workouts,
        _selectedDate
    ) { allWorkouts, date ->
        if (date == null) {
            allWorkouts
        } else {
            allWorkouts.filter { workout ->
                val workoutDate = Instant.ofEpochMilli(workout.startTime)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                workoutDate == date
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun selectDate(date: LocalDate) {
        _selectedDate.value = if (_selectedDate.value == date) null else date
    }

    fun refreshCalendarViewType() {
        _calendarViewType.value = preferencesManager.getCalendarViewType()
    }

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
