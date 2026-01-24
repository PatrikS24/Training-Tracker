package com.example.trainingtracker.ui

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trainingtracker.controller.WorkoutScreenState
import com.example.trainingtracker.controller.WorkoutViewModel
import com.example.trainingtracker.model.Exercise
import com.example.trainingtracker.model.Movement
import kotlinx.coroutines.delay
import java.util.Date

@Composable
fun WorkoutScreen( viewModel: WorkoutViewModel = viewModel() )
{
    LaunchedEffect(Unit) {
        viewModel.getActiveWorkout()
    }

    var initialSearchQuery by remember { mutableStateOf<Exercise?>(null) }

    when (viewModel.state) {
        WorkoutScreenState.inactive -> NoActiveWorkout(viewModel)
        WorkoutScreenState.active -> {
            ActiveWorkout(viewModel, onSearchTriggered = { dataFromHome ->
                initialSearchQuery = dataFromHome})
        }
        WorkoutScreenState.search -> SearchScreen(viewModel, initialSearchQuery,
            onDismiss = {
                viewModel.setScreenState( WorkoutScreenState.active )
        })
    }
}

@Composable
fun NoActiveWorkout(viewModel: WorkoutViewModel = viewModel()) {
    // todo: add workout history
    Column (modifier = Modifier.fillMaxSize().padding(16.dp)){

        Spacer(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.4f))

        Button(onClick = {
            viewModel.createWorkout()
        },
            modifier = Modifier.fillMaxWidth())
        {
            Text("Start new workout")
        }

        Spacer(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.1f))

        var showDeleteWorkoutTableDialog by remember { mutableStateOf(false) }
        Button(
            onClick = {
                showDeleteWorkoutTableDialog = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Delete workout table")
        }
        if (showDeleteWorkoutTableDialog) {
            AreYouSureDialog("Are you sure you want to delete all workout tables?") {
                    isSure ->
                showDeleteWorkoutTableDialog = false
                if (isSure) {viewModel.deleteAllWorkouts()}

            }
        }
    }
}

@Composable
fun ActiveWorkout(viewModel: WorkoutViewModel = viewModel(), onSearchTriggered: (Exercise?) -> Unit) {
    Column (
        modifier = Modifier.fillMaxSize().padding(0.dp),
        horizontalAlignment = Alignment.CenterHorizontally){

        // Top bar
        Row (
            modifier = Modifier.fillMaxWidth().padding(5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            ClickToEditText(viewModel.activeWorkout.name)

            WorkoutDurationDisplay(viewModel.activeWorkout.startTime)

            var showFinishWorkoutDialog by remember { mutableStateOf(false) }

            TextButton(onClick = {
                showFinishWorkoutDialog = true
            }) {
                Text("Finish")
            }

            if (showFinishWorkoutDialog) {
                AreYouSureDialog("Are you sure you want to finish this workout?") {
                    isSure ->
                    if (isSure) {
                        viewModel.finishWorkout()
                    }
                    showFinishWorkoutDialog = false
                }
            }
        }

        LazyColumn (modifier = Modifier.weight(1f)){
            items(viewModel.activeWorkout.exercises) { exercise ->

                ExerciseCard(viewModel, exercise, onSearchTriggered = {
                    dataFromHome ->
                    onSearchTriggered(dataFromHome)
                })
            }
            item {
                Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(onClick = {
                        viewModel.createExercise()
                    }) {
                        Text("Add Exercise")
                    }
                    var showCancelWorkoutDialog by remember { mutableStateOf(false) }

                    TextButton(onClick = {
                        showCancelWorkoutDialog = true
                    }) {
                        Text("Cancel Workout")
                    }

                    if (showCancelWorkoutDialog) {
                        AreYouSureDialog("Are you sure you want to cancel this workout?") {
                                isSure ->
                            showCancelWorkoutDialog = false
                            if (isSure) {
                                viewModel.deleteActiveWorkout()
                            }

                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WorkoutDurationDisplay(startTime: Date) {
    // State to hold the formatted string
    var currentTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    // Update the currentTime every second
    LaunchedEffect(key1 = startTime) {
        while (true) {
            currentTime = System.currentTimeMillis()
            delay(1000L)
        }
    }

    // Calculate the difference
    val elapsedMillis = currentTime - startTime.time
    val seconds = (elapsedMillis / 1000) % 60
    val minutes = (elapsedMillis / (1000 * 60)) % 60
    val hours = (elapsedMillis / (1000 * 60 * 60))

    val timeString = "%02d:%02d:%02d".format(hours, minutes, seconds)

    Text(
        text = timeString
    )
}

@Composable
fun ClickToEditText(initialText: String? = "", viewModel: WorkoutViewModel = viewModel()) {
    var text by remember { mutableStateOf(initialText) }
    var isFocused by remember { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val style = LocalTextStyle.current.copy(
        color = LocalContentColor.current
    )

    text?.let { it1 ->
        BasicTextField(
            value = it1,
            onValueChange = { text = it },
            modifier = Modifier.onFocusChanged { isFocused = it.isFocused },
            textStyle = style,
            decorationBox = { inner ->
                if (isFocused) {
                    inner()
                } else {
                    text?.let { Text(it, style = style) }
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions( onDone = {
                // Handle Enter key here
                if (text != null) {
                    viewModel.activeWorkout.name = it1
                }
                keyboardController?.hide()
                focusManager.clearFocus()
                viewModel.updateDatabase()
            }
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(viewModel: WorkoutViewModel = viewModel(), exercise: Exercise?, onDismiss: () -> Unit) {
    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(true) }

    BackHandler(enabled = true) {
        // Choose your logic here:
        onDismiss()
    }

    viewModel.getAllMovements()

    SearchBar(
        query = query,
        onQueryChange = { query = it },
        onSearch = { active = false },
        active = active,
        onActiveChange = { active = it }
    ) {
        // Everything inside these braces appears ONLY when 'active' is true
        val filteredResults = viewModel.movements.filter { it.name.contains(query, ignoreCase = true) }

        if (filteredResults.isEmpty() && query.isNotEmpty()) {
            Text("No results found for '$query'", modifier = Modifier.padding(16.dp))
        } else {
            LazyColumn {
                items(filteredResults) { result ->
                    // What each search result looks like
                    Text(
                        text = result.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clickable {
                                /* Handle selection */
                                exercise?.movement = Movement(result.id, result.name)
                                viewModel.updateExercise(exercise)
                                exercise?.onMovementChosen(viewModel)
                                active = false
                                viewModel.setScreenState( WorkoutScreenState.active )
                            }
                    )
                }
            }
        }
    }
}

@Composable
fun AreYouSureDialog(question: String, onIsSure: (Boolean) -> Unit) {
    Dialog(
        onDismissRequest = { onIsSure(false) }, // Dismiss when clicking outside
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
            Column (
                modifier = Modifier.padding(16.dp).fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
                ){

                Text(question, modifier = Modifier.weight(2.5f))

                Row (modifier = Modifier.weight(1f)){
                    Button(onClick = {
                        onIsSure(false)
                    }) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.size(40.dp))
                    Button(onClick = {
                        onIsSure(true)
                    }) {
                        Text("Yes")
                    }
                }
            }
        }
    }
}