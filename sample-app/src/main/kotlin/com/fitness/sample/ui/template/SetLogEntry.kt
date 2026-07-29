package com.fitness.sample.ui.template

/**
 * Entry for a logged set.
 */
data class SetLogEntry(
    val setNumber: Int,
    val reps: Int,
    val weight: Float?,
    val restSeconds: Int = 0
)
