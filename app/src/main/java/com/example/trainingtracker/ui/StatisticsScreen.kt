package com.example.trainingtracker.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.entryModelOf

@Composable fun StatisticsScreen()
{
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Coming soon...", modifier = Modifier.offset(x = 0.dp, y = 20.dp))
        SimpleLineChart()
    }

}

@Composable
fun SimpleLineChart() {
    val chartEntryModel = entryModelOf(1f, 2f, 3f, 2.5f, 4f)

    Chart(
        chart = lineChart(),
        model = chartEntryModel
    )
}