package com.fitness.sample.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.fitness.sdk.domain.model.ExerciseCategory
import com.fitness.sdk.domain.model.ExerciseDefinition

/**
 * Thumbnail for an [ExerciseDefinition]. Shows the first demonstration image when available,
 * falling back to a category-colored emoji badge otherwise.
 */
@Composable
fun ExerciseThumbnail(
    exercise: ExerciseDefinition,
    size: Dp = 44.dp,
    modifier: Modifier = Modifier
) {
    val imageUrl = exercise.imageUrls.firstOrNull()
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(8.dp))
            .background(getCategoryColor(exercise.category)),
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl != null) {
            AsyncImage(
                model = imageUrl,
                contentDescription = exercise.name,
                modifier = Modifier.size(size),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = getCategoryEmoji(exercise.category),
                style = MaterialTheme.typography.titleMedium
            )
        }
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
