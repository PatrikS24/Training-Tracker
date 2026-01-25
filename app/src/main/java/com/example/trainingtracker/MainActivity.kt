package com.example.trainingtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.trainingtracker.ui.*
import com.example.trainingtracker.ui.theme.TrainingTrackerTheme
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.entryModelOf

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TrainingTrackerTheme {
                MainScreen()
            }
        }
    }
}



@Composable
fun MainScreen() {

    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomBar(navController)
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            AppNavHost(navController)
        }
    }
}
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