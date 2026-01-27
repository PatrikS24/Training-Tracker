package com.example.trainingtracker.ui.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.shader.toShaderProvider
import com.patrykandpatrick.vico.compose.common.shape.toVicoShape
import com.patrykandpatrick.vico.core.cartesian.Scroll
import com.patrykandpatrick.vico.core.cartesian.Zoom
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.Fill
import java.text.DecimalFormat
import java.time.temporal.ChronoField
import java.time.temporal.WeekFields
import java.util.Locale
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
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        viewModel.movementName?.let { Text(it, fontSize = 25.sp) }

        val scrollState = rememberScrollState()

        if (!viewModel.hasMovementData) {
            Column(
                modifier = Modifier.fillMaxSize().padding(18.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                StatisticCard(
                    "No data available for this movement"
                ) { }
            }
        } else {

            Column(
                modifier = Modifier.fillMaxSize().padding(10.dp).verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                StatisticCard("Personal best", modifier = Modifier) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        val best = viewModel.getBestSet()
                        val date = "${best?.date?.get(ChronoField.DAY_OF_MONTH)}." +
                                "${best?.date?.get(ChronoField.MONTH_OF_YEAR)}." +
                                "${best?.date?.get(ChronoField.YEAR).toString().subSequence(2, 4)}"
                        Text("Date: $date")
                        Text("Weight: ${best?.weight} Kg")
                        Text("Reps: ${best?.reps}")
                    }

                }

                StatisticCard(
                    "Weight"
                ) {
                    MovementLineChart(viewModel, viewModel.byWeightModelProducer, "Kg")
                }

                StatisticCard(
                    "Repetitions"
                ) {
                    MovementLineChart(viewModel, viewModel.byRepsModelProducer, "Reps", 1.0)
                }

                StatisticCard("Frequency per week", modifier = Modifier) {
                    MovementFrequencyChart(viewModel)
                }
            }
        }
    }
}


@Composable
fun MovementLineChart(viewModel: MovementStatisticsViewModel,
                      modelProducer: CartesianChartModelProducer,
                      axisLabel: String,
                      step: Double? = null) {

    if (!viewModel.hasMovementData) {
        return
    }

    val dayLabels = viewModel.dayLabels

    // Formatter is UI logic, so we 'remember' it here
    val labelFormatter = remember(dayLabels) {
        CartesianValueFormatter { _, value, _ ->
            val date = dayLabels.getOrNull(value.roundToInt())
            if (date != null) {
                "${date.get(ChronoField.DAY_OF_MONTH)}." +
                "${date.get(ChronoField.MONTH_OF_YEAR)}." +
                "${date.get(ChronoField.YEAR).toString().subSequence(2,4)}"
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

    val decimalFormat = if (step?.rem(1) == 0.0) "#" else "#.#"

    val marker = rememberMarker()

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
                labelRotationDegrees = 0f,
                guideline = null
            ),
            startAxis = VerticalAxis.rememberStart(
                itemPlacer = VerticalAxis.ItemPlacer.step({ step }),
                title = axisLabel,
                titleComponent = rememberTextComponent(
                    color = MaterialTheme.colorScheme.onSurface,
                    textSize = 12.sp,
                    lineCount = 1
                ),
                valueFormatter = CartesianValueFormatter.decimal(DecimalFormat(decimalFormat))
            ),
            marker = marker
        ),
        modelProducer = modelProducer,
        scrollState = scrollState,
        zoomState = zoomState,
    )
}

@Composable
fun MovementFrequencyChart(viewModel: MovementStatisticsViewModel) {
    val weekLabels = viewModel.weekLabels

    if (!viewModel.hasMovementData) {
        return
    }

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
        initialZoom = remember { Zoom.x(8.0) }
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
                valueFormatter = CartesianValueFormatter.decimal(DecimalFormat("#")),
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