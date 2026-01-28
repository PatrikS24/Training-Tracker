package com.example.trainingtracker.ui.generalUi

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlin.Unit

@Composable
fun ConfirmDialog(
    text: String,
    negativeAnswer: String = "Cancel",
    positiveAnswer: String = "Confirm",
    onIsSure: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {

    Dialog(
        onDismissRequest = { onIsSure(false) }, // Dismiss when clicking outside
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(16.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column (
                modifier = Modifier.padding(16.dp).fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ){

                Text(text)

                Column (content = content)

                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Button(onClick = {
                        onIsSure(false)
                    },
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(negativeAnswer)
                    }

                    Button(onClick = {
                        onIsSure(true)
                    },
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(positiveAnswer)
                    }
                }
            }
        }
    }
}