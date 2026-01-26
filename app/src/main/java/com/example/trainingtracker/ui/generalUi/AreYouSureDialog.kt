package com.example.trainingtracker.ui.generalUi

import androidx.compose.foundation.layout.Column
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
            shape = MaterialTheme.shapes.large
        ) {
            Column (
                modifier = Modifier.padding(16.dp).fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ){

                Text(question, modifier = Modifier.weight(2.5f))

                Row (modifier = Modifier.weight(1f)){
                    Button(onClick = {
                        onIsSure(false)
                    },
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.size(40.dp))
                    Button(onClick = {
                        onIsSure(true)
                    },
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text("Yes")
                    }
                }
            }
        }
    }
}