package com.example.trainingtracker.model

class ExerciseSet(val id : Int = 0) {
    var weight : Float = 0f;
    var reps : Int = 0;
    var orderIndex = 0;
    var isWarmup : Boolean = false;
    var completed : Boolean = false;
}