package com.example.trainingtracker.model

import androidx.compose.runtime.mutableStateListOf
import java.time.Duration
import java.time.temporal.Temporal
import java.util.Date

class Workout(val id : Int, var name: String) {
    var exercises : MutableList<Exercise> = mutableStateListOf<Exercise>();
    var startTime : Date = Date()
    var durationMinutes : Int = 0;

    var completed : Boolean = false

    fun updateDuration() {
        durationMinutes = Duration.between(startTime.toInstant(), Date().toInstant()).toMinutes().toInt();
    }
}