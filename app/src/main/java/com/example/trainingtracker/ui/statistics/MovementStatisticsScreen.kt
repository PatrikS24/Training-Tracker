package com.example.trainingtracker.ui.statistics

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun MovementStatisticsScreen(
    //viewModel: MovementStatisticsViewModel = viewModel(),
    movementId: Int
) {
    Text(movementId.toString())
}