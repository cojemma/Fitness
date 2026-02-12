package com.fitness.sample.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Link
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fitness.sample.R
import com.fitness.sample.ui.template.TemplateExerciseState
import com.fitness.sample.ui.template.TemplateSetState

/**
 * Card for editing an exercise within a template.
 */
@Composable
fun TemplateExerciseCard(
    exercise: TemplateExerciseState,
    exerciseIndex: Int,
    onRemoveExercise: () -> Unit,
    onAddSet: () -> Unit,
    onRemoveSet: (Int) -> Unit,
    onUpdateSet: (Int, TemplateSetState) -> Unit,
    onUpdateRestSeconds: (Int) -> Unit,
    onSwapExercise: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onSuperset: () -> Unit,
    isSuperset: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSuperset) MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                           else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with exercise name and delete button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isSuperset) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = stringResource(R.string.label_superset),
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = exercise.exerciseName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box {
                    var showMenu by remember { mutableStateOf(false) }

                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.cd_options)
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.menu_move_up)) },
                            onClick = {
                                onMoveUp()
                                showMenu = false
                            },
                            leadingIcon = { Icon(Icons.Default.KeyboardArrowUp, null) }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.menu_move_down)) },
                            onClick = {
                                onMoveDown()
                                showMenu = false
                            },
                            leadingIcon = { Icon(Icons.Default.KeyboardArrowDown, null) }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.menu_swap_exercise)) },
                            onClick = {
                                onSwapExercise()
                                showMenu = false
                            },
                            leadingIcon = { Icon(Icons.Default.Refresh, null) }
                        )
                        DropdownMenuItem(
                            text = { Text(if (isSuperset) stringResource(R.string.menu_unlink_superset) else stringResource(R.string.menu_link_superset)) },
                            onClick = {
                                onSuperset()
                                showMenu = false
                            },
                            leadingIcon = { Icon(Icons.Default.Link, null) }
                        )
                    }
                }

                IconButton(
                    onClick = onRemoveExercise,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.cd_remove_exercise),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sets header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.header_set),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.width(24.dp)
                )
                Text(
                    text = stringResource(R.string.header_reps),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.header_weight_kg),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "W",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.width(24.dp),
                    textAlign = TextAlign.Center
                )
                // Spacer for delete button column
                Spacer(modifier = Modifier.width(28.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sets list
            exercise.sets.forEachIndexed { setIndex, set ->
                SetRow(
                    set = set,
                    onUpdateSet = { updatedSet -> onUpdateSet(setIndex, updatedSet) },
                    onRemoveSet = { onRemoveSet(setIndex) },
                    canRemove = exercise.sets.size > 1
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Add set button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                FilledTonalIconButton(
                    onClick = onAddSet,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.cd_add_set),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Rest timer setting
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.label_rest_between_sets),
                    style = MaterialTheme.typography.bodyMedium
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf(60, 90, 120, 180).forEach { seconds ->
                        RestTimerChip(
                            seconds = seconds,
                            isSelected = exercise.restSeconds == seconds,
                            onClick = { onUpdateRestSeconds(seconds) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SetRow(
    set: TemplateSetState,
    onUpdateSet: (TemplateSetState) -> Unit,
    onRemoveSet: () -> Unit,
    canRemove: Boolean,
    modifier: Modifier = Modifier
) {
    // Local text state decoupled from the model — avoids "jumping" on partial input
    var repsText by remember(set.setNumber, set.targetReps) {
        val t = set.targetReps.toString()
        mutableStateOf(TextFieldValue(t, TextRange(t.length)))
    }
    var weightText by remember(set.setNumber, set.targetWeight) {
        val t = set.targetWeight?.let {
            if (it % 1f == 0f) it.toInt().toString() else "%.1f".format(it)
        } ?: ""
        mutableStateOf(TextFieldValue(t, TextRange(t.length)))
    }

    val repsRegex = remember { Regex("^\\d{0,3}$") }
    val weightRegex = remember { Regex("^\\d{0,4}(\\.\\d{0,1})?$") }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                if (set.isWarmupSet) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                else MaterialTheme.colorScheme.surface,
                RoundedCornerShape(8.dp)
            )
            .padding(vertical = 4.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Set number
        Text(
            text = "${set.setNumber}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(24.dp)
        )

        // Reps input
        OutlinedTextField(
            value = repsText,
            onValueChange = { newValue ->
                if (newValue.text.isEmpty() || newValue.text.matches(repsRegex)) {
                    repsText = newValue
                    newValue.text.toIntOrNull()?.let { reps ->
                        if (reps > 0) onUpdateSet(set.copy(targetReps = reps))
                    }
                }
            },
            modifier = Modifier.weight(1f),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
            shape = RoundedCornerShape(10.dp)
        )

        // Weight input
        OutlinedTextField(
            value = weightText,
            onValueChange = { newValue ->
                if (newValue.text.isEmpty() || newValue.text.matches(weightRegex)) {
                    weightText = newValue
                    if (newValue.text.isBlank()) {
                        onUpdateSet(set.copy(targetWeight = null))
                    } else {
                        newValue.text.toFloatOrNull()?.let { weight ->
                            if (weight >= 0) onUpdateSet(set.copy(targetWeight = weight))
                        }
                    }
                }
            },
            modifier = Modifier.weight(1f),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
            shape = RoundedCornerShape(10.dp),
            placeholder = {
                Text(
                    "\u2014",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        )

        // Warm-up checkbox
        Checkbox(
            checked = set.isWarmupSet,
            onCheckedChange = { onUpdateSet(set.copy(isWarmupSet = it)) },
            modifier = Modifier.size(24.dp)
        )

        // Remove button
        if (canRemove) {
            IconButton(
                onClick = onRemoveSet,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.cd_remove_set),
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
            }
        } else {
            Spacer(modifier = Modifier.width(28.dp))
        }
    }
}

@Composable
private fun RestTimerChip(
    seconds: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant
    ) {
        Text(
            text = "${seconds}s",
            style = MaterialTheme.typography.labelMedium,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}
