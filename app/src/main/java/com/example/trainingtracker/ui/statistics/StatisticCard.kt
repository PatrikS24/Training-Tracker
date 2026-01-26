package com.example.trainingtracker.ui.statistics

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StatisticCard(title: String, modifier: Modifier, content: @Composable ColumnScope.() -> Unit) {
    ElevatedCard {
        Column(
            modifier = Modifier.padding(5.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title)
            Column(modifier = modifier, content = content)
        }
    }
}