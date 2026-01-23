package com.example.trainingtracker.controller

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.trainingtracker.model.Exercise
import com.example.trainingtracker.model.Movement
import com.example.trainingtracker.model.Workout
import kotlinx.coroutines.launch
import java.util.Date

class WorkoutViewModel(application: Application) :
    AndroidViewModel(application) {
    val nonActiveWorkout = Workout(id = 999999, name = "non active workout")
    var activeWorkout: Workout by mutableStateOf(nonActiveWorkout)

    var state : WorkoutScreenState by mutableStateOf(WorkoutScreenState.inactive)

    var movements = mutableStateListOf<MovementDB>()

    private val workoutDao = DatabaseProvider
        .getDatabase(application)
        .workoutDao()

    private val exerciseDao = DatabaseProvider
        .getDatabase(application)
        .exerciseDao()

    private val movementDao = DatabaseProvider
        .getDatabase(application)
        .movementDao()


    fun hasActiveWorkout(): Boolean {
        return !activeWorkout.equals( nonActiveWorkout )
    }

    fun getActiveWorkout() {
        if (hasActiveWorkout()) return

        viewModelScope.launch {
            var active = workoutDao.getActive()
            if (!active.isEmpty() && !active[0].completed) {
                activeWorkout = Workout(active[0].id, active[0].name)
                activeWorkout.startTime = active[0].startTime
                activeWorkout.updateDuration()
                activeWorkout.completed = active[0].completed
                updateExercises()
                state = WorkoutScreenState.active
            } else {
                state = WorkoutScreenState.inactive
            }
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
            state = WorkoutScreenState.active
        }
    }

    fun updateDatabase() {
        viewModelScope.launch {
            val a = activeWorkout
            val updatedWorkoutDB = WorkoutDB(
                id = a.id,
                name = a.name,
                startTime = a.startTime,
                durationMinutes = a.durationMinutes,
                completed = a.completed
            )
            workoutDao.updateWorkout(updatedWorkoutDB)
        }
    }

    fun deleteAllWorkouts() {
        viewModelScope.launch {
            workoutDao.deleteAll()
            getActiveWorkout()
            activeWorkout = nonActiveWorkout
            state = WorkoutScreenState.inactive
        }
    }

    fun createExercise() {
        viewModelScope.launch {
            var exerciseDb = ExerciseDB(
                id = 0,
                movementId = null,
                workoutId = activeWorkout.id,
                orderIndex = activeWorkout.exercises.size,
                notes = ""
            )
            var id = exerciseDao.insert(exerciseDb)
            var exercise = Exercise(
                id = id.toInt()
            )
            exercise.orderIndex = activeWorkout.exercises.size
            activeWorkout.exercises.add(exercise)
        }
    }

    fun updateExercises() {
        if (hasActiveWorkout()) {
            activeWorkout.exercises.clear()
            viewModelScope.launch {
                val dbExercises = exerciseDao.getAllExercisesById(activeWorkout.id)
                for (dbExercise in dbExercises) {
                    var exercise = Exercise(id = dbExercise.id)
                    val dbMovement = movementDao.getMovementById(dbExercise.movementId)
                    try {
                        exercise.movement = Movement(id = dbMovement.id, name = dbMovement.name)
                    } catch (e: NullPointerException) {
                        exercise.movement = null
                    }
                    exercise.orderIndex = dbExercise.orderIndex
                    exercise.notes = dbExercise.notes
                    // Todo: get sets and create sets objects and add them to exercise object list variable
                    activeWorkout.exercises.add(exercise)
                }
            }
        }
    }

    fun setScreenState(screenState: WorkoutScreenState) {
        state = screenState
    }

    fun getAllMovements() {
        viewModelScope.launch {
            val dbMovements: List<MovementDB> = movementDao.getAllMovements()

            movements.clear()
            movements.addAll(dbMovements)
            movements.sortBy { it.name.lowercase() }
        }
    }
}

enum class WorkoutScreenState {
    inactive, active, search
}