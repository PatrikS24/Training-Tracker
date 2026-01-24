package com.example.trainingtracker.model

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewModelScope
import com.example.trainingtracker.controller.WorkoutViewModel
import kotlinx.coroutines.launch

class Exercise(val id : Int) {
    var movement : Movement? = null;
    var sets : MutableList<ExerciseSet> = mutableStateListOf<ExerciseSet>();
    var notes : String = "";
    var orderIndex = 0;
    var previousSets: MutableList<ExerciseSet> = mutableStateListOf<ExerciseSet>()

    fun onMovementChosen( viewModel: WorkoutViewModel) {
        viewModel.getPreviousSetsForExercise(this)
        if (sets.isEmpty()) {
            viewModel.createSet(this)
        }
    }
}