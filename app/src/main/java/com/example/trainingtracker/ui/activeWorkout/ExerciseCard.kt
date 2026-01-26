package com.example.trainingtracker.ui.activeWorkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.trainingtracker.controller.WorkoutScreenState
import com.example.trainingtracker.controller.WorkoutViewModel
import com.example.trainingtracker.model.Exercise

@Composable
fun ExerciseCard( viewModel: WorkoutViewModel, exercise: Exercise, onSearchTriggered: (Exercise?) -> Unit ) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(5.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.Start) {
            if (exercise.movement == null) { // No movement chosen yet
                Row(
                    modifier = Modifier.fillMaxWidth().padding(5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = {
                        viewModel.state = WorkoutScreenState.search
                        onSearchTriggered(exercise)
                    },
                        shape = MaterialTheme.shapes.medium
                    ) {
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

                Row (
                    modifier = Modifier.fillMaxWidth().padding(5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text("Set")
                    Text("Previous", modifier = Modifier.width(90.dp))
                    Text("Kg")
                    Text("Reps")
                    Text("Done")
                }

                Column(
                    modifier = Modifier.padding(5.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    for (set in exercise.sets) {
                        SetCard(viewModel, set, exercise)
                    }

                    Row {
                        TextButton(onClick = {
                            viewModel.createSet(exercise)
                        }) {
                            Text("Add Set")
                        }

                        if (exercise.sets.size > 1) {
                            TextButton(onClick = {
                                viewModel.deleteSet(exercise.sets.last(), exercise)
                            }) {
                                Text("Remove Set")
                            }
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun ExerciseCardOptionsMenu( viewModel: WorkoutViewModel, exercise: Exercise, onSearchTriggered: (Exercise?) -> Unit ) {
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