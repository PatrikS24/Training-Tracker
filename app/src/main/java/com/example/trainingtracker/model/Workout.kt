package com.example.trainingtracker.model

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Duration
import java.time.LocalDateTime

class Workout(val id : Int) {
    var name : String = "";
    var exercises : MutableList<Exercise> = mutableListOf<Exercise>();
    @RequiresApi(Build.VERSION_CODES.O)
    var startTime : LocalDateTime = LocalDateTime.now();
    var durationMinutes : Int = 0;

    fun updateDuration() {
        durationMinutes = Duration.between(startTime, LocalDateTime.now()).toMinutes() as Int;
    }
}