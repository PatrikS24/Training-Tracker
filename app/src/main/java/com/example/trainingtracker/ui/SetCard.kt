package com.example.trainingtracker.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trainingtracker.controller.WorkoutViewModel
import com.example.trainingtracker.model.Exercise
import com.example.trainingtracker.model.ExerciseSet
import kotlin.text.toDouble
import kotlin.text.toInt

@SuppressLint("MutableCollectionMutableState")
@Composable
fun SetCard( viewModel: WorkoutViewModel = viewModel(), set: ExerciseSet, exercise: Exercise ) {

    var weight by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }
    var completed by remember { mutableStateOf(set.completed) }

    Card(
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

            Text(set.orderIndex.toString())

            viewModel.getPreviousSetsForExercise(exercise)

            val previousSet = exercise.previousSets
                .getOrNull(set.orderIndex - 1)

            val previousReps = previousSet?.reps?.toString() ?: "-"
            val previousWeight = previousSet?.weight?.toString() ?: "-"

            Text("$previousWeight kg x $previousReps")



            CompactNumericInput(
                value = weight,
                onValueChange = {weight = it},
                placeholder = set.weight.toString(),
                isDecimal = true,
                onDone = {
                    try {
                        viewModel.updateSetReps(set, reps.toInt())
                    } catch (e: NumberFormatException) { }
                    try {
                        viewModel.updateSetWeight(set, weight.toDouble())
                    } catch (e: NumberFormatException) { }
                }
            )

            CompactNumericInput(
                value = reps,
                onValueChange = {reps = it},
                placeholder = set.reps.toString(),
                isDecimal = false,
                onDone = {
                    try {
                        viewModel.updateSetReps(set, reps.toInt())
                    } catch (e: NumberFormatException) { }
                    try {
                        viewModel.updateSetWeight(set, weight.toDouble())
                    } catch (e: NumberFormatException) { }
                }
            )

            Checkbox(
                checked = completed,
                onCheckedChange = {
                    completed = it
                    viewModel.updateSetCompleted(set, completed)
                    try {
                        viewModel.updateSetReps(set, reps.toInt())
                    } catch (e: NumberFormatException) { }
                    try {
                        viewModel.updateSetWeight(set, weight.toDouble())
                    } catch (e: NumberFormatException) { }
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
    onDone: () -> Unit
) {
    // Regex logic:
    // Natural: Digits only
    // Decimal: Digits and one optional dot

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current


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
            .background(Color.DarkGray, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp),
        textStyle = TextStyle(fontSize = 14.sp, color = Color.White, textAlign = TextAlign.End),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isDecimal) KeyboardType.Decimal else KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                onDone()
                keyboardController?.hide()
                focusManager.clearFocus()
            },
        ),
        decorationBox = { innerTextField ->
            Box(contentAlignment = Alignment.CenterEnd) {
                if (value.isEmpty()) {
                    Text(placeholder, color = Color.White, textAlign = TextAlign.End, fontSize = 14.sp, maxLines = 1,
                        overflow = TextOverflow.Ellipsis)
                }
                innerTextField()
            }
        }
    )
}