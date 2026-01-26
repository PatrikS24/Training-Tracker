package com.example.trainingtracker.ui.statistics

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trainingtracker.controller.statistics.GeneralStatisticsViewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.common.Fill
import java.time.temporal.WeekFields
import java.util.Locale
import kotlin.math.roundToInt


@Composable
fun GeneralStatisticsScreen(viewModel: GeneralStatisticsViewModel = viewModel()) {
    WorkoutFrequencyChart(viewModel)
}

@Composable
fun WorkoutFrequencyChart(viewModel: GeneralStatisticsViewModel) {
    val weekLabels = viewModel.weekLabels

    // Formatter is UI logic, so we 'remember' it here
    val labelFormatter = remember(weekLabels) {
        CartesianValueFormatter { _, value, _ ->
            val date = weekLabels.getOrNull(value.roundToInt())
            if (date != null) {
                val weekOfYear = date.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear())
                "W$weekOfYear"
            } else {
                ""
            }
        }
    }

    val scrollState = rememberVicoScrollState(
        scrollEnabled = true
    )
    val zoomState = rememberVicoZoomState(
        zoomEnabled = true
    )

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberColumnCartesianLayer(),
            bottomAxis = HorizontalAxis.rememberBottom(
                valueFormatter = labelFormatter,
                label = rememberTextComponent(color = MaterialTheme.colorScheme.onSurface, textSize = 12.sp),
                guideline = null
                ),
            startAxis = VerticalAxis.rememberStart(),
        ),
        modelProducer = viewModel.modelProducer,
        scrollState = scrollState,
        zoomState = zoomState
    )
}
