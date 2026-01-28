package com.example.trainingtracker.controller

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.trainingtracker.controller.statistics.StatisticsRepository
import com.example.trainingtracker.model.Workout
import com.example.trainingtracker.ui.history.HistoryScreen
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = DatabaseProvider
        .getDatabase(application)
        .historyDao()

    val repository = HistoryRepository(dao)

    var workouts: MutableList<Workout> = mutableStateListOf()

    fun loadWorkouts() {
        viewModelScope.launch {
            workouts.clear()
            workouts.addAll(repository.getWorkouts())
        }
    }

    fun hasData(): Boolean {
        return !workouts.isEmpty()
    }

    fun deleteWorkout(workout: Workout) {
        viewModelScope.launch {
            repository.deleteWorkout(workout)
            workouts.remove(workout)
        }
    }

}