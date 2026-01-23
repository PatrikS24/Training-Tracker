package com.example.trainingtracker.ui

import android.graphics.Paint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trainingtracker.controller.WorkoutScreenState
import com.example.trainingtracker.controller.WorkoutViewModel
import com.example.trainingtracker.model.Exercise

@Composable
fun WorkoutScreen( viewModel: WorkoutViewModel = viewModel() )
{
    LaunchedEffect(Unit) {
        viewModel.getActiveWorkout()
    }

    when (viewModel.state) {
        WorkoutScreenState.inactive -> NoActiveWorkout(viewModel)
        WorkoutScreenState.active -> ActiveWorkout(viewModel)
        WorkoutScreenState.search -> FullScreenSearchBar(viewModel)
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
fun ActiveWorkout(viewModel: WorkoutViewModel = viewModel()) {
    Column (
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally){

        ClickToEditText(viewModel.activeWorkout.name)

        LazyColumn (modifier = Modifier.weight(1f)){
            items(viewModel.activeWorkout.exercises) { exercise ->
                ExerciseCard(viewModel, exercise)
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
fun ExerciseCard( viewModel: WorkoutViewModel = viewModel(), exercise: Exercise ) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.End) {
            exercise.movement?.name?.let { Text(text = it, style = MaterialTheme.typography.titleLarge) }

            Button(onClick = {
                viewModel.state = WorkoutScreenState.search
            }) {
                Text("Add movement")
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
fun FullScreenSearchBar( viewModel: WorkoutViewModel = viewModel() ) {
    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
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

                                active = false
                                viewModel.state = WorkoutScreenState.active
                            }
                    )
                }
            }
        }
    }
}