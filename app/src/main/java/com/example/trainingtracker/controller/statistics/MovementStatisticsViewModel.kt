package com.example.trainingtracker.controller.statistics

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.trainingtracker.controller.DatabaseProvider
import com.example.trainingtracker.controller.StatisticsRepository
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.Instant
import java.util.Date

class MovementStatisticsViewModel(application: Application) : AndroidViewModel(application) {

    private val statisticsDao = DatabaseProvider
        .getDatabase(application)
        .statisticsDao()

    val repository = StatisticsRepository(statisticsDao)

    var movementId : Int? by mutableStateOf(null)
    var movementName : String? by mutableStateOf(null)


    val byWeightModelProducer = CartesianChartModelProducer()
    val byRepsModelProducer = CartesianChartModelProducer()

    private var movementData: List<DailyMovementData> = emptyList()

    var dayLabels by mutableStateOf<List<LocalDate>>(emptyList())
        private set

    var hasMovementData by mutableStateOf(false)
        private set


    data class DailyMovementData(
        val date: LocalDate,
        val weight: Double,
        val reps: Int,
    )

    fun setMovement(id: Int) {
        movementId = id
        loadMovementData(movementId)
    }

    fun loadMovementData(movementId: Int?) {
        if (movementId == null) return
        viewModelScope.launch {
            val data = repository.getHeaviestSetsForMovement(movementId)
            val dailyData = data.map {
                DailyMovementData(
                    date = it.startTime.toLocalDate(),
                    weight = it.set.weight,
                    reps = it.set.reps
            ) }
            val name = repository.getMovementName(movementId)
            movementData = dailyData.sortedBy { it.date }
            movementName = name
            updateMovementData()
        }
    }

    fun updateMovementData() {
        if (movementId == null || movementData.isEmpty()) return
        dayLabels = movementData.map { it.date }
        if (movementData.isNotEmpty()) {
            viewModelScope.launch {
                // Weight
                byWeightModelProducer.runTransaction {
                    lineSeries {
                        series(
                            movementData.mapIndexed { index, _ -> index.toFloat() },
                            movementData.map { it.weight }
                        )
                    }
                }
                // Reps
                byRepsModelProducer.runTransaction {
                    lineSeries {
                        series(
                            movementData.mapIndexed { index, _ -> index.toFloat() },
                            movementData.map { it.reps }
                        )
                    }
                }
                hasMovementData = true
            }
        }
    }

    private fun Date.toLocalDate(): LocalDate =
        Instant.ofEpochMilli(time)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
}

