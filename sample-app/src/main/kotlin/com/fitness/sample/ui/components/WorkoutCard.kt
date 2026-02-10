package com.fitness.sample.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Sports
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fitness.sample.R
import com.fitness.sample.ui.theme.CardioColor
import com.fitness.sample.ui.theme.FlexibilityColor
import com.fitness.sample.ui.theme.HIITColor
import com.fitness.sample.ui.theme.OtherColor
import com.fitness.sample.ui.theme.SportsColor
import com.fitness.sample.ui.theme.StrengthColor
import com.fitness.sdk.domain.model.Workout
import com.fitness.sdk.domain.model.WorkoutType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WorkoutCard(
    workout: Workout,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Type icon
            WorkoutTypeIcon(type = workout.type)

            Spacer(modifier = Modifier.width(16.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = workout.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = formatDate(workout.startTime),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Duration
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.workout_min_format, workout.durationMinutes),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    // Calories
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFFFF5722)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.workout_cal_format, workout.caloriesBurned),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    // Exercises count
                    if (workout.exercises.isNotEmpty()) {
                        Text(
                            text = stringResource(R.string.workout_exercises_format, workout.exercises.size),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Delete button
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.cd_delete),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun WorkoutTypeIcon(
    type: WorkoutType,
    modifier: Modifier = Modifier
) {
    val (icon, color) = getWorkoutTypeIconAndColor(type)

    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = type.name,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
    }
}

fun getWorkoutTypeIconAndColor(type: WorkoutType): Pair<ImageVector, Color> {
    return when (type) {
        WorkoutType.STRENGTH -> Icons.Default.FitnessCenter to StrengthColor
        WorkoutType.CARDIO -> Icons.Default.DirectionsRun to CardioColor
        WorkoutType.FLEXIBILITY -> Icons.Default.SelfImprovement to FlexibilityColor
        WorkoutType.HIIT -> Icons.Default.Whatshot to HIITColor
        WorkoutType.SPORTS -> Icons.Default.Sports to SportsColor
        WorkoutType.OTHER -> Icons.Default.FitnessCenter to OtherColor
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy \u2022 HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
