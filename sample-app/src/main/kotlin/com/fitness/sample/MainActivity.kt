package com.fitness.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fitness.sample.navigation.FitnessNavGraph
import com.fitness.sample.navigation.Screen
import com.fitness.sample.ui.theme.FitnessTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FitnessTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Define bottom navigation items
    val bottomNavItems = listOf(
        BottomNavItem(
            route = Screen.Home.route,
            title = stringResource(R.string.nav_workouts),
            selectedIcon = Icons.Filled.DateRange,
            unselectedIcon = Icons.Outlined.DateRange
        ),
        BottomNavItem(
            route = Screen.Exercises.route,
            title = stringResource(R.string.nav_exercises),
            selectedIcon = Icons.Filled.FitnessCenter,
            unselectedIcon = Icons.Outlined.FitnessCenter
        ),
        BottomNavItem(
            route = Screen.Templates.route,
            title = stringResource(R.string.nav_templates),
            selectedIcon = Icons.Filled.PlayArrow,
            unselectedIcon = Icons.Outlined.PlayArrow
        )
    )

    // Routes where bottom nav should be hidden
    val hideBottomNavRoutes = listOf(
        Screen.AddWorkout.route,
        Screen.WorkoutDetails.route,
        Screen.EditWorkout.route,
        Screen.ExercisePicker.route,
        Screen.AddTemplate.route,
        Screen.EditTemplate.route,
        Screen.ActiveWorkout.route,
        Screen.Settings.route
    )

    val shouldShowBottomNav = currentDestination?.route?.let { route ->
        hideBottomNavRoutes.none { hiddenRoute ->
            route.startsWith(hiddenRoute.substringBefore("{"))
        }
    } ?: true

    Scaffold(
        bottomBar = {
            if (shouldShowBottomNav) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val isSelected = currentDestination?.hierarchy?.any {
                            it.route == item.route
                        } == true

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                navController.navigate(item.route) {
                                    // Pop up to the start destination to avoid building up a large back stack
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    // Avoid multiple copies of the same destination
                                    launchSingleTop = true
                                    // Restore state when reselecting a previously selected item
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.title
                                )
                            },
                            label = { Text(item.title) }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            FitnessNavGraph(navController = navController)
        }
    }
}

data class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)
