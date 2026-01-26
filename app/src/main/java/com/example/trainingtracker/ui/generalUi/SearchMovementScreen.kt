package com.example.trainingtracker.ui.generalUi

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
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
import com.example.trainingtracker.controller.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchMovementsScreen(viewModel: SearchViewModel = viewModel(), onDismiss: () -> Unit, onMovementChosen: (Int) -> Unit) {
    var text by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        viewModel.loadMovements()
    }



    SearchBar(
        modifier = Modifier
            .padding(top = 8.dp),
        inputField = {
            SearchBarDefaults.InputField(
                query = text,
                onQueryChange = { text = it },
                onSearch = { expanded = false },
                expanded = expanded,
                onExpandedChange = {
                    expanded = it
                },
                placeholder = { Text("Search movements...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (expanded && text.isNotEmpty()) {
                        IconButton(onClick = { text = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = null)
                        }
                    }
                },
            )
        },
        expanded = expanded,
        onExpandedChange = {
            expanded = it
            if (!it) {
                onDismiss()
            }},
    ) {
        // Everything inside these braces appears ONLY when 'active' is true
        val filteredResults = viewModel.movements.filter { it.name.contains(text, ignoreCase = true) }

        if (filteredResults.isEmpty() && text.isNotEmpty()) {
            Text("No results found for '$text'", modifier = Modifier.padding(16.dp))
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
                                expanded = false
                                onMovementChosen(result.id)
                            }
                    )
                }
            }
        }
    }
}