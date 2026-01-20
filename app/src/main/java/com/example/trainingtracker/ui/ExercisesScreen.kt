package com.example.trainingtracker.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun ExercisesScreen()
{
    // todo: get data from db
    LazyColumn {
        items(100) { index ->
            MovementCard()
        }
    }
}

@Composable
fun MovementCard() {
    var dialogData by remember { mutableStateOf(DialogData(1, false)) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "title", style = MaterialTheme.typography.titleLarge)
            Text(text = "description")

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
                dialogData = dialogData,
                updateDialogData = { newData ->
                    dialogData = newData
                }
            )
        }
    }
}

@Composable
fun editMovementDialog(dialogData: DialogData,
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
                1 -> EditMovementState(dialogData) { newData ->
                    updateDialogData(newData) // Pass the update back up
                }
                2 -> DeleteMovementState(dialogData) { newData ->
                    updateDialogData(newData) // Pass the update back up
                }
            }
        }
    }
}

@Composable
fun EditMovementState(dialogData: DialogData,
                      updateDialogData: (DialogData) -> Unit) {
    var exerciseName by remember { mutableStateOf("")}

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Edit exercise name",
            modifier = Modifier
                .wrapContentSize(Alignment.Center),
            textAlign = TextAlign.Start,
        )
        OutlinedTextField(
            value = exerciseName,
            onValueChange = { newText -> exerciseName = newText },
            label = { Text("") },
            placeholder = { Text("John Doe") }, // Todo: placeholder old name
            modifier = Modifier.fillMaxWidth()
        )
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = {
                updateDialogData(dialogData.copy(state = 2))
            }) {
                Text("Delete")
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = {
                updateDialogData(dialogData.copy(showDialog = false))
                // Todo: update database with new name
            }) {
                Text("Confirm")
            }
        }
    }
}

@Composable
fun DeleteMovementState(dialogData : DialogData,
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

                // Todo: delete from here & database
            }) {
                Text("Confirm")
            }
        }
    }
}

data class DialogData(val state : Int, val showDialog : Boolean)