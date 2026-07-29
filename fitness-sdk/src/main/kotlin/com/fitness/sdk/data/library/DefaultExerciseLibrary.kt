package com.fitness.sdk.data.library

import com.fitness.sdk.domain.model.ExerciseCategory
import com.fitness.sdk.domain.model.ExerciseDefinition
import com.fitness.sdk.domain.model.MuscleGroup

/**
 * Default implementation of [ExerciseLibraryProvider] with 50+ predefined exercises.
 * Covers major muscle groups and exercise categories.
 */
class DefaultExerciseLibrary : ExerciseLibraryProvider {

    private val exercises: List<ExerciseDefinition> by lazy { createExerciseLibrary() }
    private val exerciseMap: Map<String, ExerciseDefinition> by lazy { 
        exercises.associateBy { it.id } 
    }

    override fun getAllExercises(): List<ExerciseDefinition> = exercises

    override fun getExerciseById(id: String): ExerciseDefinition? = exerciseMap[id]

    override fun getExercisesByCategory(category: ExerciseCategory): List<ExerciseDefinition> =
        exercises.filter { it.category == category }

    override fun getExercisesByMuscleGroup(muscleGroup: MuscleGroup): List<ExerciseDefinition> =
        exercises.filter { 
            it.primaryMuscle == muscleGroup || muscleGroup in it.secondaryMuscles 
        }

    override fun searchExercises(query: String): List<ExerciseDefinition> {
        if (query.isBlank()) return exercises
        val lowerQuery = query.lowercase()
        return exercises.filter { it.name.lowercase().contains(lowerQuery) }
    }

