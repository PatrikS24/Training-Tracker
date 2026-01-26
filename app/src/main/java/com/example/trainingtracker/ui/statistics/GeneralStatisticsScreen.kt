package com.example.trainingtracker.ui.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
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
import com.patrykandpatrick.vico.compose.common.shader.toShaderProvider
import com.patrykandpatrick.vico.compose.common.shape.toVicoShape
import com.patrykandpatrick.vico.core.cartesian.Scroll
import com.patrykandpatrick.vico.core.cartesian.Zoom
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.Fill
import com.patrykandpatrick.vico.core.common.component.TextComponent
import java.time.temporal.ChronoField
import java.time.temporal.TemporalField
import java.time.temporal.WeekFields
import java.util.Locale
import kotlin.math.roundToInt


@Composable
fun GeneralStatisticsScreen(viewModel: GeneralStatisticsViewModel = viewModel()) {

    Column(
        modifier = Modifier.fillMaxSize().padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Workouts per week")
        WorkoutFrequencyChart(viewModel)

        Spacer(modifier = Modifier.fillMaxHeight(0.1f))

        Text("Workout duration")
        WorkoutDurationChart(viewModel)
    }
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
        scrollEnabled = true,
        initialScroll = Scroll.Absolute.End
    )
    val zoomState = rememberVicoZoomState(
        zoomEnabled = true,
        initialZoom = remember { Zoom.x(5.0) }
    )

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberColumnCartesianLayer(
                columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                    rememberLineComponent(
                        fill = Fill(
                            shaderProvider = Brush.verticalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                                )
                            ).toShaderProvider()
                        ),
                        thickness = 16.dp,
                        shape = MaterialTheme.shapes.small.toVicoShape()
                    )
                )
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                valueFormatter = labelFormatter,
                label = rememberTextComponent(color = MaterialTheme.colorScheme.onSurface, textSize = 12.sp),
                guideline = null
                ),
            startAxis = VerticalAxis.rememberStart(
                itemPlacer = VerticalAxis.ItemPlacer.step({ 1.0 }),
                valueFormatter = CartesianValueFormatter.decimal(java.text.DecimalFormat("#")),
                title = "Frequency",
                titleComponent = rememberTextComponent(
                    color = MaterialTheme.colorScheme.onSurface,
                    textSize = 12.sp,
                    lineCount = 1
                )
            ),
        ),
        modelProducer = viewModel.frequencyModelProducer,
        scrollState = scrollState,
        zoomState = zoomState
    )
}

@Composable
fun WorkoutDurationChart(viewModel: GeneralStatisticsViewModel) {
    val dayLabels = viewModel.dayLabels

    // Formatter is UI logic, so we 'remember' it here
    val labelFormatter = remember(dayLabels) {
        CartesianValueFormatter { _, value, _ ->
            val date = dayLabels.getOrNull(value.roundToInt())
            if (date != null) {
                "${date.get(ChronoField.DAY_OF_MONTH)}.${date.get(ChronoField.MONTH_OF_YEAR)}"
            } else {
                ""
            }
        }
    }

    val scrollState = rememberVicoScrollState(
        scrollEnabled = true,
        initialScroll = Scroll.Absolute.End
    )
    val zoomState = rememberVicoZoomState(
        zoomEnabled = true,
        initialZoom = remember { Zoom.x(5.0) }
    )

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberColumnCartesianLayer(
                columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                    rememberLineComponent(
                        fill = Fill(
                            shaderProvider = Brush.verticalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                                )
                            ).toShaderProvider()
                        ),
                        thickness = 16.dp,
                        shape = MaterialTheme.shapes.small.toVicoShape()
                    )
                )
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                valueFormatter = labelFormatter,
                label = rememberTextComponent(color = MaterialTheme.colorScheme.onSurface, textSize = 12.sp),
                guideline = null,
                labelRotationDegrees = 0f
            ),
            startAxis = VerticalAxis.rememberStart(
                itemPlacer = VerticalAxis.ItemPlacer.step({ 1.0 }),
                valueFormatter = CartesianValueFormatter.decimal(java.text.DecimalFormat("#")),
                title = "Duration (min)",
                titleComponent = rememberTextComponent(
                    color = MaterialTheme.colorScheme.onSurface,
                    textSize = 12.sp,
                    lineCount = 1
                )
            ),
        ),
        modelProducer = viewModel.durationModelProducer,
        scrollState = scrollState,
        zoomState = zoomState,
        //modifier = Modifier.fillMaxHeight()
    )
}