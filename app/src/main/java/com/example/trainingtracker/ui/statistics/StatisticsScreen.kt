package com.example.trainingtracker.ui.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable fun StatisticsScreen(onGeneralStatisticsClicked: () -> Unit, onMovementStatisticsClicked: () -> Unit)
{
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
        ) {

        // todo: add back simple chart
        val buttonWidth = 0.8f

        Button(onClick = {
            onGeneralStatisticsClicked()
        },
            modifier = Modifier.fillMaxWidth(buttonWidth),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("General Statistics")
        }

        Spacer(modifier = Modifier.fillMaxWidth(0.5f).height(8.dp))

        Button(onClick = {
            onMovementStatisticsClicked()
        },
            modifier = Modifier.fillMaxWidth(buttonWidth),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Per Movement Statistics")
        }
    }
}