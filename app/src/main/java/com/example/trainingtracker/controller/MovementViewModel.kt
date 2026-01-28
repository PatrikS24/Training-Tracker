package com.example.trainingtracker.controller

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.trainingtracker.model.Movement
import kotlinx.coroutines.launch

class MovementViewModel(application: Application) :
    AndroidViewModel(application) {

    var movements = mutableStateListOf<Movement>();

    private val dao = DatabaseProvider
        .getDatabase(application)
        .movementDao()

    init {
        viewModelScope.launch {
            refreshMovements()
        }
    }

    fun renameMovement(movement : Movement, newName : String) {
        viewModelScope.launch {
            dao.updateName(movement.id, newName)
            refreshMovements()
        }
    }

    fun refreshMovements() {

        viewModelScope.launch {
            val dbMovements: List<MovementDB> = dao.getAllMovements()

            movements.clear()
            movements.addAll(
                dbMovements.map { db ->
                    Movement(
                        id = db.id,
                        name = db.name
                    )
                }
            )
            movements.sortBy { it.name.lowercase() }
        }

    }

    fun createMovement(name: String) {
        viewModelScope.launch {
            val movement = MovementDB(name = name)
            dao.insert(movement)
            refreshMovements()
        }
    }

    fun deleteMovement(movement: Movement) {
        viewModelScope.launch {
            dao.deleteById(movement.id)
            movements.remove(movement)
        }
    }
}