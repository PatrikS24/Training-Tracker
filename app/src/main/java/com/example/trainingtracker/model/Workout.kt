package com.example.trainingtracker.model

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Duration
import java.time.temporal.Temporal
import java.util.Date

class Workout(val id : Int, name: String) {
    var exercises : MutableList<Exercise> = mutableListOf<Exercise>();
    var startTime : Date? = null
    var durationMinutes : Int = 0;

    fun updateDuration() {
        durationMinutes = Duration.between(startTime as Temporal?, Date() as Temporal?).toMinutes() as Int;
    }
}