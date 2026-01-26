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
    val modelProducer = CartesianChartModelProducer()
    private var weeklyData: List<WeeklyCount> = emptyList()

    var weekLabels by mutableStateOf<List<LocalDate>>(emptyList())
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
        weeklyData = fillMissingWeeks(rawDummyData)
        updateChartState()
        
        // Load actual data from database
        //loadWeeklyData()
    }

    private fun fillMissingWeeks(data: List<WeeklyCount>): List<WeeklyCount> {
        if (data.isEmpty()) return emptyList()
        
        val sortedData = data.sortedBy { it.weekStart }
        val startDate = sortedData.first().weekStart
        val endDate = sortedData.last().weekStart
        
        val result = mutableListOf<WeeklyCount>()
        var currentDate = startDate
        
        val dataMap = sortedData.associateBy { it.weekStart }
        
        while (!currentDate.isAfter(endDate)) {
            result.add(dataMap[currentDate] ?: WeeklyCount(currentDate, 0))
            currentDate = currentDate.plusWeeks(1)
        }
        
        return result
    }

    private fun updateChartState() {
        weekLabels = weeklyData.map { it.weekStart }
        if (weeklyData.isNotEmpty()) {
            viewModelScope.launch {
                modelProducer.runTransaction {
                    columnSeries {
                        series(
                            weeklyData.mapIndexed { index, _ -> index.toFloat() },
                            weeklyData.map { it.count.toFloat() }
                        )
                    }
                }
            }
        }
    }

    fun loadWeeklyData() {
        viewModelScope.launch {
            val data = repository.getWorkouts()
            val frequencyMap = data.groupingBy { it.startTime.toLocalDate().startOfWeek() }.eachCount()
            val rawSortedData = frequencyMap.map{ WeeklyCount(it.key, it.value) }.sortedBy { it.weekStart }
            
            val filledData = fillMissingWeeks(rawSortedData)
            
            if (filledData.isNotEmpty()) {
                weeklyData = filledData
                updateChartState()
            }
        }
    }

    data class WeeklyCount(
        val weekStart: LocalDate,
        val count: Int
    )

    fun Date.toLocalDate(): LocalDate =
        Instant.ofEpochMilli(time)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

    fun LocalDate.startOfWeek(): LocalDate =
        with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))


}
