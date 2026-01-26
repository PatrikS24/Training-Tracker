package com.example.trainingtracker.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.example.trainingtracker.ui.activeWorkout.WorkoutScreen
import com.example.trainingtracker.ui.generalUi.SearchMovementsScreen
import com.example.trainingtracker.ui.statistics.GeneralStatisticsScreen
import com.example.trainingtracker.ui.statistics.MovementStatisticsScreen
import com.example.trainingtracker.ui.statistics.StatisticsScreen

sealed class BottomNavItem(
    val route: String,
    val label: String
) {
    object Workout : BottomNavItem("workout", "Workout")
    object Exercises : BottomNavItem("exercises", "Exercises")
    object Statistics : BottomNavItem("statistics_graph", "Statistics")

    companion object {
        val items = listOf(Workout, Exercises, Statistics)
    }
}

@Composable
fun AppNavHost(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Workout.route
    ) {
        composable("workout") { WorkoutScreen() }
        composable("exercises") { ExercisesScreen() }

        navigation(
            startDestination = "statistics",
            route = "statistics_graph"
        ) {
            composable("statistics") { StatisticsScreen(
                onGeneralStatisticsClicked = {navController.navigate("general_statistics")},
                onMovementStatisticsClicked = {navController.navigate("search")}
            ) }
            composable("search") { SearchMovementsScreen(
                onDismiss = {
                    navController.popBackStack()
                },
                onMovementChosen = {
                    movement ->
                    navController.navigate("movement_statistics/${movement}")
                }
            ) }

            composable("general_statistics") { GeneralStatisticsScreen() }

            composable(
                route = "movement_statistics/{movementId}",
                arguments = listOf(
                navArgument("movementId") { type = NavType.IntType }
            )) {
                backStackEntry ->
                val movementId = backStackEntry.arguments!!.getInt("movementId")
                MovementStatisticsScreen(
                    movementId = movementId
                )
            }
        }
    }
}

@Composable
fun BottomBar(navController: NavController) {

    val currentRoute = navController
        .currentBackStackEntryAsState()
        .value?.destination?.route

    NavigationBar {
        BottomNavItem.items.forEach { item ->

            val selected = currentRoute == item.route

            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },

                // Text-only bottom bar
                icon = {
                    Text(
                        text = item.label,
                        style = if (selected)
                            MaterialTheme.typography.labelLarge
                        else
                            MaterialTheme.typography.labelMedium
                    )
                },

                // Disable label to avoid duplication
                label = null
            )
        }
    }
}