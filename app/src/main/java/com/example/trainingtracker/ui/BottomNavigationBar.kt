package com.example.trainingtracker.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.example.trainingtracker.ui.activeWorkout.WorkoutScreen
import com.example.trainingtracker.ui.generalUi.SearchMovementsScreen
import com.example.trainingtracker.ui.history.HistoryScreen
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
    object History : BottomNavItem("history_graph", "History")

    companion object {
        val items = listOf(Workout, Exercises, Statistics, History)
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

        navigation(
            startDestination = "history",
            route = "history_graph"
        ) {
            composable(route = "history") {
                HistoryScreen(onEdit = {
                    navController.navigate("edit_workout")
                })
            }

            composable(
                route = "edit_workout/{workoutId}",
                arguments = listOf(
                    navArgument("workoutId") { type = NavType.IntType }
                )) {
                    backStackEntry ->
                    val workoutId = backStackEntry.arguments!!.getInt("workoutId")
                    // edit workout screen
                }
        }
    }
}

@Composable
fun BottomBar(navController: NavController) {

    val navBackStackEntry =
        navController.currentBackStackEntryAsState().value

    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        BottomNavItem.items.forEach { item ->

            val selected = currentDestination
                ?.hierarchy
                ?.any { it.route == item.route } == true

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
                icon = {
                    Text(
                        text = item.label,
                        style = if (selected)
                            MaterialTheme.typography.labelLarge
                        else
                            MaterialTheme.typography.labelMedium
                    )
                },
                label = null
            )
        }
    }
}
