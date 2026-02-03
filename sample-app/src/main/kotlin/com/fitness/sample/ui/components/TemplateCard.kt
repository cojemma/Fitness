package com.fitness.sample.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.fitness.sdk.domain.model.MuscleGroup
import com.fitness.sdk.domain.model.WorkoutTemplate

/**
 * Card component for displaying a workout template summary.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TemplateCard(
    template: WorkoutTemplate,
    onClick: () -> Unit,
    onStartWorkout: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header row with name and delete button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = template.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete template",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Description
            if (template.description?.isNotBlank() == true) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = template.description!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Muscle group chips
            if (template.targetMuscleGroups.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    template.targetMuscleGroups.take(4).forEach { muscleGroup ->
                        MuscleGroupChip(muscleGroup = muscleGroup)
                    }
                    if (template.targetMuscleGroups.size > 4) {
                        Text(
                            text = "+${template.targetMuscleGroups.size - 4}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Exercise and set count
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatBadge(
                        label = "Exercises",
                        value = template.getTotalExercises().toString()
                    )
                    StatBadge(
                        label = "Sets",
                        value = template.getTotalWorkingSets().toString()
                    )
                    if (template.estimatedDurationMinutes > 0) {
                        StatBadge(
                            label = "Minutes",
                            value = template.estimatedDurationMinutes.toString()
                        )
                    }
                }

                // Start workout button
                FilledTonalButton(
                    onClick = onStartWorkout,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Start")
                }
            }
        }
    }
}

@Composable
private fun StatBadge(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun MuscleGroupChip(
    muscleGroup: MuscleGroup,
    modifier: Modifier = Modifier
) {
    val color = getMuscleGroupColor(muscleGroup)
    
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Text(
                text = formatMuscleGroupName(muscleGroup),
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private fun formatMuscleGroupName(muscleGroup: MuscleGroup): String {
    return muscleGroup.name
        .lowercase()
        .replaceFirstChar { it.uppercase() }
        .replace("_", " ")
}

private fun getMuscleGroupColor(muscleGroup: MuscleGroup): Color {
    return when (muscleGroup) {
        MuscleGroup.CHEST -> Color(0xFFE53935)
        MuscleGroup.BACK -> Color(0xFF1E88E5)
        MuscleGroup.SHOULDERS -> Color(0xFFFF9800)
        MuscleGroup.BICEPS -> Color(0xFF8E24AA)
        MuscleGroup.TRICEPS -> Color(0xFF00ACC1)
        MuscleGroup.QUADRICEPS -> Color(0xFF43A047)
        MuscleGroup.HAMSTRINGS -> Color(0xFF2E7D32)
        MuscleGroup.CORE -> Color(0xFFFFB300)
        MuscleGroup.GLUTES -> Color(0xFFD81B60)
        MuscleGroup.FOREARMS -> Color(0xFF5D4037)
        MuscleGroup.CALVES -> Color(0xFF546E7A)
        MuscleGroup.FULL_BODY -> Color(0xFF7B1FA2)
    }
}
