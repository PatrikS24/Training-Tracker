package com.example.trainingtracker.model

class Exercise(val id : Int) {
    var movement : Movement? = null;
    var sets : MutableList<ExerciseSet> = mutableListOf<ExerciseSet>();
    var notes : String = "";
    var orderIndex = 0;
}