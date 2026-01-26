package com.example.trainingtracker.controller

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


public class SearchViewModel(application: Application) : AndroidViewModel(application){

    private val movementDao = DatabaseProvider
        .getDatabase(application)
        .movementDao()

    var movements = mutableStateListOf<MovementDB>()


    fun loadMovements() {
        viewModelScope.launch {
            movements.clear()
            movements.addAll(movementDao.getAllMovements().sortedBy{ it.name }.toMutableList())
        }
    }
}
