package com.fitness.sample.ui.workout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fitness.sample.R
import com.fitness.sample.ui.components.ExerciseItem
import com.fitness.sample.ui.exercise.AddExerciseDialog
import com.fitness.sdk.domain.model.Exercise
import com.fitness.sdk.domain.model.ExerciseDefinition
import com.fitness.sdk.domain.model.WorkoutType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWorkoutScreen(
    onNavigateBack: () -> Unit,
    onWorkoutSaved: () -> Unit,
    onSelectFromLibrary: (() -> Unit)? = null,
    pendingExercise: ExerciseDefinition? = null,
    onExerciseConsumed: (() -> Unit)? = null,
    workoutId: Long? = null,
    viewModel: WorkoutViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val exercises by viewModel.exercises.collectAsState()
    val savedWorkoutId by viewModel.savedWorkoutId.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var showAddExerciseDialog by remember { mutableStateOf(false) }

    // Handle pending exercise from library picker
    LaunchedEffect(pendingExercise) {
        pendingExercise?.let { definition ->
            // Convert ExerciseDefinition to Exercise and add it
            viewModel.addExercise(definition.toExercise())
            onExerciseConsumed?.invoke()
        }
    }

    // Load workout if editing
    LaunchedEffect(workoutId) {
        workoutId?.let { viewModel.loadWorkout(it) }
    }

    // Handle successful save
    LaunchedEffect(savedWorkoutId) {
        savedWorkoutId?.let { onWorkoutSaved() }
    }

    // Show error
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (uiState.isEditing) stringResource(R.string.title_edit_workout)
                        else stringResource(R.string.title_new_workout)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddExerciseDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.cd_add_exercise))
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Name field
            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::updateName,
                label = { Text(stringResource(R.string.label_workout_name)) },
                placeholder = { Text(stringResource(R.string.placeholder_workout_name)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Type dropdown
            WorkoutTypeDropdown(
                selectedType = uiState.type,
                onTypeSelected = viewModel::updateType,
                modifier = Modifier.fillMaxWidth()
            )

            // Duration and Calories in a row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = uiState.durationMinutes,
                    onValueChange = viewModel::updateDuration,
                    label = { Text(stringResource(R.string.label_duration_min)) },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                OutlinedTextField(
                    value = uiState.caloriesBurned,
                    onValueChange = viewModel::updateCalories,
                    label = { Text(stringResource(R.string.label_calories)) },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }

            // Notes
            OutlinedTextField(
                value = uiState.notes,
                onValueChange = viewModel::updateNotes,
                label = { Text(stringResource(R.string.label_notes_optional)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )

            // Exercises section
            if (exercises.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.label_exercises_count, exercises.size),
                    style = MaterialTheme.typography.titleMedium
                )

                exercises.forEachIndexed { index, exercise ->
                    ExerciseItem(exercise = exercise)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Save button
            Button(
                onClick = viewModel::saveWorkout,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            ) {
                Text(
                    text = if (uiState.isLoading) stringResource(R.string.btn_saving)
                    else stringResource(R.string.btn_save_workout)
                )
            }

            Spacer(modifier = Modifier.height(80.dp)) // Space for FAB
        }
    }

    // Add exercise dialog
    if (showAddExerciseDialog) {
        AddExerciseDialog(
            onDismiss = { showAddExerciseDialog = false },
            onExerciseAdded = { exercise ->
                viewModel.addExercise(exercise)
                showAddExerciseDialog = false
            },
            onSelectFromLibrary = onSelectFromLibrary?.let { selectFromLibrary ->
                {
                    showAddExerciseDialog = false
                    selectFromLibrary()
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutTypeDropdown(
    selectedType: WorkoutType,
    onTypeSelected: (WorkoutType) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedType.name.lowercase().replaceFirstChar { it.uppercase() },
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.label_workout_type)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            WorkoutType.entries.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type.name.lowercase().replaceFirstChar { it.uppercase() }) },
                    onClick = {
                        onTypeSelected(type)
                        expanded = false
                    }
                )
            }
        }
    }
}
