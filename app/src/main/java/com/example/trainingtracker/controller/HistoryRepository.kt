package com.example.trainingtracker.controller

import androidx.compose.runtime.toMutableStateList
import com.example.trainingtracker.model.Exercise
import com.example.trainingtracker.model.ExerciseSet
import com.example.trainingtracker.model.Movement
import com.example.trainingtracker.model.Workout

class HistoryRepository(
    private val dao: HistoryDao)
{

    suspend fun getWorkouts(): MutableList<Workout> {
        val dbWorkouts = dao.getWorkoutHistory()

        val workouts = dbWorkouts.map { it1 ->
            Workout(id = it1.workout.id, name = it1.workout.name).apply {
                completed = it1.workout.completed
                startTime = it1.workout.startTime
                durationMinutes = it1.workout.durationMinutes
                exercises = it1.exercises.map { it2 ->
                    Exercise(id = it2.exercise.id).apply {
                        if (it2.movement != null) {
                            movement = Movement(it2.movement.id, it2.movement.name)
                        }
                        notes = it2.exercise.notes
                        sets = it2.sets.map { it3 ->
                            ExerciseSet(it3.id).apply {
                                completed = it3.completed
                                weight = it3.weight
                                reps = it3.reps
                                orderIndex = it3.orderIndex
                                isWarmup = it3.isWarmup
                            }
                        }.sortedBy { it.orderIndex }.toMutableList()
                } }.sortedBy { it.orderIndex }.toMutableList()
            }
        }.toMutableStateList()
        return workouts
    }

    suspend fun deleteWorkout(workout: Workout) {
        dao.deleteWorkoutById(workout.id)
    }
}