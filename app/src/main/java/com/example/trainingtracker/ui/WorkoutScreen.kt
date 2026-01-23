package com.example.trainingtracker.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trainingtracker.controller.WorkoutScreenState
import com.example.trainingtracker.controller.WorkoutViewModel
import com.example.trainingtracker.model.Exercise
import com.example.trainingtracker.model.Movement

@Composable
fun WorkoutScreen( viewModel: WorkoutViewModel = viewModel() )
{
    LaunchedEffect(Unit) {
        viewModel.getActiveWorkout()
    }

    var initialSearchQuery by remember { mutableStateOf<Exercise?>(null) }

    when (viewModel.state) {
        WorkoutScreenState.inactive -> NoActiveWorkout(viewModel)
        WorkoutScreenState.active -> ActiveWorkout(viewModel, onSearchTriggered = { dataFromHome ->
            initialSearchQuery = dataFromHome})
        WorkoutScreenState.search -> FullScreenSearchBar(viewModel, initialSearchQuery,
            onDismiss = {
                viewModel.state = WorkoutScreenState.active
                
        })
    }
}

@Composable
fun NoActiveWorkout(viewModel: WorkoutViewModel = viewModel()) {
    // todo: add workout history
    Column (modifier = Modifier.fillMaxSize().padding(16.dp)){
        Spacer(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.5f))
        Button(onClick = {
            viewModel.createWorkout()
        },
            modifier = Modifier.fillMaxWidth())
        {
            Text("Start new workout")
        }
        Spacer(modifier = Modifier.fillMaxWidth().fillMaxHeight())
    }
}

@Composable
fun ActiveWorkout(viewModel: WorkoutViewModel = viewModel(), onSearchTriggered: (Exercise?) -> Unit) {
    Column (
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally){

        ClickToEditText(viewModel.activeWorkout.name)

        LazyColumn (modifier = Modifier.weight(1f)){
            items(viewModel.activeWorkout.exercises) { exercise ->
                ExerciseCard(viewModel, exercise, onSearchTriggered = { dataFromHome ->
                    onSearchTriggered(dataFromHome)})
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
                    Button(onClick = {
                        // todo: cancel workout
                    }) {
                        Text("Cancel Workout")
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { viewModel.deleteAllWorkouts() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Delete workout table")
        }
    }
}

@Composable
fun ExerciseCard( viewModel: WorkoutViewModel = viewModel(), exercise: Exercise, onSearchTriggered: (Exercise?) -> Unit ) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.End) {
            exercise.movement?.name?.let { Text(text = it, style = MaterialTheme.typography.titleLarge) }

            if (exercise.movement == null) {
                Button(onClick = {
                    viewModel.state = WorkoutScreenState.search
                    onSearchTriggered(exercise)
                }) {
                    Text("Choose movement")
                }
            }

        }
    }
}
@Composable
fun ClickToEditText(initialText: String? = "", viewModel: WorkoutViewModel = viewModel()) {
    var text by remember { mutableStateOf(initialText) }
    var isFocused by remember { mutableStateOf(false) }

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
                viewModel.updateDatabase()
            }
            )
        )
    }
}

@Composable
fun SearchScreen( viewModel: WorkoutViewModel = viewModel() ) {

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenSearchBar(viewModel: WorkoutViewModel = viewModel(), exercise: Exercise?, onDismiss: () -> Unit) {
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
                                active = false
                                viewModel.state = WorkoutScreenState.active
                            }
                    )
                }
            }
        }
    }
}