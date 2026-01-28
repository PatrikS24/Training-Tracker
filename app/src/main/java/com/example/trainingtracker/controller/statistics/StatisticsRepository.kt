package com.example.trainingtracker.controller.statistics

import com.example.trainingtracker.controller.HeaviestSetWithTime
import com.example.trainingtracker.controller.StatisticsDao
import com.example.trainingtracker.controller.WorkoutDB

class StatisticsRepository(private val statisticsDao: StatisticsDao) {
    suspend fun getCompletedWorkouts(): List<WorkoutDB> {
        return statisticsDao.getAllCompletedWorkouts()
    }

    suspend fun getHeaviestSetsForMovement(movementId: Int): List<HeaviestSetWithTime> {
        return statisticsDao.getHeaviestSetsForMovement(movementId)
    }

    suspend fun getMovementName(movementId: Int): String {
        return statisticsDao.getMovementName(movementId) ?: "Unknown Movement"
    }
}