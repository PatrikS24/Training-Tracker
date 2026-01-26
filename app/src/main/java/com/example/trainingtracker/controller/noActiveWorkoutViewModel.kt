package com.example.trainingtracker.controller

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.trainingtracker.model.Workout
import kotlinx.coroutines.launch
import java.util.Date

class noActiveWorkoutViewModel(application: Application) : AndroidViewModel(application) {


    var isActiveWorkout: Boolean by mutableStateOf(false)
    private val workoutDao = DatabaseProvider
        .getDatabase(application)
        .workoutDao()

    init {
        getActiveWorkout()
    }

    fun getActiveWorkout() {
        viewModelScope.launch {
            val active = workoutDao.getActive()
            isActiveWorkout = !active.isEmpty()
        }
    }

    fun createWorkout() {
        var newWorkout = WorkoutDB(
            name = "workout",
            completed = false,
            id = 0,
            startTime = Date(),
            durationMinutes = 0
        )
        viewModelScope.launch {
            workoutDao.insert(newWorkout)
            getActiveWorkout()
        }
    }

    fun deleteAllWorkouts() {
        viewModelScope.launch {
            workoutDao.deleteAll()
            getActiveWorkout()
        }
    }

}