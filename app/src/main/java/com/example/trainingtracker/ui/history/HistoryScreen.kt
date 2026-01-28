package com.example.trainingtracker.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trainingtracker.controller.HistoryViewModel
import com.example.trainingtracker.model.Exercise
import com.example.trainingtracker.model.ExerciseSet
import com.example.trainingtracker.model.Workout
import com.example.trainingtracker.ui.activeWorkout.AreYouSureDialog
import java.time.ZoneId
import java.time.temporal.ChronoField
import java.time.temporal.TemporalField
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Instant.ofEpochMilli
import java.time.temporal.TemporalAdjusters
import java.util.Date
import kotlin.time.toKotlinInstant


@Composable
fun HistoryScreen(viewModel: HistoryViewModel = viewModel(), onEdit: (Int) -> Unit) {

    LaunchedEffect(Unit) {
        viewModel.loadWorkouts()
    }

    if (!viewModel.hasData()) {
        Text("No data")
    } else {
        Column(
            modifier = Modifier.fillMaxSize().padding(5.dp)
        ) {
            LazyColumn(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                items(viewModel.workouts.toList() ) { workout ->
                    WorkoutCard(
                        workout,
                        onEdit = {
                        onEdit(it.id)
                        },
                        onDelete = {
                            viewModel.deleteWorkout(workout)
                        })
                }
            }
        }
    }
}

@Composable
fun WorkoutCard(workout: Workout, onEdit: (Workout) -> Unit, onDelete: () -> Unit) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            var showDeleteWorkoutDialog by remember { mutableStateOf(false) }


            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(workout.name, modifier = Modifier.widthIn(min = 100.dp, max = 150.dp))

                    WorkoutEditMenu(
                        onEdit = {
                            onEdit(workout)
                        },
                        onDelete = {
                            showDeleteWorkoutDialog = true
                        }
                    )
                }
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text("${workout.durationMinutes / 60}h ${workout.durationMinutes % 60}min")
                    val date = workout.startTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                    val dateString = "${date.dayOfMonth}." +
                                     "${date.month.value}." +
                                     "${date.year}"
                    Text(dateString)
                }
            }

            if (showDeleteWorkoutDialog) {
                AreYouSureDialog("Are you sure you want to delete this workout?") {
                        sure ->
                    if (sure) {onDelete()}
                    showDeleteWorkoutDialog = false
                }
            }

            for (set in workout.exercises) {
                ExerciseTextCard(set)
            }
        }
    }
}

@Composable
fun ExerciseTextCard(exercise: Exercise) {

    if (exercise.sets.size == 0) return

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth().padding(3.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(5.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.Start) {
            if (exercise.movement == null) { // No movement chosen yet
                Text("No movement chosen")
            } else { // Movement chosen
                /*
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ){
                    ExerciseCardOptionsMenu( viewModel, exercise, onSearchTriggered = {
                        onSearchTriggered(exercise)
                    })
                    Text(exercise.movement!!.name)
                }
                 */

                Column (
                    modifier = Modifier.fillMaxWidth().padding(5.dp)
                ){
                    Text("${exercise.sets.size} x ${exercise.movement!!.name}")
                    val bestSet = exercise.sets.maxByOrNull { it.weight }
                    if (bestSet != null) {
                        Text("Best set: ${bestSet.weight} kg x ${bestSet.reps}")
                    }
                }

            }

        }
    }

}

@Composable
fun SetTextCard(set: ExerciseSet) {

    ElevatedCard(
        modifier = Modifier
            .fillMaxSize()
            .height(40.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(5.dp)
    ) {
        Row (
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){

            Text(set.orderIndex.toString(),modifier = Modifier
                .width(30.dp))

            Text(set.weight.toString(),modifier = Modifier
                .width(60.dp))

            Text(set.reps.toString(),modifier = Modifier
                .width(30.dp))

        }
    }
}

@Composable
fun WorkoutEditMenu( onEdit: () -> Unit, onDelete: () -> Unit) {
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
                text = { Text("Edit workout") },
                onClick = {
                    expanded = false
                    onEdit()
                }
            )
            DropdownMenuItem(
                text = { Text("Delete workout") },
                onClick = {
                    expanded = false
                    onDelete()
                }
            )
        }
    }
}