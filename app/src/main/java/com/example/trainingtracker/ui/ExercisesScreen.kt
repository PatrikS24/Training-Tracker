package com.example.trainingtracker.ui

import android.app.Dialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trainingtracker.controller.MovementViewModel
import com.example.trainingtracker.model.Movement

@Composable
fun ExercisesScreen( viewModel: MovementViewModel = viewModel() )
{
    Box (modifier = Modifier.fillMaxSize()){
        LazyColumn {
            items(viewModel.movements) { movement ->
                MovementCard(viewModel, movement)
            }
        }
        NewMovementButton(viewModel = viewModel, modifier = Modifier.align(Alignment.BottomStart))
    }


}

@Composable
fun NewMovementButton(viewModel: MovementViewModel = viewModel(), modifier: Modifier = Modifier) {
    var dialogData by remember { mutableStateOf(DialogData(-1, false)) }

    FloatingActionButton(
        modifier = modifier.padding(16.dp),
        onClick = {
            dialogData = dialogData.copy(showDialog = true)
        },
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.secondary
    ) {
        Text("+", fontSize = 40.sp)
    }
    if (dialogData.showDialog) {
        NewMovementDialog(
            viewModel = viewModel,
            dialogData = dialogData,
            updateShowAddDialog = { newData ->
                dialogData = newData
            }
        )
    }
}

@Composable
fun NewMovementDialog(
    viewModel: MovementViewModel = viewModel(),
    updateShowAddDialog: (DialogData) -> Unit,
    dialogData: DialogData
) {
    Dialog(
        onDismissRequest = { updateShowAddDialog(dialogData.copy(showDialog = false)) }, // Dismiss when clicking outside
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            var exerciseName by remember { mutableStateOf("")}

            Column(modifier = Modifier.padding(16.dp)) {
                Row (
                    modifier = Modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = "Enter exercise name",
                        modifier = Modifier
                            .wrapContentSize(Alignment.Center),
                        textAlign = TextAlign.Start,
                    )
                }

                OutlinedTextField(
                    value = exerciseName,
                    onValueChange = { newText -> exerciseName = newText },
                    label = { Text("Exercise name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = {
                        updateShowAddDialog(dialogData.copy(showDialog = false))
                    }) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Button(onClick = {
                        updateShowAddDialog(dialogData.copy(showDialog = false))
                        viewModel.addMovement(exerciseName)
                    }) {
                        Text("Add")
                    }
                }
            }
    }
}
}

@Composable
fun MovementCard(viewModel: MovementViewModel = viewModel(), movement : Movement) {
    var dialogData by remember { mutableStateOf(DialogData(1, false)) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = movement.name, style = MaterialTheme.typography.titleLarge)
            //Text(text = "description")

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(onClick = {
                    dialogData = dialogData.copy(showDialog = true)
                }) {
                    Text("Edit")
                }
            }
        }
        if (dialogData.showDialog) {
            editMovementDialog(
                viewModel,
                dialogData = dialogData,
                movement,
                updateDialogData = { newData ->
                    dialogData = newData
                }
            )
        }
    }
}

@Composable
fun editMovementDialog(
    viewModel: MovementViewModel = viewModel(),
    dialogData: DialogData, movement: Movement,
    updateDialogData: (DialogData) -> Unit ) {

    Dialog(
        onDismissRequest = { updateDialogData(dialogData.copy(showDialog = false)) }, // Dismiss when clicking outside
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            when (dialogData.state) {
                1 -> EditMovementState(viewModel, dialogData, movement) { newData ->
                    updateDialogData(newData) // Pass the update back up
                }
                2 -> DeleteMovementState(viewModel, dialogData, movement) { newData ->
                    updateDialogData(newData) // Pass the update back up
                }
            }
        }
    }
}

@Composable
fun EditMovementState(
    viewModel: MovementViewModel = viewModel(),
    dialogData: DialogData, movement: Movement,
    updateDialogData: (DialogData) -> Unit) {
    var exerciseName by remember { mutableStateOf("")}

    Column(modifier = Modifier.padding(16.dp)) {
        Row (
            modifier = Modifier.padding(3.dp),
            horizontalArrangement = Arrangement.Center,
            ) {
            Text(
                text = "Edit exercise name",
                modifier = Modifier
                    .wrapContentSize(Alignment.Center),
                textAlign = TextAlign.Start,
            )
            Spacer(modifier = Modifier.weight(1f))

            Button(onClick = {
                updateDialogData(dialogData.copy(state = 2))
            },
                colors = ButtonDefaults.buttonColors(Color.Red, Color.White)) {
                Text("Delete")
            }
        }

        OutlinedTextField(
            value = exerciseName,
            onValueChange = { newText -> exerciseName = newText },
            label = { Text(movement.name) },
            modifier = Modifier.fillMaxWidth()
        )
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = {
                updateDialogData(dialogData.copy(showDialog = false))
            }) {
                Text("Cancel")
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = {
                updateDialogData(dialogData.copy(showDialog = false))
                viewModel.renameMovement(movement, exerciseName);
            }) {
                Text("Confirm")
            }
        }
    }
}

@Composable
fun DeleteMovementState(
    viewModel: MovementViewModel = viewModel(),
    dialogData : DialogData, movement: Movement,
    updateDialogData: (DialogData) -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Are you sure you want to delete this exercise? All saved data will be deleted.",
            modifier = Modifier
                .wrapContentSize(Alignment.Center),
            textAlign = TextAlign.Start,
        )
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = {
                updateDialogData(dialogData.copy(state = 1, showDialog = false))
            }) {
                Text("Cancel")
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = {
                updateDialogData(dialogData.copy(state = 1, showDialog = false))
                viewModel.removeMovement(movement);
            },
                colors = ButtonDefaults.buttonColors(Color.Red, Color.White)
            ) {
                Text("Confirm")
            }
        }
    }
}

data class DialogData(val state : Int, val showDialog : Boolean)