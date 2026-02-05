package com.fitness.sample.ui.exercise

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fitness.sdk.domain.model.ExerciseCategory
import com.fitness.sdk.domain.model.ExerciseDefinition
import com.fitness.sdk.domain.model.ExerciseHistory
import com.fitness.sdk.domain.model.ExerciseSessionSummary
import com.fitness.sdk.domain.model.MuscleGroup
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseListScreen(
    onWorkoutClick: ((Long) -> Unit)? = null,
    viewModel: ExerciseListViewModel = viewModel()
) {
    val exercises by viewModel.exercises.collectAsState()
    val historyCache by viewModel.historyCache.collectAsState()
    val loadingExercises by viewModel.loadingExercises.collectAsState()
    val exerciseLoadErrors by viewModel.exerciseLoadErrors.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedMuscleGroup by viewModel.selectedMuscleGroup.collectAsState()

    var expandedExerciseIds by remember { mutableStateOf(setOf<String>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Exercises",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search exercises...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            )

            // Muscle group filter
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedMuscleGroup == null,
                        onClick = { viewModel.onMuscleGroupSelect(null) },
                        label = { Text("All") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
                items(MuscleGroup.entries.toTypedArray()) { muscle ->
                    FilterChip(
                        selected = selectedMuscleGroup == muscle,
                        onClick = { viewModel.onMuscleGroupSelect(muscle) },
                        label = { Text(formatMuscleGroup(muscle)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }

            Text(
                text = "${exercises.size} exercises",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            // Exercise list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(exercises, key = { it.id }) { exercise ->
                    val isExpanded = expandedExerciseIds.contains(exercise.id)
                    val history = historyCache[exercise.name]
                    val isLoading = loadingExercises.contains(exercise.name)
                    val loadError = exerciseLoadErrors[exercise.name]

                    ExerciseListItem(
                        exercise = exercise,
                        isExpanded = isExpanded,
                        history = history,
                        isLoading = isLoading,
                        loadError = loadError,
                        onClick = {
                            if (isExpanded) {
                                expandedExerciseIds = expandedExerciseIds - exercise.id
                            } else {
                                expandedExerciseIds = expandedExerciseIds + exercise.id
                                viewModel.loadExerciseHistory(exercise.name)
                            }
                        },
                        onWorkoutClick = onWorkoutClick
                    )
                }
            }
        }
    }
}

@Composable
private fun ExerciseListItem(
    exercise: ExerciseDefinition,
    isExpanded: Boolean,
    history: ExerciseHistory?,
    isLoading: Boolean,
    loadError: String?,
    onClick: () -> Unit,
    onWorkoutClick: ((Long) -> Unit)?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(getCategoryColor(exercise.category)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = getCategoryEmoji(exercise.category),
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = exercise.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = formatMuscleGroup(exercise.primaryMuscle),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand"
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )

                    if (isLoading) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(32.dp))
                        }
                    } else if (history != null) {
                        ExerciseHistoryContent(
                            history = history,
                            onWorkoutClick = onWorkoutClick
                        )
                    } else if (loadError != null) {
                        Text(
                            text = loadError,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        Text(
                            text = "No history yet. Complete workouts with this exercise to see stats.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExerciseHistoryContent(
    history: ExerciseHistory,
    onWorkoutClick: ((Long) -> Unit)?
) {
    // Stats row
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatChip(
            icon = Icons.Default.History,
            label = "Sessions",
            value = history.totalSessions.toString()
        )
        StatChip(
            icon = Icons.Default.FitnessCenter,
            label = "Max Weight",
            value = history.maxWeight?.let { "${it}kg" } ?: "—"
        )
        StatChip(
            icon = Icons.Default.TrendingUp,
            label = "Est. 1RM",
            value = history.estimated1RM?.let { "${String.format("%.1f", it)}kg" } ?: "—"
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "History",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary
    )

    Spacer(modifier = Modifier.height(8.dp))

    if (history.historyByDate.isEmpty()) {
        Text(
            text = "No recorded sessions",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    } else {
        history.historyByDate.forEach { session ->
            SessionHistoryRow(
                session = session,
                onWorkoutClick = onWorkoutClick
            )
        }
    }
}

@Composable
private fun StatChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SessionHistoryRow(
    session: ExerciseSessionSummary,
    onWorkoutClick: ((Long) -> Unit)?
) {
    val rowModifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 6.dp)
        .then(
            if (onWorkoutClick != null) {
                Modifier.clickable { onWorkoutClick(session.workoutId) }
            } else Modifier
        )

    Row(
        modifier = rowModifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = formatSessionDate(session.workoutDate),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Best: ${session.bestSet} • ${session.setsCount} sets",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = "${session.totalVolume.toInt()} kg vol",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun getCategoryColor(category: ExerciseCategory) = when (category) {
    ExerciseCategory.STRENGTH -> MaterialTheme.colorScheme.errorContainer
    ExerciseCategory.CARDIO -> MaterialTheme.colorScheme.tertiaryContainer
    ExerciseCategory.FLEXIBILITY -> MaterialTheme.colorScheme.secondaryContainer
    ExerciseCategory.PLYOMETRIC -> MaterialTheme.colorScheme.primaryContainer
    ExerciseCategory.BODYWEIGHT -> MaterialTheme.colorScheme.surfaceVariant
}

private fun getCategoryEmoji(category: ExerciseCategory) = when (category) {
    ExerciseCategory.STRENGTH -> "🏋️"
    ExerciseCategory.CARDIO -> "🏃"
    ExerciseCategory.FLEXIBILITY -> "🧘"
    ExerciseCategory.PLYOMETRIC -> "⚡"
    ExerciseCategory.BODYWEIGHT -> "💪"
}

private fun formatMuscleGroup(muscle: MuscleGroup): String = when (muscle) {
    MuscleGroup.CHEST -> "Chest"
    MuscleGroup.BACK -> "Back"
    MuscleGroup.SHOULDERS -> "Shoulders"
    MuscleGroup.BICEPS -> "Biceps"
    MuscleGroup.TRICEPS -> "Triceps"
    MuscleGroup.FOREARMS -> "Forearms"
    MuscleGroup.QUADRICEPS -> "Quads"
    MuscleGroup.HAMSTRINGS -> "Hamstrings"
    MuscleGroup.GLUTES -> "Glutes"
    MuscleGroup.CALVES -> "Calves"
    MuscleGroup.CORE -> "Core"
    MuscleGroup.FULL_BODY -> "Full Body"
}

private fun formatSessionDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
