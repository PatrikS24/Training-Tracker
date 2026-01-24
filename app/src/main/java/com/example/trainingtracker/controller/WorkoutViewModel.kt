package com.example.trainingtracker.controller

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.trainingtracker.model.Exercise
import com.example.trainingtracker.model.ExerciseSet
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

    private val exerciseSetDao = DatabaseProvider
        .getDatabase(application)
        .exerciseSetDao()


    fun hasActiveWorkout(): Boolean {
        return !activeWorkout.equals( nonActiveWorkout )
    }

    fun getActiveWorkout() {
        if (hasActiveWorkout()) return

        viewModelScope.launch {
            val active = workoutDao.getActive()
            if (!active.isEmpty() && !active[0].completed) {
                val newActive = Workout(active[0].id, active[0].name)
                newActive.startTime = active[0].startTime
                newActive.updateDuration()
                newActive.completed = active[0].completed
                activeWorkout = newActive
                getExercisesForWorkout()
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
            if (hasActiveWorkout()) {
                state = WorkoutScreenState.active
            }
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

    fun deleteActiveWorkout() {
        viewModelScope.launch {
            workoutDao.deleteById(activeWorkout.id)
            activeWorkout = nonActiveWorkout
            setScreenState(WorkoutScreenState.inactive)
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

    fun deleteExercise(exercise: Exercise) {
        viewModelScope.launch {
            exerciseDao.deleteById(exercise.id)
            activeWorkout.exercises.remove(exercise)
        }
    }

    fun getExercisesForWorkout() {
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

                    getSetsForExercise(exercise)

                    activeWorkout.exercises.add(exercise)
                }
            }
        }
    }

    fun updateExercise(exercise: Exercise?) {
        if (exercise != null) {
            viewModelScope.launch {
                val dbExercise = ExerciseDB(id = exercise.id,
                    movementId = exercise.movement?.id,
                    workoutId = activeWorkout.id,
                    orderIndex = exercise.orderIndex,
                    notes = exercise.notes)
                exerciseDao.updateExercise(dbExercise)
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

    fun createSet(exercise: Exercise) {
        viewModelScope.launch {
            var setDb = ExerciseSetDB(
                id = 0,
                exerciseId = exercise.id,
                orderIndex = exercise.sets.size + 1
            )
            var id = exerciseSetDao.insert(setDb)
            var set = ExerciseSet(
                id = id.toInt()
            )
            set.orderIndex = exercise.sets.size + 1
            exercise.sets.add(set)
        }
    }

    suspend fun getSetsForExercise(exercise: Exercise) {
        if (hasActiveWorkout()) {
            exercise.sets.clear()
            //viewModelScope.launch {
                val dbSets = exerciseSetDao.getAllExerciseSetsById(exercise.id)
                for (dbSet in dbSets) {
                    var set = ExerciseSet(id = dbSet.id)

                    set.weight = dbSet.weight
                    set.reps = dbSet.reps
                    set.orderIndex = dbSet.orderIndex
                    set.isWarmup = dbSet.isWarmup
                    set.completed = dbSet.completed

                    exercise.sets.add(set)
                }
            //}
        }
    }

    fun updateSet(set: ExerciseSet, exercise: Exercise) {
        viewModelScope.launch {
            val dbSet = ExerciseSetDB(
                id = set.id,
                exerciseId = exercise.id,
                orderIndex = set.orderIndex,
                weight = set.weight,
                reps = set.reps,
                isWarmup = set.isWarmup,
                completed = set.completed
            )
            exerciseSetDao.updateSet(dbSet)
        }
    }

    fun updateSetWeight(set: ExerciseSet, weight: Double) {
        viewModelScope.launch {
            exerciseSetDao.updateWeight(set.id, weight)
        }
    }

    fun updateSetReps(set: ExerciseSet, reps: Int) {
        viewModelScope.launch {
            exerciseSetDao.updateReps(set.id, reps)
        }
    }

    fun updateSetCompleted(set: ExerciseSet, completed: Boolean) {
        viewModelScope.launch {
            exerciseSetDao.updateCompleted(set.id, completed)
        }
    }
}

enum class WorkoutScreenState {
    inactive, active, search
}