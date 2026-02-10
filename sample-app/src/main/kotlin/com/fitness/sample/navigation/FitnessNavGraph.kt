package com.fitness.sample.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.fitness.sample.ui.exercise.CreateCustomExerciseScreen
import com.fitness.sample.ui.exercise.ExerciseListScreen
import com.fitness.sample.ui.exercise.ExercisePickerScreen
import com.fitness.sample.ui.home.HomeScreen
import com.fitness.sample.ui.settings.SettingsScreen
import com.fitness.sample.ui.template.ActiveWorkoutScreen
import com.fitness.sample.ui.template.ActiveWorkoutViewModel
import com.fitness.sample.ui.template.AddTemplateScreen
import com.fitness.sample.ui.template.TemplateListScreen
import com.fitness.sample.ui.template.TemplateViewModel
import com.fitness.sample.ui.workout.AddWorkoutScreen
import com.fitness.sample.ui.workout.WorkoutDetailsScreen
import com.fitness.sample.ui.workout.WorkoutViewModel
import com.fitness.sdk.domain.model.ExerciseDefinition

sealed class Screen(val route: String) {
    // Workout routes
    object Home : Screen("home")
    object AddWorkout : Screen("add_workout")
    object WorkoutDetails : Screen("workout/{workoutId}") {
        fun createRoute(workoutId: Long) = "workout/$workoutId"
    }
    object EditWorkout : Screen("edit_workout/{workoutId}") {
        fun createRoute(workoutId: Long) = "edit_workout/$workoutId"
    }
    object ExercisePicker : Screen("exercise_picker/{source}") {
        fun createRoute(source: String) = "exercise_picker/$source"
    }

    // Exercise list route
    object Exercises : Screen("exercises")
    object CreateCustomExercise : Screen("create_custom_exercise")

    // Template routes
    object Templates : Screen("templates")
    object AddTemplate : Screen("add_template")
    object EditTemplate : Screen("edit_template/{templateId}") {
        fun createRoute(templateId: Long) = "edit_template/$templateId"
    }
    object ActiveWorkout : Screen("active_workout/{templateId}") {
        fun createRoute(templateId: Long) = "active_workout/$templateId"
    }

    // Settings
    object Settings : Screen("settings")
}

