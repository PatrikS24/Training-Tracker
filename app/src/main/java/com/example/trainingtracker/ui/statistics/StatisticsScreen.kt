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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.continuous
import com.patrykandpatrick.vico.compose.cartesian.layer.point
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.component.shapeComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.insets
import com.patrykandpatrick.vico.compose.common.shader.toShaderProvider
import com.patrykandpatrick.vico.compose.common.shape.rounded
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.decoration.HorizontalLine
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.Fill
import com.patrykandpatrick.vico.core.common.Position
import com.patrykandpatrick.vico.core.common.shape.CorneredShape

@Composable fun StatisticsScreen(onGeneralStatisticsClicked: () -> Unit, onMovementStatisticsClicked: () -> Unit)
{
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
        ) {

        Box (
            modifier = Modifier.fillMaxWidth(0.6f).height(100.dp),
        ){
            SimpleLineChart(modifier = Modifier.fillMaxSize())
        }

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

@Composable
fun SimpleLineChart(modifier: Modifier) {
    val modelProducer = remember { CartesianChartModelProducer() }


    val lineColors = listOf(Color(0xff916cda), Color(0xffd877d8), Color(0xfff094bb))

    LaunchedEffect(Unit) {
        modelProducer.runTransaction {
            // Adds a simple line with y-values: 2, 8, 4, 10, 1
            lineSeries { series(5, 8, 6, 10, 5)  }
        }
    }

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                LineCartesianLayer.LineProvider.series(

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
            startAxis = VerticalAxis.rememberStart(
                tick = null,
                guideline = null,
                line = null,
                label = null
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                tick = null,
                guideline = null,
                line = null,
                label = null
            ),
        ),
        modelProducer = modelProducer,
        modifier = modifier
    )
}