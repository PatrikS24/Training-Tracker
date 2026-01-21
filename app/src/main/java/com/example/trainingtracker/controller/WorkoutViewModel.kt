package com.example.trainingtracker.controller

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.trainingtracker.model.Workout
import kotlinx.coroutines.launch

class WorkoutViewModel(application: Application) :
    AndroidViewModel(application) {
    var workoutActive: Boolean by mutableStateOf(false)
    var activeWorkout: Workout? = null

    private val dao = DatabaseProvider
        .getDatabase(application)
        .workoutDao()

    fun getActiveWorkout() {
        // todo: get active workout from db
        viewModelScope.launch {
            var active = dao.getActive()
            if (!active.isEmpty()) {
                activeWorkout = Workout(active[0].id, active[0].name)
                activeWorkout?.startTime = active[0].startTime
                activeWorkout?.updateDuration()
            }
        }
    }

    fun createWorkout() {

    }

    fun swapActiveState() {
        workoutActive = !workoutActive
    }
}
