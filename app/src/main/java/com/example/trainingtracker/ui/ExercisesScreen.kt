package com.example.trainingtracker.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trainingtracker.controller.MovementViewModel
import com.example.trainingtracker.model.Movement
import com.example.trainingtracker.ui.generalUi.ConfirmDialog

@Composable
fun ExercisesScreen( viewModel: MovementViewModel = viewModel() )
{
    Box (modifier = Modifier.fillMaxSize()){
        LazyColumn {
            items(viewModel.movements) { movement ->
                MovementCard(
                    movement = movement,
                    onDelete = { viewModel.deleteMovement(movement) },
                    onChangeName = {
                        newName ->
                        viewModel.renameMovement(movement, newName)
                    }
                    )
            }
        }
        NewMovementButton(
            modifier = Modifier.align(Alignment.BottomStart),
            onCreateMovement = {
                exerciseName ->
                viewModel.createMovement(exerciseName)
            })
    }


}

@Composable
fun NewMovementButton(modifier: Modifier = Modifier, onCreateMovement: (String) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    var exerciseName by remember { mutableStateOf("")}

    FloatingActionButton(
        modifier = modifier.padding(16.dp),
        onClick = {
            showDialog = true
        },
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.secondary
    ) {
        Text("+", fontSize = 40.sp)
    }
    if (showDialog) {
        ConfirmDialog(
            text = "Enter exercise name",
            positiveAnswer = "Create",
            onIsSure = {
                isSure ->
                if (isSure) { onCreateMovement(exerciseName) }
                showDialog = false
                },
        ) {
            OutlinedTextField(
                value = exerciseName,
                onValueChange = { newText -> exerciseName = newText },
                label = { Text("Exercise name") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun MovementCard(movement : Movement, onDelete: () -> Unit, onChangeName: (String) -> Unit) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showChangeNameDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf(movement.name)}

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = movement.name,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.width(300.dp))

            MovementCardOptionsMenu(
                onDeleteClicked = {
                    showDeleteDialog = true
                },
                onChangeNameClicked = {
                    showChangeNameDialog = true
                }
            )
        }

        if (showDeleteDialog) {
            ConfirmDialog(
                text = "Are you sure you want to delete ${movement.name}? All saved data related to this exercise will be deleted.",
                positiveAnswer = "Delete",
                onIsSure = {
                    isSure ->
                    if (isSure) {onDelete()}
                    showDeleteDialog = false
                }) {

            }
        }

        if (showChangeNameDialog) {
            ConfirmDialog(
                text = "Change name",
                onIsSure = {
                        isSure ->
                    if (isSure) { onChangeName(newName) }
                    showChangeNameDialog = false
                }) {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newText -> newName = newText },
                    label = { Text("Exercise name") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun MovementCardOptionsMenu(onDeleteClicked: () -> Unit, onChangeNameClicked: () -> Unit ) {
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
                text = { Text("Change name") },
                onClick = {
                    expanded = false
                    onChangeNameClicked()
                }
            )
            DropdownMenuItem(
                text = { Text("Delete movement") },
                onClick = {
                    expanded = false
                    onDeleteClicked()
                }
            )
        }
    }
}