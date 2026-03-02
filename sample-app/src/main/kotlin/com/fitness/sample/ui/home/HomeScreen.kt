package com.fitness.sample.ui.home

import android.content.Intent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fitness.sample.R
import com.fitness.sample.data.CalendarViewType
import com.fitness.sample.ui.components.EmptyState
import com.fitness.sample.ui.components.MonthlyCalendarView
import com.fitness.sample.ui.components.StatsSummary
import com.fitness.sample.ui.components.WeeklyCalendarView
import com.fitness.sample.ui.components.WorkoutCard
import com.fitness.sdk.FitnessSDK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

    var showExportDialog by remember { mutableStateOf(false) }
    var isExporting by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Pre-resolve string resources for use inside coroutines
    val exportSuccessMsg = stringResource(R.string.export_success)
    val exportNoDataMsg = stringResource(R.string.export_no_data)
    val exportErrorMsg = stringResource(R.string.export_error)

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
                    // Export button
                    if (isExporting) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(24.dp)
                                .padding(end = 4.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        IconButton(onClick = { showExportDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.FileDownload,
                                contentDescription = stringResource(R.string.setting_export_history)
                            )
                        }
                    }
                    // Settings button
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

    // Export history dialog
    if (showExportDialog) {
        ExportHistoryDialog(
            onSelect = { startTime, endTime ->
                showExportDialog = false
                isExporting = true
                scope.launch {
                    try {
                        val workoutManager = FitnessSDK.getWorkoutManager()
                        val result = workoutManager.exportWorkoutHistoryCsv(startTime, endTime)
                        result.onSuccess { csvContent ->
                            if (csvContent.lines().size <= 2) {
                                snackbarHostState.showSnackbar(exportNoDataMsg)
                            } else {
                                val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                                val fileName = "workout_history_${dateFormat.format(Date())}.csv"
                                val file = withContext(Dispatchers.IO) {
                                    File(context.cacheDir, fileName).apply {
                                        writeText(csvContent)
                                    }
                                }
                                val uri = FileProvider.getUriForFile(
                                    context,
                                    "${context.packageName}.fileprovider",
                                    file
                                )
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/csv"
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(
                                    Intent.createChooser(shareIntent, null)
                                )
                                snackbarHostState.showSnackbar(exportSuccessMsg)
                            }
                        }.onFailure { e ->
                            snackbarHostState.showSnackbar(
                                String.format(exportErrorMsg, e.message ?: "Unknown error")
                            )
                        }
                    } catch (e: Exception) {
                        snackbarHostState.showSnackbar(
                            String.format(exportErrorMsg, e.message ?: "Unknown error")
                        )
                    } finally {
                        isExporting = false
                    }
                }
            },
            onDismiss = { showExportDialog = false }
        )
    }
}

@Composable
private fun ExportHistoryDialog(
    onSelect: (startTime: Long, endTime: Long) -> Unit,
    onDismiss: () -> Unit
) {
    val now = System.currentTimeMillis()

    val options = listOf(
        stringResource(R.string.export_last_7_days) to (now - 7L * 24 * 60 * 60 * 1000),
        stringResource(R.string.export_last_30_days) to (now - 30L * 24 * 60 * 60 * 1000),
        stringResource(R.string.export_last_3_months) to (now - 90L * 24 * 60 * 60 * 1000),
        stringResource(R.string.export_all_time) to 0L
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.export_select_range),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                options.forEach { (label, startTime) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelect(startTime, now)
                                onDismiss()
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.btn_cancel))
            }
        }
    )
}
