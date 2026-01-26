package com.example.trainingtracker.controller

import com.example.trainingtracker.model.Workout

class StatisticsRepository(private val statisticsDao: StatisticsDao) {
    suspend fun getWorkouts(): List<WorkoutDB> {
        return statisticsDao.getAllWorkouts()
    }
}