    /**
     * Demonstration image URLs (from the free-exercise-db public dataset) for the hand-authored
     * exercises defined below. Keyed by exercise id and applied as a post-processing step so the
     * original exercise definitions don't need to be touched individually.
     */
    private val defaultImageOverrides: Map<String, List<String>> = mapOf(
        "chest_bench_press" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Barbell_Bench_Press_-_Medium_Grip/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Barbell_Bench_Press_-_Medium_Grip/1.jpg"
        ),
        "chest_incline_bench" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Barbell_Incline_Bench_Press_-_Medium_Grip/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Barbell_Incline_Bench_Press_-_Medium_Grip/1.jpg"
        ),
        "chest_dumbbell_fly" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Dumbbell_Flyes/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Dumbbell_Flyes/1.jpg"
        ),
        "chest_pushup" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Pushups/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Pushups/1.jpg"
        ),
        "chest_cable_crossover" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Cable_Crossover/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Cable_Crossover/1.jpg"
        ),
        "back_pullup" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Pullups/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Pullups/1.jpg"
        ),
        "back_deadlift" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Barbell_Deadlift/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Barbell_Deadlift/1.jpg"
        ),
        "back_barbell_row" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Bent_Over_Barbell_Row/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Bent_Over_Barbell_Row/1.jpg"
        ),
        "back_lat_pulldown" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Wide-Grip_Lat_Pulldown/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Wide-Grip_Lat_Pulldown/1.jpg"
        ),
        "back_seated_row" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Seated_Cable_Rows/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Seated_Cable_Rows/1.jpg"
        ),
        "back_dumbbell_row" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/One-Arm_Dumbbell_Row/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/One-Arm_Dumbbell_Row/1.jpg"
        ),
        "shoulder_overhead_press" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Barbell_Shoulder_Press/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Barbell_Shoulder_Press/1.jpg"
        ),
        "shoulder_lateral_raise" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Side_Lateral_Raise/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Side_Lateral_Raise/1.jpg"
        ),
        "shoulder_front_raise" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Front_Dumbbell_Raise/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Front_Dumbbell_Raise/1.jpg"
        ),
        "shoulder_face_pull" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Face_Pull/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Face_Pull/1.jpg"
        ),
        "shoulder_arnold_press" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Arnold_Dumbbell_Press/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Arnold_Dumbbell_Press/1.jpg"
        ),
        "biceps_barbell_curl" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Barbell_Curl/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Barbell_Curl/1.jpg"
        ),
        "biceps_dumbbell_curl" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Dumbbell_Bicep_Curl/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Dumbbell_Bicep_Curl/1.jpg"
        ),
        "biceps_hammer_curl" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Hammer_Curls/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Hammer_Curls/1.jpg"
        ),
        "biceps_preacher_curl" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Preacher_Curl/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Preacher_Curl/1.jpg"
        ),
        "biceps_concentration_curl" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Concentration_Curls/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Concentration_Curls/1.jpg"
        ),
        "triceps_pushdown" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Triceps_Pushdown/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Triceps_Pushdown/1.jpg"
        ),
        "triceps_dip" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Dips_-_Triceps_Version/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Dips_-_Triceps_Version/1.jpg"
        ),
        "triceps_overhead_extension" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Overhead_Triceps/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Overhead_Triceps/1.jpg"
        ),
        "triceps_skull_crusher" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/EZ-Bar_Skullcrusher/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/EZ-Bar_Skullcrusher/1.jpg"
        ),
        "triceps_close_grip_bench" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Close-Grip_Barbell_Bench_Press/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Close-Grip_Barbell_Bench_Press/1.jpg"
        ),
        "quads_squat" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Barbell_Squat/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Barbell_Squat/1.jpg"
        ),
        "quads_front_squat" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Front_Squat_Clean_Grip/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Front_Squat_Clean_Grip/1.jpg"
        ),
        "quads_leg_press" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Leg_Press/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Leg_Press/1.jpg"
        ),
        "quads_leg_extension" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Leg_Extensions/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Leg_Extensions/1.jpg"
        ),
        "quads_lunge" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Barbell_Walking_Lunge/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Barbell_Walking_Lunge/1.jpg"
        ),
        "quads_goblet_squat" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Goblet_Squat/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Goblet_Squat/1.jpg"
        ),
        "hams_romanian_deadlift" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Romanian_Deadlift/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Romanian_Deadlift/1.jpg"
        ),
        "hams_leg_curl" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Lying_Leg_Curls/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Lying_Leg_Curls/1.jpg"
        ),
        "hams_seated_leg_curl" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Seated_Leg_Curl/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Seated_Leg_Curl/1.jpg"
        ),
        "hams_good_morning" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Good_Morning/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Good_Morning/1.jpg"
        ),
        "glutes_hip_thrust" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Barbell_Hip_Thrust/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Barbell_Hip_Thrust/1.jpg"
        ),
        "glutes_glute_bridge" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Barbell_Glute_Bridge/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Barbell_Glute_Bridge/1.jpg"
        ),
        "glutes_kickback" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/One-Legged_Cable_Kickback/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/One-Legged_Cable_Kickback/1.jpg"
        ),
        "calves_standing_raise" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Standing_Calf_Raises/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Standing_Calf_Raises/1.jpg"
        ),
        "calves_seated_raise" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Seated_Calf_Raise/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Seated_Calf_Raise/1.jpg"
        ),
        "core_plank" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Plank/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Plank/1.jpg"
        ),
        "core_crunch" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Crunches/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Crunches/1.jpg"
        ),
        "core_leg_raise" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Hanging_Leg_Raise/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Hanging_Leg_Raise/1.jpg"
        ),
        "core_russian_twist" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Russian_Twist/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Russian_Twist/1.jpg"
        ),
        "core_dead_bug" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Dead_Bug/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Dead_Bug/1.jpg"
        ),
        "core_ab_wheel" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Ab_Roller/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Ab_Roller/1.jpg"
        ),
        "core_cable_woodchop" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Standing_Cable_Wood_Chop/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Standing_Cable_Wood_Chop/1.jpg"
        ),
        "plyo_box_jump" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Front_Box_Jump/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Front_Box_Jump/1.jpg"
        ),
        "plyo_mountain_climber" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Mountain_Climbers/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Mountain_Climbers/1.jpg"
        ),
        "plyo_jump_squat" to listOf(
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Freehand_Jump_Squat/0.jpg",
            "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Freehand_Jump_Squat/1.jpg"
        ),
    )

    private fun createExerciseLibrary(): List<ExerciseDefinition> {
        val base = listOf(
        // ============ CHEST EXERCISES ============
        ExerciseDefinition(
            id = "chest_bench_press",
            name = "Barbell Bench Press",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.CHEST,
            secondaryMuscles = listOf(MuscleGroup.TRICEPS, MuscleGroup.SHOULDERS),
            description = "Classic compound chest exercise performed lying on a flat bench.",
            instructions = "Lie on bench, grip bar slightly wider than shoulders, lower to chest, press up.",
            defaultSets = 4,
            defaultReps = 8
        ),
        ExerciseDefinition(
            id = "chest_incline_bench",
            name = "Incline Bench Press",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.CHEST,
            secondaryMuscles = listOf(MuscleGroup.SHOULDERS, MuscleGroup.TRICEPS),
            description = "Targets upper chest with an inclined bench angle.",
            defaultSets = 3,
            defaultReps = 10
        ),
        ExerciseDefinition(
            id = "chest_dumbbell_fly",
            name = "Dumbbell Fly",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.CHEST,
            description = "Isolation exercise for chest using a wide arc motion.",
            instructions = "Lie on bench with dumbbells, arms extended, lower in arc, squeeze to return.",
            defaultSets = 3,
            defaultReps = 12
        ),
        ExerciseDefinition(
            id = "chest_pushup",
            name = "Push-Up",
            category = ExerciseCategory.BODYWEIGHT,
            primaryMuscle = MuscleGroup.CHEST,
            secondaryMuscles = listOf(MuscleGroup.TRICEPS, MuscleGroup.SHOULDERS, MuscleGroup.CORE),
            description = "Fundamental bodyweight exercise for chest and upper body.",
            instructions = "Hands shoulder-width apart, lower chest to floor, push back up.",
            defaultSets = 3,
            defaultReps = 15
        ),
        ExerciseDefinition(
            id = "chest_cable_crossover",
            name = "Cable Crossover",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.CHEST,
            description = "Cable exercise for chest isolation and stretch.",
            defaultSets = 3,
            defaultReps = 12
        ),
        ExerciseDefinition(
            id = "chest_dip",
            name = "Chest Dip",
            category = ExerciseCategory.BODYWEIGHT,
            primaryMuscle = MuscleGroup.CHEST,
            secondaryMuscles = listOf(MuscleGroup.TRICEPS, MuscleGroup.SHOULDERS),
            description = "Bodyweight compound exercise leaning forward to target chest.",
            defaultSets = 3,
            defaultReps = 10
        ),

        // ============ BACK EXERCISES ============
        ExerciseDefinition(
            id = "back_pullup",
            name = "Pull-Up",
            category = ExerciseCategory.BODYWEIGHT,
            primaryMuscle = MuscleGroup.BACK,
            secondaryMuscles = listOf(MuscleGroup.BICEPS, MuscleGroup.FOREARMS),
            description = "Classic bodyweight back exercise using overhand grip.",
            instructions = "Hang from bar, pull chin above bar, lower with control.",
            defaultSets = 3,
            defaultReps = 8
        ),
        ExerciseDefinition(
            id = "back_deadlift",
            name = "Deadlift",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.BACK,
            secondaryMuscles = listOf(MuscleGroup.HAMSTRINGS, MuscleGroup.GLUTES, MuscleGroup.CORE),
            description = "Compound lift for posterior chain strength.",
            instructions = "Hinge at hips, grip bar, drive through heels, stand tall.",
            defaultSets = 4,
            defaultReps = 5
        ),
        ExerciseDefinition(
            id = "back_barbell_row",
            name = "Barbell Row",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.BACK,
            secondaryMuscles = listOf(MuscleGroup.BICEPS, MuscleGroup.FOREARMS),
            description = "Compound rowing movement for back thickness.",
            instructions = "Hinge forward, pull bar to lower chest, squeeze shoulder blades.",
            defaultSets = 4,
            defaultReps = 8
        ),
        ExerciseDefinition(
            id = "back_lat_pulldown",
            name = "Lat Pulldown",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.BACK,
            secondaryMuscles = listOf(MuscleGroup.BICEPS),
            description = "Cable machine exercise for lat development.",
            defaultSets = 3,
            defaultReps = 10
        ),
        ExerciseDefinition(
            id = "back_seated_row",
            name = "Seated Cable Row",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.BACK,
            secondaryMuscles = listOf(MuscleGroup.BICEPS),
            description = "Cable rowing for back thickness and posture.",
            defaultSets = 3,
            defaultReps = 12
        ),
        ExerciseDefinition(
            id = "back_dumbbell_row",
            name = "Single-Arm Dumbbell Row",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.BACK,
            secondaryMuscles = listOf(MuscleGroup.BICEPS),
            description = "Unilateral row for balanced back development.",
            defaultSets = 3,
            defaultReps = 10
        ),

        // ============ SHOULDER EXERCISES ============
        ExerciseDefinition(
            id = "shoulder_overhead_press",
            name = "Overhead Press",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.SHOULDERS,
            secondaryMuscles = listOf(MuscleGroup.TRICEPS, MuscleGroup.CORE),
            description = "Compound pressing movement for shoulder strength.",
            instructions = "Press barbell from shoulders to overhead, lock out arms.",
            defaultSets = 4,
            defaultReps = 6
        ),
        ExerciseDefinition(
            id = "shoulder_lateral_raise",
            name = "Lateral Raise",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.SHOULDERS,
            description = "Isolation exercise for shoulder width.",
            instructions = "Raise dumbbells to sides until parallel to floor.",
            defaultSets = 3,
            defaultReps = 15
        ),
        ExerciseDefinition(
            id = "shoulder_front_raise",
            name = "Front Raise",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.SHOULDERS,
            description = "Targets front deltoid with forward arm raise.",
            defaultSets = 3,
            defaultReps = 12
        ),
        ExerciseDefinition(
            id = "shoulder_face_pull",
            name = "Face Pull",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.SHOULDERS,
            secondaryMuscles = listOf(MuscleGroup.BACK),
            description = "Cable exercise for rear delts and shoulder health.",
            defaultSets = 3,
            defaultReps = 15
        ),
        ExerciseDefinition(
            id = "shoulder_arnold_press",
            name = "Arnold Press",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.SHOULDERS,
            secondaryMuscles = listOf(MuscleGroup.TRICEPS),
            description = "Rotating dumbbell press for complete shoulder development.",
            defaultSets = 3,
            defaultReps = 10
        ),

        // ============ BICEPS EXERCISES ============
        ExerciseDefinition(
            id = "biceps_barbell_curl",
            name = "Barbell Curl",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.BICEPS,
            secondaryMuscles = listOf(MuscleGroup.FOREARMS),
            description = "Classic biceps exercise with barbell.",
            instructions = "Curl bar from thighs to shoulders, keep elbows stationary.",
            defaultSets = 3,
            defaultReps = 10
        ),
        ExerciseDefinition(
            id = "biceps_dumbbell_curl",
            name = "Dumbbell Curl",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.BICEPS,
            secondaryMuscles = listOf(MuscleGroup.FOREARMS),
            description = "Alternating or simultaneous dumbbell curls.",
            defaultSets = 3,
            defaultReps = 12
        ),
        ExerciseDefinition(
            id = "biceps_hammer_curl",
            name = "Hammer Curl",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.BICEPS,
            secondaryMuscles = listOf(MuscleGroup.FOREARMS),
            description = "Neutral grip curl for biceps and brachialis.",
            defaultSets = 3,
            defaultReps = 12
        ),
        ExerciseDefinition(
            id = "biceps_preacher_curl",
            name = "Preacher Curl",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.BICEPS,
            description = "Strict curl using preacher bench for isolation.",
            defaultSets = 3,
            defaultReps = 10
        ),
        ExerciseDefinition(
            id = "biceps_concentration_curl",
            name = "Concentration Curl",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.BICEPS,
            description = "Seated single-arm curl for peak contraction.",
            defaultSets = 3,
            defaultReps = 12
        ),

        // ============ TRICEPS EXERCISES ============
        ExerciseDefinition(
            id = "triceps_pushdown",
            name = "Cable Pushdown",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.TRICEPS,
            description = "Cable exercise for triceps using rope or bar.",
            instructions = "Push cable down while keeping elbows at sides.",
            defaultSets = 3,
            defaultReps = 12
        ),
        ExerciseDefinition(
            id = "triceps_dip",
            name = "Triceps Dip",
            category = ExerciseCategory.BODYWEIGHT,
            primaryMuscle = MuscleGroup.TRICEPS,
            secondaryMuscles = listOf(MuscleGroup.CHEST, MuscleGroup.SHOULDERS),
            description = "Bodyweight dip with upright torso for triceps focus.",
            defaultSets = 3,
            defaultReps = 10
        ),
        ExerciseDefinition(
            id = "triceps_overhead_extension",
            name = "Overhead Triceps Extension",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.TRICEPS,
            description = "Stretches and targets the long head of triceps.",
            defaultSets = 3,
            defaultReps = 12
        ),
        ExerciseDefinition(
            id = "triceps_skull_crusher",
            name = "Skull Crusher",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.TRICEPS,
            description = "Lying triceps extension with EZ bar or dumbbells.",
            defaultSets = 3,
            defaultReps = 10
        ),
        ExerciseDefinition(
            id = "triceps_close_grip_bench",
            name = "Close Grip Bench Press",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.TRICEPS,
            secondaryMuscles = listOf(MuscleGroup.CHEST, MuscleGroup.SHOULDERS),
            description = "Narrow grip bench press emphasizing triceps.",
            defaultSets = 3,
            defaultReps = 8
        ),

        // ============ QUADRICEPS EXERCISES ============
        ExerciseDefinition(
            id = "quads_squat",
            name = "Barbell Back Squat",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.QUADRICEPS,
            secondaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.HAMSTRINGS, MuscleGroup.CORE),
            description = "King of leg exercises for overall leg development.",
            instructions = "Bar on upper back, squat to parallel or below, drive up.",
            defaultSets = 4,
            defaultReps = 6
        ),
        ExerciseDefinition(
            id = "quads_front_squat",
            name = "Front Squat",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.QUADRICEPS,
            secondaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.CORE),
            description = "Quad-dominant squat with bar in front rack position.",
            defaultSets = 3,
            defaultReps = 8
        ),
        ExerciseDefinition(
            id = "quads_leg_press",
            name = "Leg Press",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.QUADRICEPS,
            secondaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.HAMSTRINGS),
            description = "Machine exercise for heavy quad loading.",
            defaultSets = 3,
            defaultReps = 10
        ),
        ExerciseDefinition(
            id = "quads_leg_extension",
            name = "Leg Extension",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.QUADRICEPS,
            description = "Isolation exercise for quad peak contraction.",
            defaultSets = 3,
            defaultReps = 12
        ),
        ExerciseDefinition(
            id = "quads_lunge",
            name = "Walking Lunge",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.QUADRICEPS,
            secondaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.HAMSTRINGS),
            description = "Dynamic lunge for leg strength and balance.",
            defaultSets = 3,
            defaultReps = 12
        ),
        ExerciseDefinition(
            id = "quads_goblet_squat",
            name = "Goblet Squat",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.QUADRICEPS,
            secondaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.CORE),
            description = "Dumbbell or kettlebell squat for beginners.",
            defaultSets = 3,
            defaultReps = 12
        ),

        // ============ HAMSTRINGS EXERCISES ============
        ExerciseDefinition(
            id = "hams_romanian_deadlift",
            name = "Romanian Deadlift",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.HAMSTRINGS,
            secondaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.BACK),
            description = "Hip hinge movement for hamstring stretch and strength.",
            instructions = "Hinge at hips with slight knee bend, lower bar along legs.",
            defaultSets = 3,
            defaultReps = 10
        ),
        ExerciseDefinition(
            id = "hams_leg_curl",
            name = "Lying Leg Curl",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.HAMSTRINGS,
            description = "Machine isolation for hamstring contraction.",
            defaultSets = 3,
            defaultReps = 12
        ),
        ExerciseDefinition(
            id = "hams_seated_leg_curl",
            name = "Seated Leg Curl",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.HAMSTRINGS,
            description = "Seated version of leg curl machine.",
            defaultSets = 3,
            defaultReps = 12
        ),
        ExerciseDefinition(
            id = "hams_good_morning",
            name = "Good Morning",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.HAMSTRINGS,
            secondaryMuscles = listOf(MuscleGroup.BACK, MuscleGroup.GLUTES),
            description = "Barbell hip hinge for posterior chain.",
            defaultSets = 3,
            defaultReps = 10
        ),

        // ============ GLUTES EXERCISES ============
        ExerciseDefinition(
            id = "glutes_hip_thrust",
            name = "Hip Thrust",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.GLUTES,
            secondaryMuscles = listOf(MuscleGroup.HAMSTRINGS),
            description = "Primary glute isolation with barbell or bodyweight.",
            instructions = "Upper back on bench, drive hips up, squeeze glutes at top.",
            defaultSets = 3,
            defaultReps = 12
        ),
        ExerciseDefinition(
            id = "glutes_glute_bridge",
            name = "Glute Bridge",
            category = ExerciseCategory.BODYWEIGHT,
            primaryMuscle = MuscleGroup.GLUTES,
            secondaryMuscles = listOf(MuscleGroup.HAMSTRINGS),
            description = "Lying glute activation exercise.",
            defaultSets = 3,
            defaultReps = 15
        ),
        ExerciseDefinition(
            id = "glutes_kickback",
            name = "Cable Kickback",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.GLUTES,
            description = "Single leg cable exercise for glute isolation.",
            defaultSets = 3,
            defaultReps = 12
        ),
        ExerciseDefinition(
            id = "glutes_bulgarian_split_squat",
            name = "Bulgarian Split Squat",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.GLUTES,
            secondaryMuscles = listOf(MuscleGroup.QUADRICEPS),
            description = "Rear foot elevated single leg squat.",
            defaultSets = 3,
            defaultReps = 10
        ),

        // ============ CALVES EXERCISES ============
        ExerciseDefinition(
            id = "calves_standing_raise",
            name = "Standing Calf Raise",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.CALVES,
            description = "Machine or barbell exercise for calf development.",
            defaultSets = 4,
            defaultReps = 15
        ),
        ExerciseDefinition(
            id = "calves_seated_raise",
            name = "Seated Calf Raise",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.CALVES,
            description = "Targets soleus muscle with bent knees.",
            defaultSets = 3,
            defaultReps = 15
        ),

        // ============ CORE EXERCISES ============
        ExerciseDefinition(
            id = "core_plank",
            name = "Plank",
            category = ExerciseCategory.BODYWEIGHT,
            primaryMuscle = MuscleGroup.CORE,
            description = "Isometric core stability exercise.",
            instructions = "Hold push-up position on forearms, keep body straight.",
            isTimeBased = true,
            defaultSets = 3,
            defaultDurationSeconds = 60
        ),
        ExerciseDefinition(
            id = "core_crunch",
            name = "Crunch",
            category = ExerciseCategory.BODYWEIGHT,
            primaryMuscle = MuscleGroup.CORE,
            description = "Basic abdominal contraction exercise.",
            defaultSets = 3,
            defaultReps = 20
        ),
        ExerciseDefinition(
            id = "core_leg_raise",
            name = "Hanging Leg Raise",
            category = ExerciseCategory.BODYWEIGHT,
            primaryMuscle = MuscleGroup.CORE,
            description = "Hanging core exercise for lower abs.",
            defaultSets = 3,
            defaultReps = 12
        ),
        ExerciseDefinition(
            id = "core_russian_twist",
            name = "Russian Twist",
            category = ExerciseCategory.BODYWEIGHT,
            primaryMuscle = MuscleGroup.CORE,
            description = "Rotational exercise for obliques.",
            defaultSets = 3,
            defaultReps = 20
        ),
        ExerciseDefinition(
            id = "core_dead_bug",
            name = "Dead Bug",
            category = ExerciseCategory.BODYWEIGHT,
            primaryMuscle = MuscleGroup.CORE,
            description = "Anti-extension core stability drill.",
            defaultSets = 3,
            defaultReps = 10
        ),
        ExerciseDefinition(
            id = "core_ab_wheel",
            name = "Ab Wheel Rollout",
            category = ExerciseCategory.BODYWEIGHT,
            primaryMuscle = MuscleGroup.CORE,
            description = "Advanced anti-extension exercise with ab wheel.",
            defaultSets = 3,
            defaultReps = 10
        ),
        ExerciseDefinition(
            id = "core_cable_woodchop",
            name = "Cable Woodchop",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.CORE,
            description = "Rotational cable exercise for functional core.",
            defaultSets = 3,
            defaultReps = 12
        ),

        // ============ CARDIO EXERCISES ============
        ExerciseDefinition(
            id = "cardio_running",
            name = "Running",
            category = ExerciseCategory.CARDIO,
            primaryMuscle = MuscleGroup.FULL_BODY,
            secondaryMuscles = listOf(MuscleGroup.QUADRICEPS, MuscleGroup.CALVES),
            description = "Basic cardiovascular running exercise.",
            isTimeBased = true,
            defaultSets = 1,
            defaultDurationSeconds = 1800
        ),
        ExerciseDefinition(
            id = "cardio_cycling",
            name = "Cycling",
            category = ExerciseCategory.CARDIO,
            primaryMuscle = MuscleGroup.FULL_BODY,
            secondaryMuscles = listOf(MuscleGroup.QUADRICEPS, MuscleGroup.GLUTES),
            description = "Stationary or outdoor cycling cardio.",
            isTimeBased = true,
            defaultSets = 1,
            defaultDurationSeconds = 1800
        ),
        ExerciseDefinition(
            id = "cardio_jump_rope",
            name = "Jump Rope",
            category = ExerciseCategory.CARDIO,
            primaryMuscle = MuscleGroup.FULL_BODY,
            secondaryMuscles = listOf(MuscleGroup.CALVES, MuscleGroup.SHOULDERS),
            description = "Classic cardio and coordination exercise.",
            isTimeBased = true,
            defaultSets = 3,
            defaultDurationSeconds = 180
        ),
        ExerciseDefinition(
            id = "cardio_rowing",
            name = "Rowing Machine",
            category = ExerciseCategory.CARDIO,
            primaryMuscle = MuscleGroup.FULL_BODY,
            secondaryMuscles = listOf(MuscleGroup.BACK, MuscleGroup.QUADRICEPS),
            description = "Full body cardio on rowing ergometer.",
            isTimeBased = true,
            defaultSets = 1,
            defaultDurationSeconds = 1200
        ),

        // ============ PLYOMETRIC EXERCISES ============
        ExerciseDefinition(
            id = "plyo_burpee",
            name = "Burpee",
            category = ExerciseCategory.PLYOMETRIC,
            primaryMuscle = MuscleGroup.FULL_BODY,
            description = "Full body explosive conditioning exercise.",
            instructions = "Squat down, jump feet back, push-up, jump feet forward, jump up.",
            defaultSets = 3,
            defaultReps = 10
        ),
        ExerciseDefinition(
            id = "plyo_box_jump",
            name = "Box Jump",
            category = ExerciseCategory.PLYOMETRIC,
            primaryMuscle = MuscleGroup.QUADRICEPS,
            secondaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.CALVES),
            description = "Explosive jump onto elevated platform.",
            defaultSets = 3,
            defaultReps = 8
        ),
        ExerciseDefinition(
            id = "plyo_mountain_climber",
            name = "Mountain Climber",
            category = ExerciseCategory.PLYOMETRIC,
            primaryMuscle = MuscleGroup.CORE,
            secondaryMuscles = listOf(MuscleGroup.QUADRICEPS, MuscleGroup.SHOULDERS),
            description = "Dynamic plank with alternating knee drives.",
            isTimeBased = true,
            defaultSets = 3,
            defaultDurationSeconds = 30
        ),
        ExerciseDefinition(
            id = "plyo_jump_squat",
            name = "Jump Squat",
            category = ExerciseCategory.PLYOMETRIC,
            primaryMuscle = MuscleGroup.QUADRICEPS,
            secondaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.CALVES),
            description = "Explosive bodyweight squat with jump.",
            defaultSets = 3,
            defaultReps = 12
        ),

        // ============ FLEXIBILITY EXERCISES ============
        ExerciseDefinition(
            id = "flex_downward_dog",
            name = "Downward Dog",
            category = ExerciseCategory.FLEXIBILITY,
            primaryMuscle = MuscleGroup.FULL_BODY,
            secondaryMuscles = listOf(MuscleGroup.HAMSTRINGS, MuscleGroup.SHOULDERS),
            description = "Yoga pose for full body stretch.",
            isTimeBased = true,
            defaultSets = 1,
            defaultDurationSeconds = 60
        ),
        ExerciseDefinition(
            id = "flex_pigeon_pose",
            name = "Pigeon Pose",
            category = ExerciseCategory.FLEXIBILITY,
            primaryMuscle = MuscleGroup.GLUTES,
            secondaryMuscles = listOf(MuscleGroup.HAMSTRINGS),
            description = "Deep hip flexor and glute stretch.",
            isTimeBased = true,
            defaultSets = 1,
            defaultDurationSeconds = 60
        ),
        ExerciseDefinition(
            id = "flex_cobra_stretch",
            name = "Cobra Stretch",
            category = ExerciseCategory.FLEXIBILITY,
            primaryMuscle = MuscleGroup.CORE,
            description = "Back extension stretch for abs and spine.",
            isTimeBased = true,
            defaultSets = 1,
            defaultDurationSeconds = 30
        ),
        ExerciseDefinition(
            id = "flex_cat_cow",
            name = "Cat-Cow Stretch",
            category = ExerciseCategory.FLEXIBILITY,
            primaryMuscle = MuscleGroup.BACK,
            secondaryMuscles = listOf(MuscleGroup.CORE),
            description = "Spinal mobility flow exercise.",
            defaultSets = 1,
            defaultReps = 10
        ),
        // ============ ADDITIONAL EXERCISES (sourced from free-exercise-db) ============
        ExerciseDefinition(
            id = "db_Butterfly",
            name = "Butterfly",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.CHEST,
            description = "Sit on the machine with your back flat on the pad.",
            instructions = "Sit on the machine with your back flat on the pad. Take hold of the handles. Tip: Your upper arms should be positioned parallel to the floor; adjust the machine accordingly. This will be your starting position. Push the handles together slowly as you squeeze your chest in the middle. Breathe out during this part of the motion and hold the contraction for a second. Return back to the starting position slowly as you inhale until your chest muscles are fully stretched. Repeat for the...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Butterfly/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Butterfly/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Cable_Chest_Press",
            name = "Cable Chest Press",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.CHEST,
            secondaryMuscles = listOf(MuscleGroup.SHOULDERS, MuscleGroup.TRICEPS),
            description = "Adjust the weight to an appropriate amount and be seated, grasping the handles.",
            instructions = "Adjust the weight to an appropriate amount and be seated, grasping the handles. Your upper arms should be about 45 degrees to the body, with your head and chest up. The elbows should be bent to about 90 degrees. This will be your starting position. Begin by extending through the elbow, pressing the handles together straight in front of you. Keep your shoulder blades retracted as you execute the movement. After pausing at full extension, return to th starting position, keeping tension on the...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Cable_Chest_Press/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Cable_Chest_Press/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Decline_Push-Up",
            name = "Decline Push-Up",
            category = ExerciseCategory.BODYWEIGHT,
            primaryMuscle = MuscleGroup.CHEST,
            secondaryMuscles = listOf(MuscleGroup.SHOULDERS, MuscleGroup.TRICEPS),
            description = "Lie on the floor face down and place your hands about 36 inches apart while holding your torso up at arms length.",
            instructions = "Lie on the floor face down and place your hands about 36 inches apart while holding your torso up at arms length. Move your feet up to a box or bench. This will be your starting position. Next, lower yourself downward until your chest almost touches the floor as you inhale. Now breathe out and press your upper body back up to the starting position while squeezing your chest. After a brief pause at the top contracted position, you can begin to lower yourself downward again for as many...",
            defaultSets = 3,
            defaultReps = 12,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Decline_Push-Up/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Decline_Push-Up/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Incline_Push-Up",
            name = "Incline Push-Up",
            category = ExerciseCategory.BODYWEIGHT,
            primaryMuscle = MuscleGroup.CHEST,
            secondaryMuscles = listOf(MuscleGroup.SHOULDERS, MuscleGroup.TRICEPS),
            description = "Stand facing bench or sturdy elevated platform.",
            instructions = "Stand facing bench or sturdy elevated platform. Place hands on edge of bench or platform, slightly wider than shoulder width. Position forefoot back from bench or platform with arms and body straight. Arms should be perpendicular to body. Keeping body straight, lower chest to edge of box or platform by bending arms. Push body up until arms are extended. Repeat.",
            defaultSets = 3,
            defaultReps = 12,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Incline_Push-Up/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Incline_Push-Up/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Svend_Press",
            name = "Svend Press",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.CHEST,
            secondaryMuscles = listOf(MuscleGroup.FOREARMS, MuscleGroup.SHOULDERS, MuscleGroup.TRICEPS),
            description = "Begin in a standing position.",
            instructions = "Begin in a standing position. Press two lightweight plates together with your hands. Hold the plates together close to your chest to create an isometric contraction in your chest muscles. Your fingers should be pointed forward. This is your starting position. Squeeze the plates between your palms and extend your arms directly out in front of you in a controlled motion. Pause at the top of the motion, and then slowly return to the starting position.",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Svend_Press/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Svend_Press/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Chin-Up",
            name = "Chin-Up",
            category = ExerciseCategory.BODYWEIGHT,
            primaryMuscle = MuscleGroup.BACK,
            secondaryMuscles = listOf(MuscleGroup.BICEPS, MuscleGroup.FOREARMS),
            description = "Grab the pull-up bar with the palms facing your torso and a grip closer than the shoulder width.",
            instructions = "Grab the pull-up bar with the palms facing your torso and a grip closer than the shoulder width. As you have both arms extended in front of you holding the bar at the chosen grip width, keep your torso as straight as possible while creating a curvature on your lower back and sticking your chest out. This is your starting position. Tip: Keeping the torso as straight as possible maximizes biceps stimulation while minimizing back involvement. As you breathe out, pull your torso up until your...",
            defaultSets = 3,
            defaultReps = 12,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Chin-Up/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Chin-Up/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Band_Assisted_Pull-Up",
            name = "Band Assisted Pull-Up",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.BACK,
            secondaryMuscles = listOf(MuscleGroup.CORE, MuscleGroup.FOREARMS),
            description = "Choke the band around the center of the pullup bar.",
            instructions = "Choke the band around the center of the pullup bar. You can use different bands to provide varying levels of assistance. Pull the end of the band down, and place one bent knee into the loop, ensuring it won't slip out. Take a medium to wide grip on the bar. This will be your starting position. Pull yourself upward by contracting the lats as you flex the elbow. The elbow should be driven to your side. Pull to the front, attempting to get your chin over the bar. Avoid swinging or jerking...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Band_Assisted_Pull-Up/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Band_Assisted_Pull-Up/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Inverted_Row",
            name = "Inverted Row",
            category = ExerciseCategory.BODYWEIGHT,
            primaryMuscle = MuscleGroup.BACK,
            description = "Position a bar in a rack to about waist height.",
            instructions = "Position a bar in a rack to about waist height. You can also use a smith machine. Take a wider than shoulder width grip on the bar and position yourself hanging underneath the bar. Your body should be straight with your heels on the ground with your arms fully extended. This will be your starting position. Begin by flexing the elbow, pulling your chest towards the bar. Retract your shoulder blades as you perform the movement. Pause at the top of the motion, and return yourself to the start...",
            defaultSets = 3,
            defaultReps = 12,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Inverted_Row/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Inverted_Row/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_T-Bar_Row_with_Handle",
            name = "T-Bar Row with Handle",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.BACK,
            secondaryMuscles = listOf(MuscleGroup.BICEPS),
            description = "Position a bar into a landmine or in a corner to keep it from moving.",
            instructions = "Position a bar into a landmine or in a corner to keep it from moving. Load an appropriate weight onto your end. Stand over the bar, and position a Double D row handle around the bar next to the collar. Using your hips and legs, rise to a standing position. Assume a wide stance with your hips back and your chest up. Your arms should be extended. This will be your starting position. Pull the weight to your upper abdomen by retracting the shoulder blades and flexing the elbows. Do not jerk the...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/T-Bar_Row_with_Handle/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/T-Bar_Row_with_Handle/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Barbell_Shrug",
            name = "Barbell Shrug",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.BACK,
            description = "Stand up straight with your feet at shoulder width as you hold a barbell with both hands in front of you using a pronated grip (palms facing the thighs).",
            instructions = "Stand up straight with your feet at shoulder width as you hold a barbell with both hands in front of you using a pronated grip (palms facing the thighs). Tip: Your hands should be a little wider than shoulder width apart. You can use wrist wraps for this exercise for a better grip. This will be your starting position. Raise your shoulders up as far as you can go as you breathe out and hold the contraction for a second. Tip: Refrain from trying to lift the barbell by using your biceps. Slowly...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Barbell_Shrug/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Barbell_Shrug/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Dumbbell_Shrug",
            name = "Dumbbell Shrug",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.BACK,
            description = "Stand erect with a dumbbell on each hand (palms facing your torso), arms extended on the sides.",
            instructions = "Stand erect with a dumbbell on each hand (palms facing your torso), arms extended on the sides. Lift the dumbbells by elevating the shoulders as high as possible while you exhale. Hold the contraction at the top for a second. Tip: The arms should remain extended at all times. Refrain from using the biceps to help lift the dumbbells. Only the shoulders should be moving up and down. Lower the dumbbells back to the original position. Repeat for the recommended amount of repetitions.",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Dumbbell_Shrug/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Dumbbell_Shrug/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Superman",
            name = "Superman",
            category = ExerciseCategory.FLEXIBILITY,
            primaryMuscle = MuscleGroup.BACK,
            secondaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.HAMSTRINGS),
            description = "To begin, lie straight and face down on the floor or exercise mat.",
            instructions = "To begin, lie straight and face down on the floor or exercise mat. Your arms should be fully extended in front of you. This is the starting position. Simultaneously raise your arms, legs, and chest off of the floor and hold this contraction for 2 seconds. Tip: Squeeze your lower back to get the best results from this exercise. Remember to exhale during this movement. Note: When holding the contracted position, you should look like superman when he is flying. Slowly begin to lower your arms,...",
            isTimeBased = true,
            defaultSets = 1,
            defaultDurationSeconds = 30,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Superman/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Superman/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Alternating_Renegade_Row",
            name = "Alternating Renegade Row",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.BACK,
            secondaryMuscles = listOf(MuscleGroup.CORE, MuscleGroup.BICEPS, MuscleGroup.CHEST, MuscleGroup.TRICEPS),
            description = "Place two kettlebells on the floor about shoulder width apart.",
            instructions = "Place two kettlebells on the floor about shoulder width apart. Position yourself on your toes and your hands as though you were doing a pushup, with the body straight and extended. Use the handles of the kettlebells to support your upper body. You may need to position your feet wide for support. Push one kettlebell into the floor and row the other kettlebell, retracting the shoulder blade of the working side as you flex the elbow, pulling it to your side. Then lower the kettlebell to the...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Alternating_Renegade_Row/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Alternating_Renegade_Row/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Standing_Military_Press",
            name = "Standing Military Press",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.SHOULDERS,
            secondaryMuscles = listOf(MuscleGroup.TRICEPS),
            description = "Start by placing a barbell that is about chest high on a squat rack.",
            instructions = "Start by placing a barbell that is about chest high on a squat rack. Once you have selected the weights, grab the barbell using a pronated (palms facing forward) grip. Make sure to grip the bar wider than shoulder width apart from each other. Slightly bend the knees and place the barbell on your collar bone. Lift the barbell up keeping it lying on your chest. Take a step back and position your feet shoulder width apart from each other. Once you pick up the barbell with the correct grip...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Standing_Military_Press/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Standing_Military_Press/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Push_Press",
            name = "Push Press",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.SHOULDERS,
            secondaryMuscles = listOf(MuscleGroup.QUADRICEPS, MuscleGroup.TRICEPS),
            description = "Push Press.",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Push_Press/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Push_Press/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Reverse_Flyes",
            name = "Reverse Flyes",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.SHOULDERS,
            description = "To begin, lie down on an incline bench with the chest and stomach pressing against the incline.",
            instructions = "To begin, lie down on an incline bench with the chest and stomach pressing against the incline. Have the dumbbells in each hand with the palms facing each other (neutral grip). Extend the arms in front of you so that they are perpendicular to the angle of the bench. The legs should be stationary while applying pressure with the ball of your toes. This is the starting position. Maintaining the slight bend of the elbows, move the weights out and away from each other (to the side) in an arc...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Reverse_Flyes/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Reverse_Flyes/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Standing_Dumbbell_Upright_Row",
            name = "Standing Dumbbell Upright Row",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.BACK,
            secondaryMuscles = listOf(MuscleGroup.BICEPS, MuscleGroup.SHOULDERS),
            description = "Grasp a dumbbell in each hand with a pronated (palms forward) grip that is slightly less than shoulder width.",
            instructions = "Grasp a dumbbell in each hand with a pronated (palms forward) grip that is slightly less than shoulder width. The dumbbells should be resting on top of your thighs. Your arms should be extended with a slight bend at the elbows and your back should be straight. This will be your starting position. Use your side shoulders to lift the dumbbells as you exhale. The dumbbells should be close to the body as you move it up and the elbows should drive the motion. Continue to lift them until they...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Standing_Dumbbell_Upright_Row/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Standing_Dumbbell_Upright_Row/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Band_Pull_Apart",
            name = "Band Pull Apart",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.SHOULDERS,
            secondaryMuscles = listOf(MuscleGroup.BACK),
            description = "Begin with your arms extended straight out in front of you, holding the band with both hands.",
            instructions = "Begin with your arms extended straight out in front of you, holding the band with both hands. Initiate the movement by performing a reverse fly motion, moving your hands out laterally to your sides. Keep your elbows extended as you perform the movement, bringing the band to your chest. Ensure that you keep your shoulders back during the exercise. Pause as you complete the movement, returning to the starting position under control.",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Band_Pull_Apart/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Band_Pull_Apart/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Cuban_Press",
            name = "Cuban Press",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.SHOULDERS,
            secondaryMuscles = listOf(MuscleGroup.BACK),
            description = "Take a dumbbell in each hand with a pronated grip in a standing position.",
            instructions = "Take a dumbbell in each hand with a pronated grip in a standing position. Raise your upper arms so that they are parallel to the floor, allowing your lower arms to hang in the \"scarecrow\" position. This will be your starting position. To initiate the movement, externally rotate the shoulders to move the upper arm 180 degrees. Keep the upper arms in place, rotating the upper arms until the wrists are directly above the elbows, the forearms perpendicular to the floor. Now press the dumbbells...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Cuban_Press/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Cuban_Press/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Zottman_Curl",
            name = "Zottman Curl",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.BICEPS,
            secondaryMuscles = listOf(MuscleGroup.FOREARMS),
            description = "Stand up with your torso upright and a dumbbell in each hand being held at arms length.",
            instructions = "Stand up with your torso upright and a dumbbell in each hand being held at arms length. The elbows should be close to the torso. Make sure the palms of the hands are facing each other. This will be your starting position. While holding the upper arm stationary, curl the weights while contracting the biceps as you breathe out. Only the forearms should move. Your wrist should rotate so that you have a supinated (palms up) grip. Continue the movement until your biceps are fully contracted and...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Zottman_Curl/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Zottman_Curl/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_High_Cable_Curls",
            name = "High Cable Curls",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.BICEPS,
            description = "Stand between a couple of high pulleys and grab a handle in each arm.",
            instructions = "Stand between a couple of high pulleys and grab a handle in each arm. Position your upper arms in a way that they are parallel to the floor with the palms of your hands facing you. This will be your starting position. Curl the handles towards you until they are next to your ears. Make sure that as you do so you flex your biceps and exhale. The upper arms should remain stationary and only the forearms should move. Hold for a second in the contracted position as you squeeze the biceps. Slowly...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/High_Cable_Curls/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/High_Cable_Curls/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Spider_Curl",
            name = "Spider Curl",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.BICEPS,
            description = "Start out by setting the bar on the part of the preacher bench that you would normally sit on.",
            instructions = "Start out by setting the bar on the part of the preacher bench that you would normally sit on. Make sure to align the barbell properly so that it is balanced and will not fall off. Move to the front side of the preacher bench (the part where the arms usually lay) and position yourself to lay at a 45 degree slant with your torso and stomach pressed against the front side of the preacher bench. Make sure that your feet (especially the toes) are well positioned on the floor and place your upper...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Spider_Curl/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Spider_Curl/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Standing_Dumbbell_Reverse_Curl",
            name = "Standing Dumbbell Reverse Curl",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.BICEPS,
            secondaryMuscles = listOf(MuscleGroup.FOREARMS),
            description = "To begin, stand straight with a dumbbell in each hand using a pronated grip (palms facing down).",
            instructions = "To begin, stand straight with a dumbbell in each hand using a pronated grip (palms facing down). Your arms should be fully extended while your feet are shoulder width apart from each other. This is the starting position. While holding the upper arms stationary, curl the weights while contracting the biceps as you breathe out. Only the forearms should move. Continue the movement until your biceps are fully contracted and the dumbbells are at shoulder level. Hold the contracted position for a...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Standing_Dumbbell_Reverse_Curl/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Standing_Dumbbell_Reverse_Curl/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Drag_Curl",
            name = "Drag Curl",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.BICEPS,
            secondaryMuscles = listOf(MuscleGroup.FOREARMS),
            description = "Grab a barbell with a supinated grip (palms facing forward) and get your elbows close to your torso and back.",
            instructions = "Grab a barbell with a supinated grip (palms facing forward) and get your elbows close to your torso and back. This will be your starting position. As you exhale, curl the bar up while keeping the elbows to the back as you \"Drag\" the bar up by keeping it in contact with your torso. Tip: As you can see, you will not be keeping the elbows pinned to your sides, but instead you will be bringing them back. Also, do not lift your shoulders. Slowly go back to the starting position as you keep the...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Drag_Curl/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Drag_Curl/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Tate_Press",
            name = "Tate Press",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.TRICEPS,
            secondaryMuscles = listOf(MuscleGroup.CHEST, MuscleGroup.SHOULDERS),
            description = "Lie down on a flat bench with a dumbbell in each hand on top of your thighs.",
            instructions = "Lie down on a flat bench with a dumbbell in each hand on top of your thighs. The palms of your hand will be facing each other. By using your thighs to help you get the dumbbells up, clean the dumbbells one arm at a time so that you can hold them in front of you at shoulder width. Note: when holding the dumbbells in front of you, make sure your arms are wider than shoulder width apart from each other using a pronated (palms forward) grip. Allow your elbows to point out. This is your starting...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Tate_Press/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Tate_Press/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_JM_Press",
            name = "JM Press",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.TRICEPS,
            secondaryMuscles = listOf(MuscleGroup.CHEST, MuscleGroup.SHOULDERS),
            description = "Start the exercise the same way you would a close grip bench press.",
            instructions = "Start the exercise the same way you would a close grip bench press. You will lie on a flat bench while holding a barbell at arms length (fully extended) with the elbows in. However, instead of having the arms perpendicular to the torso, make sure the bar is set in a direct line above the upper chest. This will be your starting position. Now beginning from a fully extended position lower the bar down as if performing a lying triceps extension. Inhale as you perform this movement. When you...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/JM_Press/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/JM_Press/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Bench_Dips",
            name = "Bench Dips",
            category = ExerciseCategory.BODYWEIGHT,
            primaryMuscle = MuscleGroup.TRICEPS,
            secondaryMuscles = listOf(MuscleGroup.CHEST, MuscleGroup.SHOULDERS),
            description = "For this exercise you will need to place a bench behind your back.",
            instructions = "For this exercise you will need to place a bench behind your back. With the bench perpendicular to your body, and while looking away from it, hold on to the bench on its edge with the hands fully extended, separated at shoulder width. The legs will be extended forward, bent at the waist and perpendicular to your torso. This will be your starting position. Slowly lower your body as you inhale by bending at the elbows until you lower yourself far enough to where there is an angle slightly...",
            defaultSets = 3,
            defaultReps = 12,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Bench_Dips/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Bench_Dips/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Tricep_Dumbbell_Kickback",
            name = "Tricep Dumbbell Kickback",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.TRICEPS,
            description = "Start with a dumbbell in each hand and your palms facing your torso.",
            instructions = "Start with a dumbbell in each hand and your palms facing your torso. Keep your back straight with a slight bend in the knees and bend forward at the waist. Your torso should be almost parallel to the floor. Make sure to keep your head up. Your upper arms should be close to your torso and parallel to the floor. Your forearms should be pointed towards the floor as you hold the weights. There should be a 90-degree angle formed between your forearm and upper arm. This is your starting position....",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Tricep_Dumbbell_Kickback/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Tricep_Dumbbell_Kickback/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Cable_Wrist_Curl",
            name = "Cable Wrist Curl",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.FOREARMS,
            description = "Start out by placing a flat bench in front of a low pulley cable that has a straight bar attachment.",
            instructions = "Start out by placing a flat bench in front of a low pulley cable that has a straight bar attachment. Use your arms to grab the cable bar with a narrow to shoulder width supinated grip (palms up) and bring them up so that your forearms are resting against the top of your thighs. Your wrists should be hanging just beyond your knees. Start out by curling your wrist upwards and exhaling. Keep the contraction for a second. Slowly lower your wrists back down to the starting position while...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Cable_Wrist_Curl/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Cable_Wrist_Curl/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Palms-Up_Barbell_Wrist_Curl_Over_A_Bench",
            name = "Palms-Up Barbell Wrist Curl Over A Bench",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.FOREARMS,
            description = "Start out by placing a barbell on one side of a flat bench.",
            instructions = "Start out by placing a barbell on one side of a flat bench. Kneel down on both of your knees so that your body is facing the flat bench. Use your arms to grab the barbell with a supinated grip (palms up) and bring them up so that your forearms are resting against the flat bench. Your wrists should be hanging over the edge. Start out by curling your wrist upwards and exhaling. Slowly lower your wrists back down to the starting position while inhaling. Your forearms should be stationary as...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Palms-Up_Barbell_Wrist_Curl_Over_A_Bench/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Palms-Up_Barbell_Wrist_Curl_Over_A_Bench/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Farmers_Walk",
            name = "Farmer's Walk",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.FOREARMS,
            secondaryMuscles = listOf(MuscleGroup.CORE, MuscleGroup.GLUTES, MuscleGroup.HAMSTRINGS, MuscleGroup.BACK, MuscleGroup.QUADRICEPS),
            description = "There are various implements that can be used for the farmers walk.",
            instructions = "There are various implements that can be used for the farmers walk. These can also be performed with heavy dumbbells or short bars if these implements aren't available. Begin by standing between the implements. After gripping the handles, lift them up by driving through your heels, keeping your back straight and your head up. Walk taking short, quick steps, and don't forget to breathe. Move for a given distance, typically 50-100 feet, as fast as possible.",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Farmers_Walk/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Farmers_Walk/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Plate_Pinch",
            name = "Plate Pinch",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.FOREARMS,
            description = "Grab two wide-rimmed plates and put them together with the smooth sides facing outward Use your fingers to grip the outside part of the plate and your thumb...",
            instructions = "Grab two wide-rimmed plates and put them together with the smooth sides facing outward Use your fingers to grip the outside part of the plate and your thumb for the other side thus holding both plates together. This is the starting position. Squeeze the plate with your fingers and thumb. Hold this position for as long as you can. Repeat for the recommended amount of sets prescribed in your program. Switch arms and repeat the movements.",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Plate_Pinch/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Plate_Pinch/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Weighted_Sissy_Squat",
            name = "Weighted Sissy Squat",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.QUADRICEPS,
            secondaryMuscles = listOf(MuscleGroup.CALVES, MuscleGroup.GLUTES, MuscleGroup.HAMSTRINGS),
            description = "Standing upright, with feet at shoulder width and toes raised, use one hand to hold onto the beams of a squat rack and the opposite arm to hold a plate on...",
            instructions = "Standing upright, with feet at shoulder width and toes raised, use one hand to hold onto the beams of a squat rack and the opposite arm to hold a plate on top of your chest. This is your starting position. As you use one arm to hold yourself, bend at the knees and slowly lower your torso toward the ground by bringing your pelvis and knees forward. Inhale as you go down and stop when your upper and lower legs almost create a 90-degree angle. Hold the stretch position for a second. After your...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Weighted_Sissy_Squat/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Weighted_Sissy_Squat/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Hack_Squat",
            name = "Hack Squat",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.QUADRICEPS,
            secondaryMuscles = listOf(MuscleGroup.CALVES, MuscleGroup.GLUTES, MuscleGroup.HAMSTRINGS),
            description = "Place the back of your torso against the back pad of the machine and hook your shoulders under the shoulder pads provided.",
            instructions = "Place the back of your torso against the back pad of the machine and hook your shoulders under the shoulder pads provided. Position your legs in the platform using a shoulder width medium stance with the toes slightly pointed out. Tip: Keep your head up at all times and also maintain the back on the pad at all times. Place your arms on the side handles of the machine and disengage the safety bars (which on most designs is done by moving the side handles from a facing front position to a...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Hack_Squat/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Hack_Squat/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Step-up_with_Knee_Raise",
            name = "Step-up with Knee Raise",
            category = ExerciseCategory.BODYWEIGHT,
            primaryMuscle = MuscleGroup.GLUTES,
            secondaryMuscles = listOf(MuscleGroup.HAMSTRINGS, MuscleGroup.QUADRICEPS),
            description = "Stand facing a box or bench of an appropriate height with your feet together.",
            instructions = "Stand facing a box or bench of an appropriate height with your feet together. This will be your starting position. Begin the movement by stepping up, putting your left foot on the top of the bench. Extend through the hip and knee of your front leg to stand up on the box. As you stand on the box with your left leg, flex your right knee and hip, bringing your knee as high as you can. Reverse this motion to step down off the box, and then repeat the sequence on the opposite leg.",
            defaultSets = 3,
            defaultReps = 12,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Step-up_with_Knee_Raise/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Step-up_with_Knee_Raise/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Kettlebell_Pistol_Squat",
            name = "Kettlebell Pistol Squat",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.QUADRICEPS,
            secondaryMuscles = listOf(MuscleGroup.CALVES, MuscleGroup.GLUTES, MuscleGroup.HAMSTRINGS, MuscleGroup.SHOULDERS),
            description = "Pick up a kettlebell with two hands and hold it by the horns.",
            instructions = "Pick up a kettlebell with two hands and hold it by the horns. Hold one leg off of the floor and squat down on the other. Squat down by flexing the knee and sitting back with the hips, holding the kettlebell up in front of you. Hold the bottom position for a second and then reverse the motion, driving through the heel and keeping your head and chest up. Lower yourself again and repeat.",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Kettlebell_Pistol_Squat/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Kettlebell_Pistol_Squat/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Crossover_Reverse_Lunge",
            name = "Crossover Reverse Lunge",
            category = ExerciseCategory.FLEXIBILITY,
            primaryMuscle = MuscleGroup.BACK,
            secondaryMuscles = listOf(MuscleGroup.CORE, MuscleGroup.GLUTES, MuscleGroup.HAMSTRINGS, MuscleGroup.QUADRICEPS),
            description = "Stand with your feet shoulder width apart.",
            instructions = "Stand with your feet shoulder width apart. This will be your starting position. Perform a rear lunge by stepping back with one foot and flexing the hips and front knee. As you do so, rotate your torso across the front leg. After a brief pause, return to the starting position and repeat on the other side, continuing in an alternating fashion.",
            isTimeBased = true,
            defaultSets = 1,
            defaultDurationSeconds = 30,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Crossover_Reverse_Lunge/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Crossover_Reverse_Lunge/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Sumo_Deadlift",
            name = "Sumo Deadlift",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.HAMSTRINGS,
            secondaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.FOREARMS, MuscleGroup.BACK, MuscleGroup.QUADRICEPS),
            description = "Begin with a bar loaded on the ground.",
            instructions = "Begin with a bar loaded on the ground. Approach the bar so that the bar intersects the middle of the feet. The feet should be set very wide, near the collars. Bend at the hips to grip the bar. The arms should be directly below the shoulders, inside the legs, and you can use a pronated grip, a mixed grip, or hook grip. Relax the shoulders, which in effect lengthens your arms. Take a breath, and then lower your hips, looking forward with your head with your chest up. Drive through the floor,...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Sumo_Deadlift/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Sumo_Deadlift/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Donkey_Calf_Raises",
            name = "Donkey Calf Raises",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.CALVES,
            description = "For this exercise you will need access to a donkey calf raise machine.",
            instructions = "For this exercise you will need access to a donkey calf raise machine. Start by positioning your lower back and hips under the padded lever provided. The tailbone area should be the one making contact with the pad. Place both of your arms on the side handles and place the balls of your feet on the calf block with the heels extending off. Align the toes forward, inward or outward, depending on the area you wish to target, and straighten the knees without locking them. This will be your...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Donkey_Calf_Raises/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Donkey_Calf_Raises/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_3_4_Sit-Up",
            name = "3/4 Sit-Up",
            category = ExerciseCategory.BODYWEIGHT,
            primaryMuscle = MuscleGroup.CORE,
            description = "Lie down on the floor and secure your feet.",
            instructions = "Lie down on the floor and secure your feet. Your legs should be bent at the knees. Place your hands behind or to the side of your head. You will begin with your back on the ground. This will be your starting position. Flex your hips and spine to raise your torso toward your knees. At the top of the contraction your torso should be perpendicular to the ground. Reverse the motion, going only ¾ of the way down. Repeat for the recommended amount of repetitions.",
            defaultSets = 3,
            defaultReps = 12,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/3_4_Sit-Up/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/3_4_Sit-Up/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Reverse_Crunch",
            name = "Reverse Crunch",
            category = ExerciseCategory.BODYWEIGHT,
            primaryMuscle = MuscleGroup.CORE,
            description = "Lie down on the floor with your legs fully extended and arms to the side of your torso with the palms on the floor.",
            instructions = "Lie down on the floor with your legs fully extended and arms to the side of your torso with the palms on the floor. Your arms should be stationary for the entire exercise. Move your legs up so that your thighs are perpendicular to the floor and feet are together and parallel to the floor. This is the starting position. While inhaling, move your legs towards the torso as you roll your pelvis backwards and you raise your hips off the floor. At the end of this movement your knees will be...",
            defaultSets = 3,
            defaultReps = 12,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Reverse_Crunch/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Reverse_Crunch/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Push_Up_to_Side_Plank",
            name = "Push Up to Side Plank",
            category = ExerciseCategory.BODYWEIGHT,
            primaryMuscle = MuscleGroup.CHEST,
            secondaryMuscles = listOf(MuscleGroup.CORE, MuscleGroup.SHOULDERS, MuscleGroup.TRICEPS),
            description = "Get into pushup position on the toes with your hands just outside of shoulder width.",
            instructions = "Get into pushup position on the toes with your hands just outside of shoulder width. Perform a pushup by allowing the elbows to flex. As you descend, keep your body straight. Do one pushup and as you come up, shift your weight on the left side of the body, twist to the side while bringing the right arm up towards the ceiling in a side plank. Lower the arm back to the floor for another pushup and then twist to the other side. Repeat the series, alternating each side, for 10 or more reps.",
            defaultSets = 3,
            defaultReps = 12,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Push_Up_to_Side_Plank/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Push_Up_to_Side_Plank/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Flutter_Kicks",
            name = "Flutter Kicks",
            category = ExerciseCategory.BODYWEIGHT,
            primaryMuscle = MuscleGroup.GLUTES,
            secondaryMuscles = listOf(MuscleGroup.HAMSTRINGS),
            description = "On a flat bench lie facedown with the hips on the edge of the bench, the legs straight with toes high off the floor and with the arms on top of the bench...",
            instructions = "On a flat bench lie facedown with the hips on the edge of the bench, the legs straight with toes high off the floor and with the arms on top of the bench holding on to the front edge. Squeeze your glutes and hamstrings and straighten the legs until they are level with the hips. This will be your starting position. Start the movement by lifting the left leg higher than the right leg. Then lower the left leg as you lift the right leg. Continue alternating in this manner (as though you are...",
            defaultSets = 3,
            defaultReps = 12,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Flutter_Kicks/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Flutter_Kicks/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Scissor_Kick",
            name = "Scissor Kick",
            category = ExerciseCategory.FLEXIBILITY,
            primaryMuscle = MuscleGroup.CORE,
            description = "To begin, lie down with your back pressed against the floor or on an exercise mat (optional).",
            instructions = "To begin, lie down with your back pressed against the floor or on an exercise mat (optional). Your arms should be fully extended to the sides with your palms facing down. Note: The arms should be stationary the entire time. With a slight bend at the knees, lift your legs up so that your heels are about 6 inches off the ground. This is the starting position. Now lift your left leg up to about a 45 degree angle while your right leg is lowered until the heel is about 2-3 inches from the ground....",
            isTimeBased = true,
            defaultSets = 1,
            defaultDurationSeconds = 30,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Scissor_Kick/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Scissor_Kick/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Pallof_Press",
            name = "Pallof Press",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.CORE,
            secondaryMuscles = listOf(MuscleGroup.CHEST, MuscleGroup.SHOULDERS, MuscleGroup.TRICEPS),
            description = "Connect a standard handle to a tower, and—if possible—position the cable to shoulder height.",
            instructions = "Connect a standard handle to a tower, and—if possible—position the cable to shoulder height. If not, a low pulley will suffice. With your side to the cable, grab the handle with both hands and step away from the tower. You should be approximately arm's length away from the pulley, with the tension of the weight on the cable. With your feet positioned hip-width apart and knees slightly bent, hold the cable to the middle of your chest. This will be your starting position. Press the cable away...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Pallof_Press/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Pallof_Press/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Sled_Push",
            name = "Sled Push",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.QUADRICEPS,
            secondaryMuscles = listOf(MuscleGroup.CALVES, MuscleGroup.CHEST, MuscleGroup.GLUTES, MuscleGroup.HAMSTRINGS, MuscleGroup.TRICEPS),
            description = "Load your pushing sled with the desired weight.",
            instructions = "Load your pushing sled with the desired weight. Take an athletic posture, leaning into the sled with your arms fully extended, grasping the handles. Push the sled as fast as possible, focusing on extending your hips and knees to strengthen your posterior chain.",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Sled_Push/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Sled_Push/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Knee_Tuck_Jump",
            name = "Knee Tuck Jump",
            category = ExerciseCategory.PLYOMETRIC,
            primaryMuscle = MuscleGroup.HAMSTRINGS,
            secondaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.CALVES, MuscleGroup.QUADRICEPS),
            description = "Begin in a comfortable standing position with your knees slightly bent.",
            instructions = "Begin in a comfortable standing position with your knees slightly bent. Hold your hands in front of you, palms down with your fingertips together at chest height. This will be your starting position. Rapidly dip down into a quarter squat and immediately explode upward. Drive the knees towards the chest, attempting to touch them to the palms of the hands. Jump as high as you can, raising your knees up, and then ensure a good land be re-extending your legs, absorbing impact through be allowing...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Knee_Tuck_Jump/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Knee_Tuck_Jump/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_One-Arm_Medicine_Ball_Slam",
            name = "One-Arm Medicine Ball Slam",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.CORE,
            secondaryMuscles = listOf(MuscleGroup.BACK, MuscleGroup.SHOULDERS),
            description = "Start in a standing position with a staggered, athletic stance.",
            instructions = "Start in a standing position with a staggered, athletic stance. Hold a medicine ball in one hand, on the same side as your back leg. This will be your starting position. Begin by winding the arm, raising the medicine ball above your head. As you do so, extend through the hips, knees, and ankles to load up for the slam. At peak extension, flex the shoulders, spine, and hips to throw the ball hard into the ground directly in front of you. Catch the ball on the bounce and continue for the...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/One-Arm_Medicine_Ball_Slam/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/One-Arm_Medicine_Ball_Slam/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Air_Bike",
            name = "Air Bike",
            category = ExerciseCategory.BODYWEIGHT,
            primaryMuscle = MuscleGroup.CORE,
            description = "Lie flat on the floor with your lower back pressed to the ground.",
            instructions = "Lie flat on the floor with your lower back pressed to the ground. For this exercise, you will need to put your hands beside your head. Be careful however to not strain with the neck as you perform it. Now lift your shoulders into the crunch position. Bring knees up to where they are perpendicular to the floor, with your lower legs parallel to the floor. This will be your starting position. Now simultaneously, slowly go through a cycle pedal motion kicking forward with the right leg and...",
            defaultSets = 3,
            defaultReps = 12,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Air_Bike/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Air_Bike/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Alternate_Hammer_Curl",
            name = "Alternate Hammer Curl",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.BICEPS,
            secondaryMuscles = listOf(MuscleGroup.FOREARMS),
            description = "Stand up with your torso upright and a dumbbell in each hand being held at arms length.",
            instructions = "Stand up with your torso upright and a dumbbell in each hand being held at arms length. The elbows should be close to the torso. The palms of the hands should be facing your torso. This will be your starting position. While holding the upper arm stationary, curl the right weight forward while contracting the biceps as you breathe out. Continue the movement until your biceps is fully contracted and the dumbbells are at shoulder level. Hold the contracted position for a second as you squeeze...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Alternate_Hammer_Curl/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Alternate_Hammer_Curl/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Alternate_Heel_Touchers",
            name = "Alternate Heel Touchers",
            category = ExerciseCategory.BODYWEIGHT,
            primaryMuscle = MuscleGroup.CORE,
            description = "Lie on the floor with the knees bent and the feet on the floor around 18-24 inches apart.",
            instructions = "Lie on the floor with the knees bent and the feet on the floor around 18-24 inches apart. Your arms should be extended by your side. This will be your starting position. Crunch over your torso forward and up about 3-4 inches to the right side and touch your right heel as you hold the contraction for a second. Exhale while performing this movement. Now go back slowly to the starting position as you inhale. Now crunch over your torso forward and up around 3-4 inches to the left side and touch...",
            defaultSets = 3,
            defaultReps = 12,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Alternate_Heel_Touchers/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Alternate_Heel_Touchers/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Alternate_Incline_Dumbbell_Curl",
            name = "Alternate Incline Dumbbell Curl",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.BICEPS,
            secondaryMuscles = listOf(MuscleGroup.FOREARMS),
            description = "Sit down on an incline bench with a dumbbell in each hand being held at arms length.",
            instructions = "Sit down on an incline bench with a dumbbell in each hand being held at arms length. Tip: Keep the elbows close to the torso.This will be your starting position. While holding the upper arm stationary, curl the right weight forward while contracting the biceps as you breathe out. As you do so, rotate the hand so that the palm is facing up. Continue the movement until your biceps is fully contracted and the dumbbells are at shoulder level. Hold the contracted position for a second as you...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Alternate_Incline_Dumbbell_Curl/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Alternate_Incline_Dumbbell_Curl/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Alternating_Cable_Shoulder_Press",
            name = "Alternating Cable Shoulder Press",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.SHOULDERS,
            secondaryMuscles = listOf(MuscleGroup.TRICEPS),
            description = "Move the cables to the bottom of the tower and select an appropriate weight.",
            instructions = "Move the cables to the bottom of the tower and select an appropriate weight. Grasp the cables and hold them at shoulder height, palms facing forward. This will be your starting position. Keeping your head and chest up, extend through the elbow to press one side directly over head. After pausing at the top, return to the starting position and repeat on the opposite side.",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Alternating_Cable_Shoulder_Press/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Alternating_Cable_Shoulder_Press/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Alternating_Deltoid_Raise",
            name = "Alternating Deltoid Raise",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.SHOULDERS,
            description = "In a standing position, hold a pair of dumbbells at your side.",
            instructions = "In a standing position, hold a pair of dumbbells at your side. Keeping your elbows slightly bent, raise the weights directly in front of you to shoulder height, avoiding any swinging or cheating. Return the weights to your side. On the next repetition, raise the weights laterally, raising them out to your side to about shoulder height. Return the weights to the starting position and continue alternating to the front and side.",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Alternating_Deltoid_Raise/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Alternating_Deltoid_Raise/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Alternating_Floor_Press",
            name = "Alternating Floor Press",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.CHEST,
            secondaryMuscles = listOf(MuscleGroup.CORE, MuscleGroup.SHOULDERS, MuscleGroup.TRICEPS),
            description = "Lie on the floor with two kettlebells next to your shoulders.",
            instructions = "Lie on the floor with two kettlebells next to your shoulders. Position one in place on your chest and then the other, gripping the kettlebells on the handle with the palms facing forward. Extend both arms, so that the kettlebells are being held above your chest. Lower one kettlebell, bringing it to your chest and turn the wrist in the direction of the locked out kettlebell. Raise the kettlebell and repeat on the opposite side.",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Alternating_Floor_Press/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Alternating_Floor_Press/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Arm_Circles",
            name = "Arm Circles",
            category = ExerciseCategory.FLEXIBILITY,
            primaryMuscle = MuscleGroup.SHOULDERS,
            secondaryMuscles = listOf(MuscleGroup.BACK),
            description = "Stand up and extend your arms straight out by the sides.",
            instructions = "Stand up and extend your arms straight out by the sides. The arms should be parallel to the floor and perpendicular (90-degree angle) to your torso. This will be your starting position. Slowly start to make circles of about 1 foot in diameter with each outstretched arm. Breathe normally as you perform the movement. Continue the circular motion of the outstretched arms for about ten seconds. Then reverse the movement, going the opposite direction.",
            isTimeBased = true,
            defaultSets = 1,
            defaultDurationSeconds = 30,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Arm_Circles/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Arm_Circles/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Back_Flyes_-_With_Bands",
            name = "Back Flyes - With Bands",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.SHOULDERS,
            secondaryMuscles = listOf(MuscleGroup.BACK, MuscleGroup.TRICEPS),
            description = "Run a band around a stationary post like that of a squat rack.",
            instructions = "Run a band around a stationary post like that of a squat rack. Grab the band by the handles and stand back so that the tension in the band rises. Extend and lift the arms straight in front of you. Tip: Your arms should be straight and parallel to the floor while perpendicular to your torso. Your feet should be firmly planted on the floor spread at shoulder width. This will be your starting position. As you exhale, move your arms to the sides and back. Keep your arms extended and parallel to...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Back_Flyes_-_With_Bands/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Back_Flyes_-_With_Bands/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Ball_Leg_Curl",
            name = "Ball Leg Curl",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.HAMSTRINGS,
            secondaryMuscles = listOf(MuscleGroup.CALVES, MuscleGroup.GLUTES),
            description = "Begin on the floor laying on your back with your feet on top of the ball.",
            instructions = "Begin on the floor laying on your back with your feet on top of the ball. Position the ball so that when your legs are extended your ankles are on top of the ball. This will be your starting position. Raise your hips off of the ground, keeping your weight on the shoulder blades and your feet. Flex the knees, pulling the ball as close to you as you can, contracting the hamstrings. After a brief pause, return to the starting position.",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Ball_Leg_Curl/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Ball_Leg_Curl/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Band_Good_Morning",
            name = "Band Good Morning",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.HAMSTRINGS,
            secondaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.BACK),
            description = "Using a 41 inch band, stand on one end, spreading your feet a small amount.",
            instructions = "Using a 41 inch band, stand on one end, spreading your feet a small amount. Bend at the hips to loop the end of the band behind your neck. This will be your starting position. Keeping your legs straight, extend through the hips to come to a near vertical position. Ensure that you do not round your back as you go down back to the starting position.",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Band_Good_Morning/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Band_Good_Morning/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Band_Good_Morning_Pull_Through",
            name = "Band Good Morning (Pull Through)",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.HAMSTRINGS,
            secondaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.BACK),
            description = "Loop the band around a post.",
            instructions = "Loop the band around a post. Standing a little ways away, loop the opposite end around the neck. Your hands can help hold the band in position. Begin by bending at the hips, getting your butt back as far as possible. Keep your back flat and bend forward to about 90 degrees. Your knees should be only slightly bent. Return to the starting position be driving through with the hips to come back to a standing position.",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Band_Good_Morning_Pull_Through/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Band_Good_Morning_Pull_Through/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Band_Hip_Adductions",
            name = "Band Hip Adductions",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.GLUTES,
            description = "Anchor a band around a solid post or other object.",
            instructions = "Anchor a band around a solid post or other object. Stand with your left side to the post, and put your right foot through the band, getting it around the ankle. Stand up straight and hold onto the post if needed. This will be your starting position. Keeping the knee straight, raise your right legs out to the side as far as you can. Return to the starting position and repeat for the desired rep count. Switch sides.",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Band_Hip_Adductions/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Band_Hip_Adductions/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Band_Skull_Crusher",
            name = "Band Skull Crusher",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.TRICEPS,
            description = "Secure a band to the base of a rack or the bench.",
            instructions = "Secure a band to the base of a rack or the bench. Lay on the bench so that the band is lined up with your head. Take hold of the band, raising your elbows so that the upper arm is perpendicular to the floor. With the elbow flexed, the band should be above your head. This will be your starting position. Extend through the elbow to straighten your arm, keeping your upper arm in place. Pause at the top of the motion, and return to the starting position.",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Band_Skull_Crusher/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Band_Skull_Crusher/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Barbell_Curls_Lying_Against_An_Incline",
            name = "Barbell Curls Lying Against An Incline",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.BICEPS,
            description = "Lie against an incline bench, with your arms holding a barbell and hanging down in a horizontal line.",
            instructions = "Lie against an incline bench, with your arms holding a barbell and hanging down in a horizontal line. This will be your starting position. While keeping the upper arms stationary, curl the weight up as high as you can while squeezing the biceps. Breathe out as you perform this portion of the movement. Tip: Only the forearms should move. Do not swing the arms. After a second contraction, slowly go back to the starting position as you inhale. Tip: Make sure that you go all of the way down....",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Barbell_Curls_Lying_Against_An_Incline/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Barbell_Curls_Lying_Against_An_Incline/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Barbell_Incline_Shoulder_Raise",
            name = "Barbell Incline Shoulder Raise",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.SHOULDERS,
            secondaryMuscles = listOf(MuscleGroup.CHEST),
            description = "Lie back on an Incline Bench.",
            instructions = "Lie back on an Incline Bench. Using a medium width grip (a grip that is slightly wider than shoulder width), lift the bar from the rack and hold it straight over you with your arms straight. This will be your starting position. While keeping the arms straight, lift the bar by protracting your shoulder blades, raising the shoulders from the bench as you breathe out. Bring back the bar to the starting position as you breathe in. Repeat for the recommended amount of repetitions.",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Barbell_Incline_Shoulder_Raise/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Barbell_Incline_Shoulder_Raise/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Barbell_Rear_Delt_Row",
            name = "Barbell Rear Delt Row",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.SHOULDERS,
            secondaryMuscles = listOf(MuscleGroup.BICEPS, MuscleGroup.BACK),
            description = "Stand up straight while holding a barbell using a wide (higher than shoulder width) and overhand (palms facing your body) grip.",
            instructions = "Stand up straight while holding a barbell using a wide (higher than shoulder width) and overhand (palms facing your body) grip. Bend knees slightly and bend over as you keep the natural arch of your back. Let the arms hang in front of you as they hold the bar. Once your torso is parallel to the floor, flare the elbows out and away from your body. Tip: Your torso and your arms should resemble the letter \"T\". Now you are ready to begin the exercise. While keeping the upper arms perpendicular...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Barbell_Rear_Delt_Row/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Barbell_Rear_Delt_Row/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Barbell_Seated_Calf_Raise",
            name = "Barbell Seated Calf Raise",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.CALVES,
            description = "Place a block about 12 inches in front of a flat bench.",
            instructions = "Place a block about 12 inches in front of a flat bench. Sit on the bench and place the ball of your feet on the block. Have someone place a barbell over your upper thighs about 3 inches above your knees and hold it there. This will be your starting position. Raise up on your toes as high as possible as you squeeze the calves and as you breathe out. After a second contraction, slowly go back to the starting position. Tip: To get maximum benefit stretch your calves as far as you can. Repeat...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Barbell_Seated_Calf_Raise/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Barbell_Seated_Calf_Raise/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Barbell_Shrug_Behind_The_Back",
            name = "Barbell Shrug Behind The Back",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.BACK,
            secondaryMuscles = listOf(MuscleGroup.FOREARMS),
            description = "Stand up straight with your feet at shoulder width as you hold a barbell with both hands behind your back using a pronated grip (palms facing back).",
            instructions = "Stand up straight with your feet at shoulder width as you hold a barbell with both hands behind your back using a pronated grip (palms facing back). Tip: Your hands should be a little wider than shoulder width apart. You can use wrist wraps for this exercise for better grip. This will be your starting position. Raise your shoulders up as far as you can go as you breathe out and hold the contraction for a second. Tip: Refrain from trying to lift the barbell by using your biceps. The arms...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Barbell_Shrug_Behind_The_Back/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Barbell_Shrug_Behind_The_Back/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Barbell_Side_Bend",
            name = "Barbell Side Bend",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.CORE,
            secondaryMuscles = listOf(MuscleGroup.BACK),
            description = "Stand up straight while holding a barbell placed on the back of your shoulders (slightly below the neck).",
            instructions = "Stand up straight while holding a barbell placed on the back of your shoulders (slightly below the neck). Your feet should be shoulder width apart. This will be your starting position. While keeping your back straight and your head up, bend only at the waist to the right as far as possible. Breathe in as you bend to the side. Then hold for a second and come back up to the starting position as you exhale. Tip: Keep the rest of the body stationary. Now repeat the movement but bending to the...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Barbell_Side_Bend/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Barbell_Side_Bend/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Barbell_Side_Split_Squat",
            name = "Barbell Side Split Squat",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.QUADRICEPS,
            secondaryMuscles = listOf(MuscleGroup.CALVES, MuscleGroup.HAMSTRINGS, MuscleGroup.BACK),
            description = "Stand up straight while holding a barbell placed on the back of your shoulders (slightly below the neck).",
            instructions = "Stand up straight while holding a barbell placed on the back of your shoulders (slightly below the neck). Your feet should be placed wide apart with the foot of the lead leg angled out to the side. This will be your starting position. Lower your body towards the side of your angled foot by bending the knee and hip of your lead leg and while keeping the opposite leg only slightly bent. Breathe in as you lower your body. Return to the starting position by extending the hip and knee of the lead...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Barbell_Side_Split_Squat/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Barbell_Side_Split_Squat/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Bench_Press_-_With_Bands",
            name = "Bench Press - With Bands",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.CHEST,
            secondaryMuscles = listOf(MuscleGroup.SHOULDERS, MuscleGroup.TRICEPS),
            description = "Using a flat bench secure a band under the leg of the bench that is nearest to your head.",
            instructions = "Using a flat bench secure a band under the leg of the bench that is nearest to your head. Once the band is secure, grab it by both handles and lie down on the bench. Extend your arms so that you are holding the band handles in front of you at shoulder width. Once at shoulder width, rotate your wrists forward so that the palms of your hands are facing away from you. This will be your starting position. Bring down the handles slowly until your elbow forms a 90 degree angle. Keep full control...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Bench_Press_-_With_Bands/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Bench_Press_-_With_Bands/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Bent_Over_One-Arm_Long_Bar_Row",
            name = "Bent Over One-Arm Long Bar Row",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.BACK,
            secondaryMuscles = listOf(MuscleGroup.BICEPS),
            description = "Put weight on one of the ends of an Olympic barbell.",
            instructions = "Put weight on one of the ends of an Olympic barbell. Make sure that you either place the other end of the barbell in the corner of two walls; or put a heavy object on the ground so the barbell cannot slide backward. Bend forward until your torso is as close to parallel with the floor as you can and keep your knees slightly bent. Now grab the bar with one arm just behind the plates on the side where the weight was placed and put your other hand on your knee. This will be your starting...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Bent_Over_One-Arm_Long_Bar_Row/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Bent_Over_One-Arm_Long_Bar_Row/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Bent_Over_Two-Dumbbell_Row",
            name = "Bent Over Two-Dumbbell Row",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.BACK,
            secondaryMuscles = listOf(MuscleGroup.BICEPS, MuscleGroup.SHOULDERS),
            description = "With a dumbbell in each hand (palms facing your torso), bend your knees slightly and bring your torso forward by bending at the waist; as you bend make sure...",
            instructions = "With a dumbbell in each hand (palms facing your torso), bend your knees slightly and bring your torso forward by bending at the waist; as you bend make sure to keep your back straight until it is almost parallel to the floor. Tip: Make sure that you keep the head up. The weights should hang directly in front of you as your arms hang perpendicular to the floor and your torso. This is your starting position. While keeping the torso stationary, lift the dumbbells to your side (as you breathe...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Bent_Over_Two-Dumbbell_Row/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Bent_Over_Two-Dumbbell_Row/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Bent-Knee_Hip_Raise",
            name = "Bent-Knee Hip Raise",
            category = ExerciseCategory.BODYWEIGHT,
            primaryMuscle = MuscleGroup.CORE,
            description = "Lay flat on the floor with your arms next to your sides.",
            instructions = "Lay flat on the floor with your arms next to your sides. Now bend your knees at around a 75 degree angle and lift your feet off the floor by around 2 inches. Using your lower abs, bring your knees in towards you as you maintain the 75 degree angle bend in your legs. Continue this movement until you raise your hips off of the floor by rolling your pelvis backward. Breathe out as you perform this portion of the movement. Tip: At the end of the movement your knees will be over your chest....",
            defaultSets = 3,
            defaultReps = 12,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Bent-Knee_Hip_Raise/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Bent-Knee_Hip_Raise/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Bicycling",
            name = "Bicycling",
            category = ExerciseCategory.CARDIO,
            primaryMuscle = MuscleGroup.QUADRICEPS,
            secondaryMuscles = listOf(MuscleGroup.CALVES, MuscleGroup.GLUTES, MuscleGroup.HAMSTRINGS),
            description = "To begin, seat yourself on the bike and adjust the seat to your height.",
            instructions = "To begin, seat yourself on the bike and adjust the seat to your height.",
            isTimeBased = true,
            defaultSets = 1,
            defaultDurationSeconds = 600,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Bicycling/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Bicycling/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Bicycling_Stationary",
            name = "Bicycling, Stationary",
            category = ExerciseCategory.CARDIO,
            primaryMuscle = MuscleGroup.QUADRICEPS,
            secondaryMuscles = listOf(MuscleGroup.CALVES, MuscleGroup.GLUTES, MuscleGroup.HAMSTRINGS),
            description = "To begin, seat yourself on the bike and adjust the seat to your height.",
            instructions = "To begin, seat yourself on the bike and adjust the seat to your height. Select the desired option from the menu. You may have to start pedaling to turn it on. You can use the manual setting, or you can select a program to use. Typically, you can enter your age and weight to estimate the amount of calories burned during exercise. The level of resistance can be changed throughout the workout. The handles can be used to monitor your heart rate to help you stay at an appropriate intensity.",
            isTimeBased = true,
            defaultSets = 1,
            defaultDurationSeconds = 600,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Bicycling_Stationary/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Bicycling_Stationary/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Bodyweight_Squat",
            name = "Bodyweight Squat",
            category = ExerciseCategory.BODYWEIGHT,
            primaryMuscle = MuscleGroup.QUADRICEPS,
            secondaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.HAMSTRINGS),
            description = "Stand with your feet shoulder width apart.",
            instructions = "Stand with your feet shoulder width apart. You can place your hands behind your head. This will be your starting position. Begin the movement by flexing your knees and hips, sitting back with your hips. Continue down to full depth if you are able,and quickly reverse the motion until you return to the starting position. As you squat, keep your head and chest up and push your knees out.",
            defaultSets = 3,
            defaultReps = 12,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Bodyweight_Squat/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Bodyweight_Squat/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Bodyweight_Walking_Lunge",
            name = "Bodyweight Walking Lunge",
            category = ExerciseCategory.BODYWEIGHT,
            primaryMuscle = MuscleGroup.QUADRICEPS,
            secondaryMuscles = listOf(MuscleGroup.CALVES, MuscleGroup.GLUTES, MuscleGroup.HAMSTRINGS),
            description = "Begin standing with your feet shoulder width apart and your hands on your hips.",
            instructions = "Begin standing with your feet shoulder width apart and your hands on your hips. Step forward with one leg, flexing the knees to drop your hips. Descend until your rear knee nearly touches the ground. Your posture should remain upright, and your front knee should stay above the front foot. Drive through the heel of your lead foot and extend both knees to raise yourself back up. Step forward with your rear foot, repeating the lunge on the opposite leg.",
            defaultSets = 3,
            defaultReps = 12,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Bodyweight_Walking_Lunge/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Bodyweight_Walking_Lunge/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Butt_Lift_Bridge",
            name = "Butt Lift (Bridge)",
            category = ExerciseCategory.BODYWEIGHT,
            primaryMuscle = MuscleGroup.GLUTES,
            secondaryMuscles = listOf(MuscleGroup.HAMSTRINGS),
            description = "Lie flat on the floor on your back with the hands by your side and your knees bent.",
            instructions = "Lie flat on the floor on your back with the hands by your side and your knees bent. Your feet should be placed around shoulder width. This will be your starting position. Pushing mainly with your heels, lift your hips off the floor while keeping your back straight. Breathe out as you perform this part of the motion and hold at the top for a second. Slowly go back to the starting position as you breathe in.",
            defaultSets = 3,
            defaultReps = 12,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Butt_Lift_Bridge/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Butt_Lift_Bridge/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Butt-Ups",
            name = "Butt-Ups",
            category = ExerciseCategory.BODYWEIGHT,
            primaryMuscle = MuscleGroup.CORE,
            description = "Begin a pushup position but with your elbows on the ground and resting on your forearms.",
            instructions = "Begin a pushup position but with your elbows on the ground and resting on your forearms. Your arms should be bent at a 90 degree angle. Arch your back slightly out rather than keeping your back completely straight. Raise your glutes toward the ceiling, squeezing your abs tightly to close the distance between your ribcage and hips. The end result will be that you'll end up in a high bridge position. Exhale as you perform this portion of the movement. Lower back down slowly to your starting...",
            defaultSets = 3,
            defaultReps = 12,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Butt-Ups/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Butt-Ups/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Cable_Crunch",
            name = "Cable Crunch",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.CORE,
            description = "Kneel below a high pulley that contains a rope attachment.",
            instructions = "Kneel below a high pulley that contains a rope attachment. Grasp cable rope attachment and lower the rope until your hands are placed next to your face. Flex your hips slightly and allow the weight to hyperextend the lower back. This will be your starting position. With the hips stationary, flex the waist as you contract the abs so that the elbows travel towards the middle of the thighs. Exhale as you perform this portion of the movement and hold the contraction for a second. Slowly return...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Cable_Crunch/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Cable_Crunch/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Cable_Deadlifts",
            name = "Cable Deadlifts",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.QUADRICEPS,
            secondaryMuscles = listOf(MuscleGroup.FOREARMS, MuscleGroup.GLUTES, MuscleGroup.HAMSTRINGS, MuscleGroup.BACK),
            description = "Move the cables to the bottom of the towers and select an appropriate weight.",
            instructions = "Move the cables to the bottom of the towers and select an appropriate weight. Stand directly in between the uprights. To begin, squat down be flexing your hips and knees until you can reach the handles. After grasping them, begin your ascent. Driving through your heels extend your hips and knees keeping your hands hanging at your side. Keep your head and chest up throughout the movement. After reaching a full standing position, Return to the starting position and repeat.",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Cable_Deadlifts/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Cable_Deadlifts/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Cable_Hip_Adduction",
            name = "Cable Hip Adduction",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.QUADRICEPS,
            description = "Stand in front of a low pulley facing forward with one leg next to the pulley and the other one away.",
            instructions = "Stand in front of a low pulley facing forward with one leg next to the pulley and the other one away. Attach the ankle cuff to the cable and also to the ankle of the leg that is next to the pulley. Now step out and away from the stack with a wide stance and grasp the bar of the pulley system. Stand on the foot that does not have the ankle cuff (the far foot) and allow the leg with the cuff to be pulled towards the low pulley. This will be your starting position. Now perform the movement by...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Cable_Hip_Adduction/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Cable_Hip_Adduction/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Cable_Incline_Triceps_Extension",
            name = "Cable Incline Triceps Extension",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.TRICEPS,
            description = "Lie on incline an bench facing away from a high pulley machine that has a straight bar attachment on it.",
            instructions = "Lie on incline an bench facing away from a high pulley machine that has a straight bar attachment on it. Grasp the straight bar attachment overhead with a pronated (overhand; palms down) narrow grip (less than shoulder width) and keep your elbows tucked in to your sides. Your upper arms should create around a 25 degree angle when measured from the floor. Keeping the upper arms stationary, extend the arms as you flex the triceps. Breathe out during this portion of the movement and hold the...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Cable_Incline_Triceps_Extension/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Cable_Incline_Triceps_Extension/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Cable_Lying_Triceps_Extension",
            name = "Cable Lying Triceps Extension",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.TRICEPS,
            description = "Lie on a flat bench and grasp the straight bar attachment of a low pulley with a narrow overhand grip.",
            instructions = "Lie on a flat bench and grasp the straight bar attachment of a low pulley with a narrow overhand grip. Tip: The easiest way to do this is to have someone hand you the bar as you lay down. With your arms extended, position the bar over your torso. Your arms and your torso should create a 90-degree angle. This will be your starting position. Lower the bar by bending at the elbow while keeping the upper arms stationary and elbows in. Go down until the bar lightly touches your forehead. Breathe...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Cable_Lying_Triceps_Extension/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Cable_Lying_Triceps_Extension/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Cable_One_Arm_Tricep_Extension",
            name = "Cable One Arm Tricep Extension",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.TRICEPS,
            description = "With your right hand, grasp a single handle attached to the high-cable pulley using a supinated (underhand; palms facing up) grip.",
            instructions = "With your right hand, grasp a single handle attached to the high-cable pulley using a supinated (underhand; palms facing up) grip. You should be standing directly in front of the weight stack. Now pull the handle down so that your upper arm and elbow are locked in to the side of your body. Your upper arm and forearm should form an acute angle (less than 90-degrees). You can keep the other arm by the waist and you can have one leg in front of you and the other one back for better balance....",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Cable_One_Arm_Tricep_Extension/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Cable_One_Arm_Tricep_Extension/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Cable_Reverse_Crunch",
            name = "Cable Reverse Crunch",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.CORE,
            description = "Connect an ankle strap attachment to a low pulley cable and position a mat on the floor in front of it.",
            instructions = "Connect an ankle strap attachment to a low pulley cable and position a mat on the floor in front of it. Sit down with your feet toward the pulley and attach the cable to your ankles. Lie down, elevate your legs and bend your knees at a 90-degree angle. Your legs and the cable should be aligned. If not, adjust the pulley up or down until they are. With your hands behind your head, bring your knees inward to your torso and elevate your hips off the floor. Pause for a moment and in a slow and...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Cable_Reverse_Crunch/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Cable_Reverse_Crunch/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Cable_Russian_Twists",
            name = "Cable Russian Twists",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.CORE,
            description = "Connect a standard handle attachment, and position the cable to a middle pulley position.",
            instructions = "Connect a standard handle attachment, and position the cable to a middle pulley position. Lie on a stability ball perpendicular to the cable and grab the handle with one hand. You should be approximately arm's length away from the pulley, with the tension of the weight on the cable. Grab the handle with both hands and fully extend your arms above your chest. You hands should be directly in-line with the pulley. If not, adjust the pulley up or down until they are. Keep your hips elevated and...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Cable_Russian_Twists/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Cable_Russian_Twists/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Calf_Press",
            name = "Calf Press",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.CALVES,
            description = "Adjust the seat so that your legs are only slightly bent in the start position.",
            instructions = "Adjust the seat so that your legs are only slightly bent in the start position. The balls of your feet should be firmly on the platform. Select an appropriate weight, and grasp the handles. This will be your starting position. Straighten the legs by extending the knees, just barely lifting the weight from the stack. Your ankle should be fully flexed, toes pointing up. Execute the movement by pressing downward through the balls of your feet as far as possible. After a brief pause, reverse the...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Calf_Press/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Calf_Press/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Calf_Press_On_The_Leg_Press_Machine",
            name = "Calf Press On The Leg Press Machine",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.CALVES,
            description = "Using a leg press machine, sit down on the machine and place your legs on the platform directly in front of you at a medium (shoulder width) foot stance.",
            instructions = "Using a leg press machine, sit down on the machine and place your legs on the platform directly in front of you at a medium (shoulder width) foot stance. Lower the safety bars holding the weighted platform in place and press the platform all the way up until your legs are fully extended in front of you without locking your knees. (Note: In some leg press units you can leave the safety bars on for increased safety. If your leg press unit allows for this, then this is the preferred method of...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Calf_Press_On_The_Leg_Press_Machine/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Calf_Press_On_The_Leg_Press_Machine/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Cross_Over_-_With_Bands",
            name = "Cross Over - With Bands",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.CHEST,
            secondaryMuscles = listOf(MuscleGroup.BICEPS, MuscleGroup.SHOULDERS),
            description = "Secure an exercise band around a stationary post.",
            instructions = "Secure an exercise band around a stationary post. While facing away from the post, grab the handles on both ends of the band and step forward enough to create tension on the band. Raise your arms to the sides, parallel to the floor, perpendicular to your torso (your torso and the arms should resemble the letter \"T\") and with the palms facing forward. Have them extended with a slight bend at the elbows. This will be your starting position. While keeping your arms straight, bring them across...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Cross_Over_-_With_Bands/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Cross_Over_-_With_Bands/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Decline_Barbell_Bench_Press",
            name = "Decline Barbell Bench Press",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.CHEST,
            secondaryMuscles = listOf(MuscleGroup.SHOULDERS, MuscleGroup.TRICEPS),
            description = "Secure your legs at the end of the decline bench and slowly lay down on the bench.",
            instructions = "Secure your legs at the end of the decline bench and slowly lay down on the bench. Using a medium width grip (a grip that creates a 90-degree angle in the middle of the movement between the forearms and the upper arms), lift the bar from the rack and hold it straight over you with your arms locked. The arms should be perpendicular to the floor. This will be your starting position. Tip: In order to protect your rotator cuff, it is best if you have a spotter help you lift the barbell off the...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Decline_Barbell_Bench_Press/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Decline_Barbell_Bench_Press/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Dumbbell_Lunges",
            name = "Dumbbell Lunges",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.QUADRICEPS,
            secondaryMuscles = listOf(MuscleGroup.CALVES, MuscleGroup.GLUTES, MuscleGroup.HAMSTRINGS),
            description = "Stand with your torso upright holding two dumbbells in your hands by your sides.",
            instructions = "Stand with your torso upright holding two dumbbells in your hands by your sides. This will be your starting position. Step forward with your right leg around 2 feet or so from the foot being left stationary behind and lower your upper body down, while keeping the torso upright and maintaining balance. Inhale as you go down. Note: As in the other exercises, do not allow your knee to go forward beyond your toes as you come down, as this will put undue stress on the knee joint. Make sure that...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Dumbbell_Lunges/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Dumbbell_Lunges/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Dumbbell_Squat",
            name = "Dumbbell Squat",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.QUADRICEPS,
            secondaryMuscles = listOf(MuscleGroup.CALVES, MuscleGroup.GLUTES, MuscleGroup.HAMSTRINGS, MuscleGroup.BACK),
            description = "Stand up straight while holding a dumbbell on each hand (palms facing the side of your legs).",
            instructions = "Stand up straight while holding a dumbbell on each hand (palms facing the side of your legs). Position your legs using a shoulder width medium stance with the toes slightly pointed out. Keep your head up at all times as looking down will get you off balance and also maintain a straight back. This will be your starting position. Note: For the purposes of this discussion we will use the medium stance described above which targets overall development; however you can choose any of the three...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Dumbbell_Squat/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Dumbbell_Squat/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Front_Leg_Raises",
            name = "Front Leg Raises",
            category = ExerciseCategory.FLEXIBILITY,
            primaryMuscle = MuscleGroup.HAMSTRINGS,
            description = "Stand next to a chair or other support, holding on with one hand.",
            instructions = "Stand next to a chair or other support, holding on with one hand. Swing your leg forward, keeping the leg straight. Continue with a downward swing, bringing the leg as far back as your flexibility allows. Repeat 5-10 times, and then switch legs.",
            isTimeBased = true,
            defaultSets = 1,
            defaultDurationSeconds = 30,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Front_Leg_Raises/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Front_Leg_Raises/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Glute_Kickback",
            name = "Glute Kickback",
            category = ExerciseCategory.BODYWEIGHT,
            primaryMuscle = MuscleGroup.GLUTES,
            secondaryMuscles = listOf(MuscleGroup.HAMSTRINGS),
            description = "Kneel on the floor or an exercise mat and bend at the waist with your arms extended in front of you (perpendicular to the torso) in order to get into a...",
            instructions = "Kneel on the floor or an exercise mat and bend at the waist with your arms extended in front of you (perpendicular to the torso) in order to get into a kneeling push-up position but with the arms spaced at shoulder width. Your head should be looking forward and the bend of the knees should create a 90-degree angle between the hamstrings and the calves. This will be your starting position. As you exhale, lift up your right leg until the hamstrings are in line with the back while maintaining...",
            defaultSets = 3,
            defaultReps = 12,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Glute_Kickback/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Glute_Kickback/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Hamstring_Stretch",
            name = "Hamstring Stretch",
            category = ExerciseCategory.FLEXIBILITY,
            primaryMuscle = MuscleGroup.HAMSTRINGS,
            description = "Lie on your back with one leg extended above you, with the hip at ninety degrees.",
            instructions = "Lie on your back with one leg extended above you, with the hip at ninety degrees. Keep the other leg flat on the floor. Loop a belt, band, or rope over the ball of your foot. This will be your starting position. Pull on the belt to create tension in the calves and hamstrings. Hold this stretch for 10-30 seconds, and repeat with the other leg.",
            isTimeBased = true,
            defaultSets = 1,
            defaultDurationSeconds = 30,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Hamstring_Stretch/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Hamstring_Stretch/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Hip_Extension_with_Bands",
            name = "Hip Extension with Bands",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.GLUTES,
            secondaryMuscles = listOf(MuscleGroup.HAMSTRINGS),
            description = "Secure one end of the band to the lower portion of a post and attach the other to one ankle.",
            instructions = "Secure one end of the band to the lower portion of a post and attach the other to one ankle. Facing the attachment point of the band, hold on to the column to stabilize yourself. Keeping your head and your chest up, move the resisted leg back as far as you can while keeping the knee straight. Return the leg to the starting position.",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Hip_Extension_with_Bands/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Hip_Extension_with_Bands/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Hip_Lift_with_Band",
            name = "Hip Lift with Band",
            category = ExerciseCategory.STRENGTH,
            primaryMuscle = MuscleGroup.GLUTES,
            secondaryMuscles = listOf(MuscleGroup.CALVES, MuscleGroup.HAMSTRINGS),
            description = "After choosing a suitable band, lay down in the middle of the rack, after securing the band on either side of you.",
            instructions = "After choosing a suitable band, lay down in the middle of the rack, after securing the band on either side of you. If your rack doesn't have pegs, the band can be secured using heavy dumbbells or similar objects, just ensure they won't move. Adjust your position so that the band is directly over your hips. Bend your knees and place your feet flat on the floor. Your hands can be on the floor or holding the band in position. Keeping your shoulders on the ground, drive through your heels to...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Hip_Lift_with_Band/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Hip_Lift_with_Band/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Inchworm",
            name = "Inchworm",
            category = ExerciseCategory.FLEXIBILITY,
            primaryMuscle = MuscleGroup.HAMSTRINGS,
            description = "Stand with your feet close together.",
            instructions = "Stand with your feet close together. Keeping your legs straight, stretch down and put your hands on the floor directly in front of you. This will be your starting position. Begin by walking your hands forward slowly, alternating your left and your right. As you do so, bend only at the hip, keeping your legs straight. Keep going until your body is parallel to the ground in a pushup position. Now, keep your hands in place and slowly take short steps with your feet, moving only a few inches at...",
            isTimeBased = true,
            defaultSets = 1,
            defaultDurationSeconds = 30,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Inchworm/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Inchworm/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Lateral_Box_Jump",
            name = "Lateral Box Jump",
            category = ExerciseCategory.PLYOMETRIC,
            primaryMuscle = MuscleGroup.GLUTES,
            secondaryMuscles = listOf(MuscleGroup.CALVES, MuscleGroup.HAMSTRINGS, MuscleGroup.QUADRICEPS),
            description = "Assume a comfortable standing position, with a short box positioned next to you.",
            instructions = "Assume a comfortable standing position, with a short box positioned next to you. This will be your starting position. Quickly dip into a quarter squat to initiate the stretch reflex, and immediately reverse direction to jump up and to the side. Bring your knees high enough to ensure your feet have good clearance over the box. Land on the center of the box, using your legs to absorb the impact. Carefully jump down to the other side of the box, and continue going back and forth for several...",
            defaultSets = 3,
            defaultReps = 10,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Lateral_Box_Jump/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Lateral_Box_Jump/1.jpg"
            )
        ),
        ExerciseDefinition(
            id = "db_Leg-Up_Hamstring_Stretch",
            name = "Leg-Up Hamstring Stretch",
            category = ExerciseCategory.FLEXIBILITY,
            primaryMuscle = MuscleGroup.HAMSTRINGS,
            description = "Lie flat on your back, bend one knee, and put that foot flat on the floor to stabilize your spine.",
            instructions = "Lie flat on your back, bend one knee, and put that foot flat on the floor to stabilize your spine. Extend the other leg in the air. If you're tight, you wont be able to straighten it. That's okay. Extend the knee so that the sole of the lifted foot faces the ceiling (or as close as you can get it). Slowly straighten the legs as much as possible and then pull the leg toward your nose. Switch sides.",
            isTimeBased = true,
            defaultSets = 1,
            defaultDurationSeconds = 30,
            imageUrls = listOf(
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Leg-Up_Hamstring_Stretch/0.jpg",
                "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/Leg-Up_Hamstring_Stretch/1.jpg"
            )
        ),
        )
        return base.map { it.copy(imageUrls = defaultImageOverrides[it.id] ?: it.imageUrls) }
    }
}
