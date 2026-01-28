package com.example.trainingtracker.ui.activeWorkout

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trainingtracker.controller.WorkoutViewModel
import com.example.trainingtracker.model.Exercise
import com.example.trainingtracker.model.ExerciseSet
import kotlin.text.toDouble
import kotlin.text.toInt

@SuppressLint("MutableCollectionMutableState")
@Composable
fun SetCard( viewModel: WorkoutViewModel, set: ExerciseSet, exercise: Exercise ) {


    val previousSet = exercise.previousSets
        .getOrNull(set.orderIndex - 1)

    val previousReps = previousSet?.reps?.toString() ?: "-"
    val previousWeight = previousSet?.weight?.toString() ?: "-"

    var weight by remember { mutableStateOf("") }

    if (set.weight != 0.0) {
        weight = set.weight.toString()
    } else if (previousWeight == "-") {
        weight = ""
    } else {
        weight = previousWeight
    }

    var reps by remember { mutableStateOf(if (set.reps != 0) set.reps.toString() else "") }
    var completed by remember { mutableStateOf(set.completed) }

    ElevatedCard(
        modifier = Modifier
            .fillMaxSize(),
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


            Text("$previousWeight kg x $previousReps", modifier = Modifier.width(90.dp))



            CompactNumericInput(
                value = weight,
                onValueChange = {weight = it},
                placeholder = set.weight.toString(),
                isDecimal = true,
                onKeyboardHidden = {
                    updateWeightAndReps(viewModel, set, reps, weight)
                }
            )

            CompactNumericInput(
                value = reps,
                onValueChange = {reps = it},
                placeholder = set.reps.toString(),
                isDecimal = false,
                onKeyboardHidden = {
                    updateWeightAndReps(viewModel, set, reps, weight)
                }
            )

            Checkbox(
                modifier = Modifier
                    .padding(0.dp)
                    .size(16.dp),
                checked = completed,
                onCheckedChange = {
                    completed = it
                    set.completed = completed
                    viewModel.updateSetCompleted(set, completed)
                    updateWeightAndReps(viewModel, set, reps, weight)
                }
            )
            }

    }
}

@Composable
fun CompactNumericInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isDecimal: Boolean = false, // Toggle between Natural and Decimal
    onKeyboardHidden: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    var isFocused by remember { mutableStateOf(false) }
    val imeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    LaunchedEffect(imeVisible, isFocused) {
        if (!imeVisible && isFocused) { onKeyboardHidden() }
    }

    val keyboardController = LocalSoftwareKeyboardController.current


    val pattern = remember(isDecimal) {
        if (isDecimal) Regex("^\\d*\\.?\\d*$") else Regex("^\\d*$")
    }

    BasicTextField(
        value = value,
        onValueChange = { input ->
            val sanitizedInput = input.replace(',', '.')

            if (sanitizedInput.isEmpty() || sanitizedInput.matches(pattern)) {
                onValueChange(sanitizedInput)
            }
        },
        modifier = Modifier
            .width(60.dp)
            .height(30.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp)
            .onFocusChanged {
                isFocused = it.isFocused
                onKeyboardHidden()
            },
        textStyle = TextStyle(fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.End),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isDecimal) KeyboardType.Decimal else KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        decorationBox = { innerTextField ->
            Box(contentAlignment = Alignment.CenterEnd) {
                if (value.isEmpty()) {
                    Text(placeholder, color = MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.End, fontSize = 14.sp, maxLines = 1,
                        overflow = TextOverflow.Ellipsis)
                }
                innerTextField()
            }
        }
    )
}


fun updateWeightAndReps( viewModel: WorkoutViewModel, set: ExerciseSet, reps: String, weight: String) {
    try {
        viewModel.updateSetReps(set, reps.toInt())
        set.reps = reps.toInt()
    } catch (e: NumberFormatException) { }
    try {
        viewModel.updateSetWeight(set, weight.toDouble())
        set.weight = weight.toDouble()
    } catch (e: NumberFormatException) { }
}