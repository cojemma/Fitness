package com.fitness.sdk.domain.model

/**
 * Enum representing different categories of exercises.
 * Used to classify exercises by their training type.
 */
enum class ExerciseCategory {
    /** Weight-based exercises (barbells, dumbbells, machines) */
    STRENGTH,

    /** Cardiovascular exercises (running, cycling, swimming) */
    CARDIO,

    /** Stretching and mobility exercises (yoga, stretching) */
    FLEXIBILITY,

    /** Explosive, jumping exercises (box jumps, burpees) */
    PLYOMETRIC,

    /** Exercises using only body weight (push-ups, pull-ups) */
    BODYWEIGHT
}
