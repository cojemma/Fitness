package com.fitness.sample.ui.template

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fitness.sample.R
import com.fitness.sdk.domain.model.ExerciseDefinition

/**
 * Active workout session screen with set logging and rest timer.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveWorkoutScreen(
    templateId: Long,
    onNavigateBack: () -> Unit,
    onWorkoutComplete: () -> Unit,
    onAddExercise: () -> Unit = {},
    pendingExercise: ExerciseDefinition? = null,
    onExerciseConsumed: (() -> Unit)? = null,
    viewModel: ActiveWorkoutViewModel = viewModel()
) {
    val workout by viewModel.workout.collectAsState()
    val currentExerciseIndex by viewModel.currentExerciseIndex.collectAsState()
    val currentSetIndex by viewModel.currentSetIndex.collectAsState()
    val completedSets by viewModel.completedSets.collectAsState()
    val restTimeRemaining by viewModel.restTimeRemaining.collectAsState()
    val isResting by viewModel.isResting.collectAsState()
    val elapsedSeconds by viewModel.elapsedSeconds.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val workoutCompleted by viewModel.workoutCompleted.collectAsState()

    val savedWorkoutId by viewModel.savedWorkoutId.collectAsState()
    val templateSaved by viewModel.templateSaved.collectAsState()

    // Navigation state
    val snackbarHostState = remember { SnackbarHostState() }
    var showReorderSheet by remember { mutableStateOf(false) }
    var showSaveAsTemplateDialog by remember { mutableStateOf(false) }
    var templateNameInput by remember { mutableStateOf("") }

    // Check if workout was started from a template
    val originalTemplateId = viewModel.getOriginalTemplateId()
    var showCreateNewDialog by remember { mutableStateOf(false) }

    // Start workout
    LaunchedEffect(templateId) {
        viewModel.startWorkout(templateId)
    }

    // Handle pending exercise from library picker
    LaunchedEffect(pendingExercise) {
        pendingExercise?.let { definition ->
            viewModel.addExercise(definition)
            onExerciseConsumed?.invoke()
        }
    }

    // Handle workout completion - show save as template dialog
    LaunchedEffect(workoutCompleted) {
        if (workoutCompleted) {
            showSaveAsTemplateDialog = true
        }
    }

    // Handle template saved - navigate after dialog closes
    val templateSavedMessage = stringResource(R.string.snackbar_template_saved)
    LaunchedEffect(templateSaved) {
        if (templateSaved) {
            snackbarHostState.showSnackbar(templateSavedMessage)
            showSaveAsTemplateDialog = false
            showCreateNewDialog = false
            onWorkoutComplete()
        }
    }

    // Handle errors
    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    // Derive current exercise from observed states so it updates when index changes
    val currentExercise = workout?.exercises?.getOrNull(currentExerciseIndex)
    val exerciseCount = workout?.exercises?.size ?: 0

    // Check if workout was started from a template


    // Save as Template Dialog - Main Options
    if (showSaveAsTemplateDialog && !showCreateNewDialog) {
        AlertDialog(
            onDismissRequest = {
                showSaveAsTemplateDialog = false
                onWorkoutComplete()
            },
            title = {
                Text(
                    text = stringResource(R.string.dialog_workout_complete),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = stringResource(R.string.dialog_what_to_do),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Option 1: Replace Original Template (only if started from template)
                    if (originalTemplateId != null) {
                        Button(
                            onClick = {
                                viewModel.updateOriginalTemplate()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = savedWorkoutId != null
                        ) {
                            Text(stringResource(R.string.btn_update_original_template))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Option 2: Create New Template
                    OutlinedButton(
                        onClick = {
                            showCreateNewDialog = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.btn_save_as_new_template))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Option 3: Skip
                    OutlinedButton(
                        onClick = {
                            showSaveAsTemplateDialog = false
                            onWorkoutComplete()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text(stringResource(R.string.btn_skip))
                    }
                }
            },
            confirmButton = {},
            dismissButton = {}
        )
    }

    // Create New Template Dialog (name input)
    if (showCreateNewDialog) {
        val myTemplatePlaceholder = stringResource(R.string.placeholder_my_template)
        AlertDialog(
            onDismissRequest = {
                showCreateNewDialog = false
                showSaveAsTemplateDialog = false
                onWorkoutComplete()
            },
            title = {
                Text(
                    text = stringResource(R.string.dialog_new_template_name),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = templateNameInput,
                        onValueChange = { templateNameInput = it },
                        label = { Text(stringResource(R.string.label_template_name)) },
                        placeholder = { Text(workout?.name ?: myTemplatePlaceholder) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val name = templateNameInput.ifBlank { workout?.name ?: myTemplatePlaceholder }
                        viewModel.saveAsTemplate(name)
                    },
                    enabled = savedWorkoutId != null
                ) {
                    Text(stringResource(R.string.btn_create))
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showCreateNewDialog = false
                    }
                ) {
                    Text(stringResource(R.string.cd_back))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = workout?.name ?: stringResource(R.string.label_workout_fallback),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = formatTime(elapsedSeconds),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.cd_cancel_workout)
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = { viewModel.finishWorkout() },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary
                        ),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(R.string.btn_finish))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddExercise,
                shape = RoundedCornerShape(16.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.cd_add_exercise)
                )
            }
        }
    ) { paddingValues ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(stringResource(R.string.loading_starting_workout))
                    }
                }
            }
            currentExercise == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.no_exercises_in_workout))
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // Progress indicator
                    ExerciseProgress(
                        currentIndex = currentExerciseIndex,
                        totalCount = exerciseCount
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Exercise navigator rail
                    ExerciseNavigatorRail(
                        exercises = workout?.exercises ?: emptyList(),
                        currentExerciseIndex = currentExerciseIndex,
                        completedSets = completedSets,
                        onExerciseSelected = { index -> viewModel.goToExercise(index) },
                        onReorderClicked = { showReorderSheet = true }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Superset Info
                    val currentSupersetId = currentExercise.supersetGroupId
                    if (currentSupersetId != null) {
                        val otherExercisesInSuperset = workout?.exercises?.filterIndexed { index, ex ->
                             ex.supersetGroupId == currentSupersetId && index != currentExerciseIndex
                        } ?: emptyList()

                        if (otherExercisesInSuperset.isNotEmpty()) {
                            SupersetInfoCard(otherExercises = otherExercisesInSuperset)
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    // Current exercise card
                    CurrentExerciseCard(
                        exerciseName = currentExercise.name,
                        currentSet = currentSetIndex + 1,
                        totalSets = currentExercise.sets,
                        targetReps = currentExercise.reps,
                        targetWeight = currentExercise.weight,
                        lastSetInfo = viewModel.getLastSetData(currentExercise.name, currentSetIndex + 1),
                        completedSetsCount = completedSets[currentExerciseIndex]?.size ?: 0,
                        onLogSet = { reps, weight -> viewModel.logSet(reps, weight) }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Rest timer (visible when resting)
                    AnimatedVisibility(
                        visible = isResting,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        RestTimerCard(
                            remainingSeconds = restTimeRemaining,
                            onSkip = { viewModel.skipRest() }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Navigation buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.previousExercise() },
                            enabled = currentExerciseIndex > 0,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(stringResource(R.string.btn_previous))
                        }

                        OutlinedButton(
                            onClick = { viewModel.nextExercise() },
                            enabled = currentExerciseIndex < exerciseCount - 1,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(stringResource(R.string.btn_next))
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Completed sets summary
                    CompletedSetsSummary(
                        completedSets = completedSets[currentExerciseIndex] ?: emptyList()
                    )
                }
            }
        }

        // Exercise reorder bottom sheet
        if (showReorderSheet) {
            ExerciseReorderSheet(
                exercises = workout?.exercises ?: emptyList(),
                currentExerciseIndex = currentExerciseIndex,
                completedSets = completedSets,
                onDismiss = { showReorderSheet = false },
                onReorder = { from, to -> viewModel.reorderExercises(from, to) },
                onExerciseSelected = { index ->
                    viewModel.goToExercise(index)
                    showReorderSheet = false
                }
            )
        }
    }
}

@Composable
private fun ExerciseProgress(
    currentIndex: Int,
    totalCount: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.exercise_progress_format, currentIndex + 1, totalCount),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        val progress by animateFloatAsState(
            targetValue = (currentIndex + 1).toFloat() / totalCount.toFloat(),
            label = "progress"
        )

        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
private fun CurrentExerciseCard(
    exerciseName: String,
    currentSet: Int,
    totalSets: Int,
    targetReps: Int,
    targetWeight: Float?,
    lastSetInfo: String?,
    completedSetsCount: Int,
    onLogSet: (Int, Float?) -> Unit,
    modifier: Modifier = Modifier
) {
    var repsInput by remember(exerciseName, currentSet) { mutableStateOf(targetReps.toString()) }
    var weightInput by remember(exerciseName, currentSet) {
        mutableStateOf(targetWeight?.let { if (it % 1f == 0f) it.toInt().toString() else "%.1f".format(it) } ?: "")
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Exercise name
            Text(
                text = exerciseName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Set indicator
            Text(
                text = stringResource(R.string.set_of_format, currentSet, totalSets),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // Last session data
            if (lastSetInfo != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = stringResource(R.string.last_set_prefix, lastSetInfo),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Input fields
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reps input
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.label_reps_input),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = repsInput,
                        onValueChange = { repsInput = it },
                        modifier = Modifier.width(80.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = MaterialTheme.typography.titleLarge.copy(
                            textAlign = TextAlign.Center
                        )
                    )
                }

                Text(
                    text = "\u00d7",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )

                // Weight input
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.label_weight_kg_input),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = weightInput,
                        onValueChange = { weightInput = it },
                        modifier = Modifier.width(100.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        textStyle = MaterialTheme.typography.titleLarge.copy(
                            textAlign = TextAlign.Center
                        ),
                        placeholder = {
                            Text(
                                "\u2014",
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Complete set button
            Button(
                onClick = {
                    val reps = repsInput.toIntOrNull() ?: targetReps
                    val weight = weightInput.toFloatOrNull()
                    onLogSet(reps, weight)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.btn_complete_set),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // Completed sets indicator
            if (completedSetsCount > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    repeat(totalSets) { index ->
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(
                                    if (index < completedSetsCount) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RestTimerCard(
    remainingSeconds: Int,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.label_rest),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = formatTime(remainingSeconds),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )

            Spacer(modifier = Modifier.height(16.dp))

            FilledTonalButton(
                onClick = onSkip,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(stringResource(R.string.btn_skip_rest))
            }
        }
    }
}

@Composable
private fun CompletedSetsSummary(
    completedSets: List<SetLogEntry>,
    modifier: Modifier = Modifier
) {
    if (completedSets.isEmpty()) return

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.label_completed_sets),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            completedSets.forEach { entry ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.set_number_format, entry.setNumber),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = entry.weight?.let {
                            val weightStr = if (it % 1f == 0f) it.toInt().toString() else "%.1f".format(it)
                            stringResource(R.string.reps_weight_format, entry.reps, weightStr)
                        } ?: stringResource(R.string.reps_only_format, entry.reps),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

private fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return "%d:%02d".format(minutes, remainingSeconds)
}

@Composable
private fun SupersetInfoCard(
    otherExercises: List<com.fitness.sdk.domain.model.Exercise>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Link,
                contentDescription = stringResource(R.string.label_superset),
                tint = MaterialTheme.colorScheme.tertiary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = stringResource(R.string.label_superset),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.paired_with_format, otherExercises.joinToString { it.name }),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
