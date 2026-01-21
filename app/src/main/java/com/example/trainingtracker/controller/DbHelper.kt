package com.example.trainingtracker.controller

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(
    tableName = "movements",
    indices = [Index(value = ["name"], unique = true)]
)
data class MovementDB(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String
)

@Dao
interface MovementDao {

    @Query("SELECT * FROM movements")
    suspend fun getAllMovements(): List<MovementDB>

    @Insert
    suspend fun insert(movement: MovementDB)

    @Query("DELETE FROM movements WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("UPDATE movements SET name = :name WHERE id = :id")
    suspend fun updateName(id: Int, name: String)

}

@Database(
    entities = [MovementDB::class],
    version = 1
)
abstract class TrainingDatabase : RoomDatabase() {
    abstract fun movementDao(): MovementDao
}


object DatabaseProvider {

    @Volatile
    private var INSTANCE: TrainingDatabase? = null

    fun getDatabase(context: Context): TrainingDatabase {
        return INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                TrainingDatabase::class.java,
                "training_database"
            ).build().also { INSTANCE = it }
        }
    }
}
