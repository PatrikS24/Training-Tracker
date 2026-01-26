package com.example.trainingtracker.ui.generalUi

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trainingtracker.controller.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchMovementsScreen(viewModel: SearchViewModel = viewModel(), onDismiss: () -> Unit, onMovementChosen: (Int) -> Unit) {
    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        viewModel.loadMovements()
    }

    BackHandler(enabled = true) {
        // Choose your logic here:
        onDismiss()
    }


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
                                active = false
                                onMovementChosen(result.id)
                            }
                    )
                }
            }
        }
    }
}