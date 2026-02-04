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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
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
                            contentDescription = "Superset",
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
                            contentDescription = "Options"
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Move Up") },
                            onClick = { 
                                onMoveUp()
                                showMenu = false 
                            },
                            leadingIcon = { Icon(Icons.Default.KeyboardArrowUp, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Move Down") },
                            onClick = { 
                                onMoveDown()
                                showMenu = false 
                            },
                            leadingIcon = { Icon(Icons.Default.KeyboardArrowDown, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Swap Exercise") },
                            onClick = { 
                                onSwapExercise()
                                showMenu = false 
                            },
                            leadingIcon = { Icon(Icons.Default.Refresh, null) }
                        )
                        DropdownMenuItem(
                            text = { Text(if (isSuperset) "Unlink Superset" else "Link to Next (Superset)") },
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
                        contentDescription = "Remove exercise",
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
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Set",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.width(40.dp)
                )
                Text(
                    text = "Reps",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.width(70.dp)
                )
                Text(
                    text = "Weight (kg)",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.width(90.dp)
                )
                Text(
                    text = "Warm-up",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.width(70.dp)
                )
                Spacer(modifier = Modifier.width(40.dp))
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
                        contentDescription = "Add set",
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
                    text = "Rest between sets",
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
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                if (set.isWarmupSet) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                else MaterialTheme.colorScheme.surface,
                RoundedCornerShape(8.dp)
            )
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Set number
        Text(
            text = "${set.setNumber}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(40.dp)
        )

        // Reps input
        OutlinedTextField(
            value = set.targetReps.toString(),
            onValueChange = { value ->
                value.toIntOrNull()?.let { reps ->
                    if (reps > 0) onUpdateSet(set.copy(targetReps = reps))
                }
            },
            modifier = Modifier.width(70.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = MaterialTheme.typography.bodyMedium
        )

        // Weight input
        OutlinedTextField(
            value = set.targetWeight?.toString() ?: "",
            onValueChange = { value ->
                if (value.isBlank()) {
                    onUpdateSet(set.copy(targetWeight = null))
                } else {
                    value.toFloatOrNull()?.let { weight ->
                        if (weight >= 0) onUpdateSet(set.copy(targetWeight = weight))
                    }
                }
            },
            modifier = Modifier.width(90.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            textStyle = MaterialTheme.typography.bodyMedium,
            placeholder = { Text("—", style = MaterialTheme.typography.bodyMedium) }
        )

        // Warm-up checkbox
        Checkbox(
            checked = set.isWarmupSet,
            onCheckedChange = { onUpdateSet(set.copy(isWarmupSet = it)) },
            modifier = Modifier.width(70.dp)
        )

        // Remove button
        if (canRemove) {
            IconButton(
                onClick = onRemoveSet,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove set",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )
            }
        } else {
            Spacer(modifier = Modifier.width(40.dp))
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
