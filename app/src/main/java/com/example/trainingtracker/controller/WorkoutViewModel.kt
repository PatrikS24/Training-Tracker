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

class WorkoutViewModel(application: Application) :
    AndroidViewModel(application) {
    var activeWorkout: Workout? by mutableStateOf(null)

    private val dao = DatabaseProvider
        .getDatabase(application)
        .workoutDao()

    fun getActiveWorkout() {
        viewModelScope.launch {
            var active = dao.getActive()
            if (!active.isEmpty() && (active[0] != null)) {
                activeWorkout = Workout(active[0].id, active[0].name)
                activeWorkout?.startTime = active[0].startTime
                activeWorkout?.updateDuration()
                activeWorkout?.completed = active[0].completed
            }
        }
    }

    fun createWorkout() {
        var newWorkout = WorkoutDB(
            name = "workout", completed = false,
            id = 0,
            startTime = Date(),
            durationMinutes = 0
        )
        viewModelScope.launch {
            dao.insert(newWorkout)
            getActiveWorkout()
        }
    }

    fun updateDatabase() {
        viewModelScope.launch {
            val a = activeWorkout
            if (a != null) {
                var updatedWorkoutDB = WorkoutDB(
                    id = a.id,
                    name = a.name,
                    startTime = a.startTime,
                    durationMinutes = a.durationMinutes,
                    completed = a.completed
                )
                dao.updateWorkout(updatedWorkoutDB)
            }

        }
    }

    fun deleteAllWorkouts() {
        viewModelScope.launch {
            dao.deleteAll()
            getActiveWorkout()
            activeWorkout = null
        }
    }
}
