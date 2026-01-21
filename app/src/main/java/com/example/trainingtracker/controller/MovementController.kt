package com.example.trainingtracker.controller

import androidx.compose.runtime.mutableStateListOf
import com.example.trainingtracker.model.Movement

object MovementController {
    var movements = mutableStateListOf<Movement>();

    init {
        // todo: retreive data from db
        // Debug
        for (i in 1..2) {
            movements.add(Movement(i, i.toString()))
        }
    }

    fun renameMovement(movement : Movement, newName : String) {
        movement.name = newName;

    }

    fun refreshMovements() {
        // todo: get a recent list of movements from db
    }

    fun addMovement(name: String) : Int { // returns id
        // Todo: add new movement to db
        movements.add(Movement(id = null, name = name))
        return 0;
    }

    fun removeMovement(movement: Movement) {
        movements.remove(movement)
        // todo: remove from db
    }
}