package com.example.trainingtracker.controller.statistics

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.trainingtracker.controller.DatabaseProvider
import com.example.trainingtracker.controller.ExerciseSetDB
import com.example.trainingtracker.controller.statistics.StatisticsRepository
import com.example.trainingtracker.controller.statistics.GeneralStatisticsViewModel.WeeklyCount
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.Instant
import java.time.temporal.TemporalAdjusters
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
    val frequencyModelProducer = CartesianChartModelProducer()


    private var movementData: List<DailyMovementData> = emptyList()
    private var weeklyFrequencyData: List<WeeklyCount> = emptyList()


    var dayLabels by mutableStateOf<List<LocalDate>>(emptyList())
        private set

    var weekLabels by mutableStateOf<List<LocalDate>>(emptyList())
        private set

    var hasMovementData by mutableStateOf(false)
        private set


    data class DailyMovementData(
        val date: LocalDate,
        val weight: Double,
        val reps: Int,
    )

    data class WeeklyCount(
        val weekStart: LocalDate,
        val count: Int
    )

    /*
    init {
        val today = LocalDate.now().startOfWeek()
        val rawDummyData = listOf(
            WeeklyCount(today.minusWeeks(7), 2),
            WeeklyCount(today.minusWeeks(4), 1),
            WeeklyCount(today.minusWeeks(3), 3),
            WeeklyCount(today.minusWeeks(2), 4),
            WeeklyCount(today.minusWeeks(1), 2),
            WeeklyCount(today, 1),
        )
        weeklyFrequencyData = fillMissingWeeks(rawDummyData)
        updateWorkoutFrequencyChartState()
    }
     */

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

            val frequencyMap = movementData.groupingBy { it.date.startOfWeek() }.eachCount()
            val rawSortedData = frequencyMap.map{ WeeklyCount(it.key, it.value) }.sortedBy { it.weekStart }
            val filledData = fillMissingWeeks(rawSortedData)

            if (filledData.isNotEmpty()) {
                weeklyFrequencyData = filledData
                updateWorkoutFrequencyChartState()
            }

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

    private fun updateWorkoutFrequencyChartState() {
        weekLabels = weeklyFrequencyData.map { it.weekStart }
        if (weeklyFrequencyData.isNotEmpty()) {
            viewModelScope.launch {
                frequencyModelProducer.runTransaction {
                    columnSeries {
                        series(
                            weeklyFrequencyData.mapIndexed { index, _ -> index.toFloat() },
                            weeklyFrequencyData.map { it.count.toFloat() }
                        )
                    }
                }
            }
        }
    }

    fun getBestSet(): DailyMovementData? {
        if (movementData == null) return null

        return movementData.maxByOrNull { it.weight }
    }


    private fun fillMissingWeeks(data: List<WeeklyCount>): List<WeeklyCount> {
        if (data.isEmpty()) return emptyList()

        val sortedData = data.sortedBy { it.weekStart }
        val startDate = sortedData.first().weekStart
        val endDate = LocalDate.now().startOfWeek()

        val result = mutableListOf<WeeklyCount>()
        var currentDate = startDate

        val dataMap = sortedData.associateBy { it.weekStart }

        while (!currentDate.isAfter(endDate)) {
            result.add(dataMap[currentDate] ?: WeeklyCount(currentDate, 0))
            currentDate = currentDate.plusWeeks(1)
        }

        return result
    }

    private fun Date.toLocalDate(): LocalDate =
        Instant.ofEpochMilli(time)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

    private fun LocalDate.startOfWeek(): LocalDate =
        with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
}

