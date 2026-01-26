package com.example.trainingtracker.controller

import com.example.trainingtracker.model.Workout

class StatisticsRepository(private val statisticsDao: StatisticsDao) {
    suspend fun getCompletedWorkouts(): List<WorkoutDB> {
        return statisticsDao.getAllCompletedWorkouts()
    }

    suspend fun getHeaviestSetsForMovement(movementId: Int): List<HeaviestSetWithTime> {
        return statisticsDao.getHeaviestSetsForMovement(movementId)
    }
}
