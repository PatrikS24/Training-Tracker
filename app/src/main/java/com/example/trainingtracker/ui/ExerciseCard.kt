package com.example.trainingtracker.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trainingtracker.controller.ExerciseDB
import com.example.trainingtracker.controller.WorkoutScreenState
import com.example.trainingtracker.controller.WorkoutViewModel
import com.example.trainingtracker.model.Exercise

@Composable
fun ExerciseCard( viewModel: WorkoutViewModel = viewModel(), exercise: Exercise, onSearchTriggered: (Exercise?) -> Unit ) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(5.dp),
            horizontalAlignment = Alignment.End) {
            if (exercise.movement == null) { // No movement chosen yet
                Row(
                    modifier = Modifier.fillMaxWidth().padding(5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = {
                        viewModel.state = WorkoutScreenState.search
                        onSearchTriggered(exercise)
                    }) {
                        Text("Choose movement")
                    }
                    TextButton(onClick = {
                        viewModel.deleteExercise(exercise)
                    }){
                        Text("Delete")
                    }
                }

            } else { // Movement chosen
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ){
                    ExerciseCardOptionsMenu( viewModel, exercise, onSearchTriggered = {
                        onSearchTriggered(exercise)
                    })
                    Text(exercise.movement!!.name)
                }
            }

        }
    }
}

@Composable
fun ExerciseCardOptionsMenu( viewModel: WorkoutViewModel = viewModel(), exercise: Exercise, onSearchTriggered: (Exercise?) -> Unit ) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More options"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Change movement") },
                onClick = {
                    expanded = false
                    viewModel.setScreenState( WorkoutScreenState.search )
                    onSearchTriggered(exercise)
                }
            )
            DropdownMenuItem(
                text = { Text("Delete exercise") },
                onClick = {
                    expanded = false
                    viewModel.deleteExercise(exercise)
                }
            )
        }
    }
}