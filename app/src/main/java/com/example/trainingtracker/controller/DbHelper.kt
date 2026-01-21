package com.example.trainingtracker.controller

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Update
import java.util.Date

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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(movement: MovementDB)

    @Query("DELETE FROM movements WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("UPDATE OR IGNORE movements SET name = :name WHERE id = :id")
    suspend fun updateName(id: Int, name: String)

}

@Entity(
    tableName = "workouts"
)
data class WorkoutDB(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val startTime: Date = Date(),
    val durationMinutes: Int,
    val completed: Boolean
)

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workouts")
    suspend fun getAllWorkouts(): List<WorkoutDB>

    @Query("SELECT * FROM workouts WHERE completed = false")
    suspend fun getActive(): List<WorkoutDB>

    @Query("SELECT * FROM workouts WHERE id = :id")
    suspend fun getById(id: Int): WorkoutDB?

    @Insert
    suspend fun insert(workoutDB: WorkoutDB)

    @Query("DELETE FROM workouts WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Update
    suspend fun updateWorkout(workout: WorkoutDB)

    @Query("UPDATE workouts SET name = :name WHERE id = :id")
    suspend fun updateName(id: Int, name: String)

    @Query("DELETE FROM workouts")    // todo: REMOVE
    suspend fun deleteAll()
}

@Entity(
    tableName = "exercises",
    foreignKeys = [
        ForeignKey(
            entity = MovementDB::class,
            parentColumns = ["id"],
            childColumns = ["movementId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = WorkoutDB::class,
            parentColumns = ["id"],
            childColumns = ["workoutId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ExerciseDB(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val movementId: Int,
    val workoutId: Int,
    val orderIndex: Int,
    val notes: String,
    val completed: Boolean
)


@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercises")
    suspend fun getAllExercises(): List<ExerciseDB>

    @Insert
    suspend fun insert(exerciseDB: ExerciseDB)

    @Query("DELETE FROM exercises WHERE id = :id")
    suspend fun deleteById(id: Int)
}

@Entity(
    tableName = "exercise_sets",
    foreignKeys = [
        ForeignKey(
            entity = ExerciseDB::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ExerciseSetDB(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val exerciseId: Int,
    val orderIndex: Int,
    val weight: Double = 0.0,
    val reps: Int = 0,
    val isWarmup: Boolean = false,
    val completed: Boolean = false
)

@Dao
interface ExerciseSetDao {
    @Query("SELECT * FROM exercise_sets")
    suspend fun getAllWorkouts(): List<ExerciseSetDB>

    @Insert
    suspend fun insert(exerciseSetDB: ExerciseSetDB)

    @Query("DELETE FROM exercise_sets WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("UPDATE exercise_sets SET reps = :reps WHERE id = :id")
    suspend fun updateReps(id: Int, reps: Int)

    @Query("UPDATE exercise_sets SET weight = :weight WHERE id = :id")
    suspend fun updateWeight(id: Int, weight: Double)
}



@Database(
    entities = [MovementDB::class,
                WorkoutDB::class,
                ExerciseDB::class,
                ExerciseSetDB::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TrainingDatabase : RoomDatabase() {
    abstract fun movementDao(): MovementDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun exerciseSetDao(): ExerciseSetDao
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
            ).fallbackToDestructiveMigration().build().also { INSTANCE = it }  // todo: remove ".fallbackToDestructiveMigration()"
        }
    }
}


class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}