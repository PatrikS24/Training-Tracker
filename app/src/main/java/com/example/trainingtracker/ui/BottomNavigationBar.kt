package com.example.trainingtracker.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.trainingtracker.HomeScreen
import com.example.trainingtracker.StatisticsScreen

sealed class BottomNavItem(
    val route: String,
    val label: String
) {
    object Home : BottomNavItem("home", "Home")
    object Exercises : BottomNavItem("exercises", "Exercises")
    object Statistics : BottomNavItem("statistics", "Statistics")

    companion object {
        val items = listOf(Home, Exercises, Statistics)
    }
}

@Composable
fun AppNavHost(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.route
    ) {
        composable("home") { HomeScreen() }
        composable("exercises") { ExercisesScreen() }
        composable("statistics") { StatisticsScreen() }
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