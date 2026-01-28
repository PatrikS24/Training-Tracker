package com.example.trainingtracker.ui.workout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trainingtracker.controller.noActiveWorkoutViewModel
import com.example.trainingtracker.ui.generalUi.ConfirmDialog

@Composable
fun NoActiveWorkoutScreen(viewModel: noActiveWorkoutViewModel = viewModel(), onWorkoutStarted: () -> Unit) {
    // todo: add workout history
    Column (
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center
    ){

        Button(onClick = {
            viewModel.createWorkout()
            onWorkoutStarted()
        },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium)
        {
            Text("Start new workout")
        }

        Spacer(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.07f))

        var showDeleteWorkoutTableDialog by remember { mutableStateOf(false) }
        Button(
            onClick = {
                showDeleteWorkoutTableDialog = true
            },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Delete workout table")
        }
        if (showDeleteWorkoutTableDialog) {
            ConfirmDialog("Are you sure you want to delete all workout tables?",
                onIsSure = {
                        isSure ->
                    showDeleteWorkoutTableDialog = false
                    if (isSure) {viewModel.deleteAllWorkouts()}
                }) {}
        }
    }
}