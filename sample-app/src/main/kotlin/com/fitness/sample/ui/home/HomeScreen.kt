package com.fitness.sample.ui.home

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fitness.sample.R
import com.fitness.sample.data.CalendarViewType
import com.fitness.sample.ui.components.EmptyState
import com.fitness.sample.ui.components.MonthlyCalendarView
import com.fitness.sample.ui.components.StatsSummary
import com.fitness.sample.ui.components.WeeklyCalendarView
import com.fitness.sample.ui.components.WorkoutCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddWorkout: () -> Unit,
    onWorkoutClick: (Long) -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val workouts by viewModel.workouts.collectAsState()
    val filteredWorkouts by viewModel.filteredWorkouts.collectAsState()
    val calendarViewType by viewModel.calendarViewType.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val error by viewModel.error.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Refresh calendar type when returning from settings
    LaunchedEffect(Unit) {
        viewModel.refreshCalendarViewType()
    }

    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    val weeklyStats = viewModel.getWeeklyStats(workouts)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.title_my_workouts),
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.cd_settings)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddWorkout,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.cd_add_workout)
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (workouts.isEmpty() && calendarViewType == CalendarViewType.NONE) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                EmptyState(
                    title = stringResource(R.string.empty_workouts_title),
                    subtitle = stringResource(R.string.empty_workouts_subtitle)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .animateContentSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Stats summary
                item {
                    StatsSummary(
                        totalWorkouts = weeklyStats.totalWorkouts,
                        totalCalories = weeklyStats.totalCalories,
                        totalMinutes = weeklyStats.totalMinutes,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Calendar view
                when (calendarViewType) {
                    CalendarViewType.WEEKLY -> {
                        item {
                            WeeklyCalendarView(
                                workouts = workouts,
                                selectedDate = selectedDate,
                                onDateSelected = { viewModel.selectDate(it) },
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                    }
                    CalendarViewType.MONTHLY -> {
                        item {
                            MonthlyCalendarView(
                                workouts = workouts,
                                selectedDate = selectedDate,
                                onDateSelected = { viewModel.selectDate(it) },
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                    }
                    CalendarViewType.NONE -> { /* No calendar */ }
                }

                // Workout list (filtered when a date is selected)
                val displayWorkouts = if (selectedDate != null) filteredWorkouts else workouts

                if (displayWorkouts.isEmpty() && selectedDate != null) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            EmptyState(
                                title = stringResource(R.string.empty_workouts_title),
                                subtitle = stringResource(R.string.empty_workouts_subtitle)
                            )
                        }
                    }
                } else {
                    items(
                        items = displayWorkouts,
                        key = { it.id }
                    ) { workout ->
                        WorkoutCard(
                            workout = workout,
                            onClick = { onWorkoutClick(workout.id) },
                            onDelete = { viewModel.deleteWorkout(workout.id) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
