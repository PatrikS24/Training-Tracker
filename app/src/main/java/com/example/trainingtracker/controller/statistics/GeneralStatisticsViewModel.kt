package com.example.trainingtracker.controller.statistics

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.trainingtracker.controller.DatabaseProvider
import com.example.trainingtracker.controller.StatisticsRepository
import com.example.trainingtracker.controller.WorkoutDB
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import java.util.Date

class GeneralStatisticsViewModel(application: Application) : AndroidViewModel(application){

    private val statisticsDao = DatabaseProvider
        .getDatabase(application)
        .statisticsDao()
    val repository = StatisticsRepository(statisticsDao)
    val frequencyModelProducer = CartesianChartModelProducer()

    val durationModelProducer = CartesianChartModelProducer()

    private var weeklyFrequencyData: List<WeeklyCount> = emptyList()

    private var dailyDurationData: List<DailyDuration> = emptyList()


    var weekLabels by mutableStateOf<List<LocalDate>>(emptyList())
        private set

    var dayLabels by mutableStateOf<List<LocalDate>>(emptyList())
        private set

    init {
        // Initialize with dummy weekly data for immediate preview
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

        val rawDummyDurationData = listOf(
            DailyDuration(today.minusDays(7), 60),
            DailyDuration(today.minusDays(4), 73),
            DailyDuration(today.minusDays(3), 90),
            DailyDuration(today.minusWeeks(2), 96),
            DailyDuration(today.minusWeeks(1), 65)
            )

        dailyDurationData = rawDummyDurationData.sortedBy { it.date }
        updateDurationChartState()
        
        // Load actual data from database
        loadChartData()
    }

    data class WeeklyCount(
        val weekStart: LocalDate,
        val count: Int
    )

    data class DailyDuration(
        val date: LocalDate,
        val durationMinutes: Int
    )

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

    private fun updateDurationChartState() {
        dayLabels = dailyDurationData.map { it.date }
        if (dailyDurationData.isNotEmpty()) {
            viewModelScope.launch {
                durationModelProducer.runTransaction {
                    columnSeries {
                        series(
                            dailyDurationData.mapIndexed { index, _ -> index.toFloat() },
                            dailyDurationData.map { it.durationMinutes.toFloat() }
                        )
                    }
                }
            }
        }
    }

    fun loadChartData() {
        viewModelScope.launch {
            val data = repository.getCompletedWorkouts()
            loadWorkoutFrequencyData(data)
            loadWorkoutDurationData(data)
        }
    }

    fun loadWorkoutFrequencyData(data: List<WorkoutDB>) {
        val frequencyMap = data.groupingBy { it.startTime.toLocalDate().startOfWeek() }.eachCount()
        val rawSortedData = frequencyMap.map{ WeeklyCount(it.key, it.value) }.sortedBy { it.weekStart }

        val filledData = fillMissingWeeks(rawSortedData)

        if (filledData.isNotEmpty()) {
            weeklyFrequencyData = filledData
            updateWorkoutFrequencyChartState()
        }
    }

    fun loadWorkoutDurationData(data: List<WorkoutDB>) {
        val rawSortedData = data.map{ DailyDuration(it.startTime.toLocalDate(), it.durationMinutes) }.sortedBy { it.date }

        if (rawSortedData.isNotEmpty()) {
            dailyDurationData = rawSortedData
            updateDurationChartState()
        }
    }

    data class SetWithTime(
        val date: LocalDate,
        val weight: Double,
        val reps: Int,
        val isWarmup: Boolean
    )

    fun Date.toLocalDate(): LocalDate =
        Instant.ofEpochMilli(time)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

    fun LocalDate.startOfWeek(): LocalDate =
        with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))


}
