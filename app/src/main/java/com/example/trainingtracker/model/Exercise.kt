package com.example.trainingtracker.model

import androidx.compose.runtime.mutableStateListOf

class Exercise(val id : Int) {
    var movement : Movement? = null;
    var sets : MutableList<ExerciseSet> = mutableStateListOf<ExerciseSet>();
    var notes : String = "";
    var orderIndex = 0;
}