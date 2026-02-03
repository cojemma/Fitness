package com.fitness.sample.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.fitness.sample.ui.home.HomeScreen
import com.fitness.sample.ui.workout.AddWorkoutScreen
import com.fitness.sample.ui.workout.WorkoutDetailsScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AddWorkout : Screen("add_workout")
    object WorkoutDetails : Screen("workout/{workoutId}") {
        fun createRoute(workoutId: Long) = "workout/$workoutId"
    }
    object EditWorkout : Screen("edit_workout/{workoutId}") {
        fun createRoute(workoutId: Long) = "edit_workout/$workoutId"
    }
}

@Composable
fun FitnessNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        // Home screen
        composable(Screen.Home.route) {
            HomeScreen(
                onAddWorkout = {
                    navController.navigate(Screen.AddWorkout.route)
                },
                onWorkoutClick = { workoutId ->
                    navController.navigate(Screen.WorkoutDetails.createRoute(workoutId))
                }
            )
        }

        // Add workout screen
        composable(Screen.AddWorkout.route) {
            AddWorkoutScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onWorkoutSaved = {
                    navController.popBackStack()
                }
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
            AddWorkoutScreen(
                workoutId = workoutId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onWorkoutSaved = {
                    navController.popBackStack(Screen.Home.route, inclusive = false)
                }
            )
        }
    }
}
