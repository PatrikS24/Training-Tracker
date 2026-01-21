package com.example.trainingtracker.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trainingtracker.controller.WorkoutViewModel

@Composable
fun WorkoutScreen( viewModel: WorkoutViewModel = viewModel() )
{
    viewModel.getActiveWorkout()

    when (viewModel.workoutActive) {
        false -> {
            NoActiveWorkout(viewModel)
        }
        true -> {
            ActiveWorkout(viewModel)
        }
    }
}

@Composable
fun NoActiveWorkout(viewModel: WorkoutViewModel = viewModel()) {
    // todo: add workout history
    Column (modifier = Modifier.fillMaxSize().padding(16.dp)){
        Spacer(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.5f))
        Button(onClick = {
            // todo: create new workout
            viewModel.swapActiveState()
        },
            modifier = Modifier.fillMaxWidth())
        {
            Text("Start new workout")
        }
        Spacer(modifier = Modifier.fillMaxWidth().fillMaxHeight())
    }
}

@Composable
fun ActiveWorkout(viewModel: WorkoutViewModel = viewModel()) {

}