@Composable
fun FitnessNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Home.route
) {
    // Shared state to pass selected exercise back
    var selectedExercise by remember { mutableStateOf<ExerciseDefinition?>(null) }
    var pendingExerciseSource by remember { mutableStateOf<String?>(null) }

    // Create shared ViewModels at NavGraph level to survive navigation to picker
    // Use rememberSaveable key to reset when starting fresh
    var addWorkoutKey by rememberSaveable { mutableStateOf(0) }
    var editWorkoutKey by rememberSaveable { mutableStateOf(0) }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // ========== Workout Routes ==========

        // Home screen (Workout history)
        composable(Screen.Home.route) {
            HomeScreen(
                onAddWorkout = {
                    // Reset key to get fresh ViewModel for new workout
                    addWorkoutKey++
                    navController.navigate(Screen.AddWorkout.route)
                },
                onWorkoutClick = { workoutId ->
                    navController.navigate(Screen.WorkoutDetails.createRoute(workoutId))
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        // Add workout screen
        composable(Screen.AddWorkout.route) { backStackEntry ->
            // Get ViewModel scoped to this back stack entry
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.AddWorkout.route)
            }
            val workoutViewModel: WorkoutViewModel = viewModel(parentEntry)

            // Check if we have a pending exercise from the picker
            val exerciseToAdd = if (pendingExerciseSource == "add") selectedExercise else null

            AddWorkoutScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onWorkoutSaved = {
                    navController.popBackStack()
                },
                onSelectFromLibrary = {
                    pendingExerciseSource = "add"
                    navController.navigate(Screen.ExercisePicker.createRoute("add"))
                },
                pendingExercise = exerciseToAdd,
                onExerciseConsumed = {
                    selectedExercise = null
                    pendingExerciseSource = null
                },
                viewModel = workoutViewModel
            )
        }

        // Workout details screen
        composable(
            route = Screen.WorkoutDetails.route,
            arguments = listOf(
                navArgument("workoutId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getLong("workoutId") ?: 0L
            WorkoutDetailsScreen(
                workoutId = workoutId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onEditWorkout = { id ->
                    // Reset key to get fresh ViewModel for edit
                    editWorkoutKey++
                    navController.navigate(Screen.EditWorkout.createRoute(id))
                }
            )
        }

        // Edit workout screen
        composable(
            route = Screen.EditWorkout.route,
            arguments = listOf(
                navArgument("workoutId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getLong("workoutId") ?: 0L

            // Get ViewModel scoped to this back stack entry
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.EditWorkout.route)
            }
            val workoutViewModel: WorkoutViewModel = viewModel(parentEntry)

            // Check if we have a pending exercise from the picker
            val exerciseToAdd = if (pendingExerciseSource == "edit") selectedExercise else null

            AddWorkoutScreen(
                workoutId = workoutId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onWorkoutSaved = {
                    navController.popBackStack(Screen.Home.route, inclusive = false)
                },
                onSelectFromLibrary = {
                    pendingExerciseSource = "edit"
                    navController.navigate(Screen.ExercisePicker.createRoute("edit"))
                },
                pendingExercise = exerciseToAdd,
                onExerciseConsumed = {
                    selectedExercise = null
                    pendingExerciseSource = null
                },
                viewModel = workoutViewModel
            )
        }

        // Exercise picker screen
        composable(
            route = Screen.ExercisePicker.route,
            arguments = listOf(
                navArgument("source") { type = NavType.StringType }
            )
        ) {
            ExercisePickerScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onExerciseSelected = { exercise ->
                    selectedExercise = exercise
                    navController.popBackStack()
                },
                onCreateCustomExercise = {
                    navController.navigate(Screen.CreateCustomExercise.route)
                }
            )
        }

        // ========== Exercise List Route ==========

        composable(Screen.Exercises.route) {
            ExerciseListScreen(
                onWorkoutClick = { workoutId ->
                    navController.navigate(Screen.WorkoutDetails.createRoute(workoutId))
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onCreateCustomExercise = {
                    navController.navigate(Screen.CreateCustomExercise.route)
                }
            )
        }

        // ========== Template Routes ==========

        // Template list screen
        composable(Screen.Templates.route) {
            TemplateListScreen(
                onAddTemplate = {
                    navController.navigate(Screen.AddTemplate.route)
                },
                onTemplateClick = { templateId ->
                    navController.navigate(Screen.EditTemplate.createRoute(templateId))
                },
                onStartWorkout = { templateId ->
                    navController.navigate(Screen.ActiveWorkout.createRoute(templateId))
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        // Add template screen
        composable(Screen.AddTemplate.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.AddTemplate.route)
            }
            val templateViewModel: TemplateViewModel = viewModel(parentEntry)

            val exerciseToAdd = if (pendingExerciseSource == "template_add") selectedExercise else null

            AddTemplateScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onTemplateSaved = {
                    navController.popBackStack()
                },
                onSelectFromLibrary = {
                    pendingExerciseSource = "template_add"
                    navController.navigate(Screen.ExercisePicker.createRoute("template_add"))
                },
                pendingExercise = exerciseToAdd,
                onExerciseConsumed = {
                    selectedExercise = null
                    pendingExerciseSource = null
                },
                viewModel = templateViewModel
            )
        }

        // Edit template screen
        composable(
            route = Screen.EditTemplate.route,
            arguments = listOf(
                navArgument("templateId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val templateId = backStackEntry.arguments?.getLong("templateId") ?: 0L

            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.EditTemplate.route)
            }
            val templateViewModel: TemplateViewModel = viewModel(parentEntry)

            val exerciseToAdd = if (pendingExerciseSource == "template_edit") selectedExercise else null

            AddTemplateScreen(
                templateId = templateId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onTemplateSaved = {
                    navController.popBackStack()
                },
                onSelectFromLibrary = {
                    pendingExerciseSource = "template_edit"
                    navController.navigate(Screen.ExercisePicker.createRoute("template_edit"))
                },
                pendingExercise = exerciseToAdd,
                onExerciseConsumed = {
                    selectedExercise = null
                    pendingExerciseSource = null
                },
                viewModel = templateViewModel
            )
        }

        // Active workout screen
        composable(
            route = Screen.ActiveWorkout.route,
            arguments = listOf(
                navArgument("templateId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val templateId = backStackEntry.arguments?.getLong("templateId") ?: 0L

            // ViewModel scoped to this destination's back stack entry (persists while on back stack)
            val activeWorkoutViewModel: ActiveWorkoutViewModel = viewModel(backStackEntry)

            // Check if we have a pending exercise from the picker
            val exerciseToAdd = if (pendingExerciseSource == "active_workout") selectedExercise else null

            ActiveWorkoutScreen(
                templateId = templateId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onWorkoutComplete = {
                    // Navigate to home to see the completed workout
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Templates.route) { inclusive = false }
                    }
                },
                onAddExercise = {
                    pendingExerciseSource = "active_workout"
                    navController.navigate(Screen.ExercisePicker.createRoute("active_workout"))
                },
                pendingExercise = exerciseToAdd,
                onExerciseConsumed = {
                    selectedExercise = null
                    pendingExerciseSource = null
                },
                viewModel = activeWorkoutViewModel
            )
        }

        // ========== Create Custom Exercise Route ==========
        composable(Screen.CreateCustomExercise.route) {
            CreateCustomExerciseScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onExerciseSaved = {
                    navController.popBackStack()
                }
            )
        }

        // ========== Settings Route ==========
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
