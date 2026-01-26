package com.example.trainingtracker.ui.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trainingtracker.controller.statistics.MovementStatisticsViewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.continuous
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.shader.toShaderProvider
import com.patrykandpatrick.vico.core.cartesian.Scroll
import com.patrykandpatrick.vico.core.cartesian.Zoom
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.Fill
import java.time.temporal.ChronoField
import kotlin.math.roundToInt

@Composable
fun MovementStatisticsScreen(
    viewModel: MovementStatisticsViewModel = viewModel(),
    movementId: Int
) {
    LaunchedEffect(Unit) {
        viewModel.setMovement(movementId)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        viewModel.movementName?.let { Text(it, fontSize = 25.sp) }

        MovementLineChartCard(viewModel, viewModel.byWeightModelProducer,"Weight", "Kg")

        MovementLineChartCard(viewModel, viewModel.byRepsModelProducer, "Repetitions", "Reps", 1.0)

    }
}

@Composable
fun MovementLineChartCard(
    viewModel: MovementStatisticsViewModel,
    modelProducer: CartesianChartModelProducer,
    title: String,
    axisLabel: String,
    step: Double = 0.5
) {
    Card() {
        Column(
            modifier = Modifier.padding(5.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
            ) {
            Text(title)
            MovementLineChart(viewModel, modelProducer, axisLabel, step)
        }
    }
}

@Composable
fun MovementLineChart(viewModel: MovementStatisticsViewModel,
                      modelProducer: CartesianChartModelProducer,
                      axisLabel: String,
                      step: Double = 0.5) {

    if (!viewModel.hasMovementData) {
        Text("No data available for this movement")
        return
    }

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
        initialZoom = remember { Zoom.x(20.0) }
    )

    val decimalFormat = if (step % 1 == 0.0) "#" else "#.#"

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                lineProvider = LineCartesianLayer.LineProvider.series(
                    LineCartesianLayer.rememberLine(
                        fill = LineCartesianLayer.LineFill.single(fill(MaterialTheme.colorScheme.primary)),
                        areaFill = LineCartesianLayer.AreaFill.single(
                            fill = Fill(
                                shaderProvider = Brush.verticalGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), // Top
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0f)    // Bottom
                                    )
                                ).toShaderProvider()
                            )
                        ),
                        stroke = LineCartesianLayer.LineStroke.continuous(),
                        pointConnector = LineCartesianLayer.PointConnector.cubic(0.3f)
                    )
                )
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                valueFormatter = labelFormatter,
                label = rememberTextComponent(color = MaterialTheme.colorScheme.onSurface, textSize = 12.sp),
                labelRotationDegrees = 0f
            ),
            startAxis = VerticalAxis.rememberStart(
                itemPlacer = VerticalAxis.ItemPlacer.step({ step }),
                title = axisLabel,
                titleComponent = rememberTextComponent(
                    color = MaterialTheme.colorScheme.onSurface,
                    textSize = 12.sp,
                    lineCount = 1
                ),
                valueFormatter = CartesianValueFormatter.decimal(java.text.DecimalFormat(decimalFormat))
            ),
        ),
        modelProducer = modelProducer,
        scrollState = scrollState,
        zoomState = zoomState,
    )
}