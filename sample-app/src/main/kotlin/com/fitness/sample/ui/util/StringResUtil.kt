package com.fitness.sample.ui.util

import androidx.annotation.StringRes
import com.fitness.sample.R
import com.fitness.sdk.domain.model.MuscleGroup

/**
 * Returns the string resource ID for a given MuscleGroup.
 * Use with stringResource() in Composable context.
 */
@StringRes
fun getMuscleGroupStringRes(muscle: MuscleGroup): Int = when (muscle) {
    MuscleGroup.CHEST -> R.string.muscle_chest
    MuscleGroup.BACK -> R.string.muscle_back
    MuscleGroup.SHOULDERS -> R.string.muscle_shoulders
    MuscleGroup.BICEPS -> R.string.muscle_biceps
    MuscleGroup.TRICEPS -> R.string.muscle_triceps
    MuscleGroup.FOREARMS -> R.string.muscle_forearms
    MuscleGroup.QUADRICEPS -> R.string.muscle_quads
    MuscleGroup.HAMSTRINGS -> R.string.muscle_hamstrings
    MuscleGroup.GLUTES -> R.string.muscle_glutes
    MuscleGroup.CALVES -> R.string.muscle_calves
    MuscleGroup.CORE -> R.string.muscle_core
    MuscleGroup.FULL_BODY -> R.string.muscle_full_body
}
