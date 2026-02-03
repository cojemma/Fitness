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

    private fun createExerciseLibrary(): List<ExerciseDefinition> = listOf(
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
        )
    )
}
