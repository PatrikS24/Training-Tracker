package com.example.trainingtracker.controller

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Embedded
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

    @Query("SELECT * FROM movements WHERE id = :id LIMIT 1")
    suspend fun getMovementById(id: Int?) : MovementDB

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
    ],
    indices = [
        Index(value = ["movementId"]),
        Index(value = ["workoutId"])
    ]
)
data class ExerciseDB(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val movementId: Int?,
    val workoutId: Int,
    val orderIndex: Int,
    val notes: String
)


@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercises")
    suspend fun getAllExercises(): List<ExerciseDB>

    @Insert
    suspend fun insert(exerciseDB: ExerciseDB): Long

    @Query("DELETE FROM exercises WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM exercises WHERE workoutId = :id")
    suspend fun getAllExercisesById(id: Int) : List<ExerciseDB>

    @Update
    suspend fun updateExercise(exerciseDB: ExerciseDB)

    @Query("UPDATE exercises SET orderIndex = :orderIndex WHERE id = :id")
    suspend fun updateOrderIndex(id: Int, orderIndex: Int)

    @Query("""
        SELECT e.*
        FROM exercises e
        INNER JOIN workouts w ON e.workoutId = w.id
        WHERE e.movementId = :movementId
        AND w.completed = 1
        ORDER BY w.startTime DESC
        LIMIT 1
    """)
    suspend fun getLatestCompletedExerciseForMovement(
        movementId: Int
    ): ExerciseDB?

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
    ],
    indices = [
        Index(value = ["exerciseId"])
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
    suspend fun getAllExerciseSets(): List<ExerciseSetDB>

    @Query("SELECT * FROM exercise_sets WHERE exerciseId = :id")
    suspend fun getAllExerciseSetsById(id: Int) : List<ExerciseSetDB>

    @Insert
    suspend fun insert(exerciseSetDB: ExerciseSetDB): Long

    @Query("DELETE FROM exercise_sets WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("UPDATE exercise_sets SET reps = :reps WHERE id = :id")
    suspend fun updateReps(id: Int, reps: Int)

    @Query("UPDATE exercise_sets SET weight = :weight WHERE id = :id")
    suspend fun updateWeight(id: Int, weight: Double)

    @Query("UPDATE exercise_sets SET completed = :completed WHERE id = :id")
    suspend fun updateCompleted(id: Int, completed: Boolean)

    @Query("UPDATE exercise_sets SET orderIndex = :orderIndex WHERE id = :id")
    suspend fun updateOrderIndex(id: Int, orderIndex: Int)

    @Update
    suspend fun updateSet(set: ExerciseSetDB)
}

data class HeaviestSetWithTime(
    @Embedded val set: ExerciseSetDB,
    val startTime: Date
)


@Dao
interface StatisticsDao {
    @Query("SELECT * FROM workouts WHERE completed = 1")
    suspend fun getAllCompletedWorkouts(): List<WorkoutDB>

    @Query("SELECT name FROM movements WHERE id = :movementId")
    suspend fun getMovementName(movementId: Int): String

    @Query("""
        SELECT es.*, w.startTime
        FROM exercise_sets es
        INNER JOIN exercises e
            ON es.exerciseId = e.id
        INNER JOIN workouts w
            ON e.workoutId = w.id
        INNER JOIN (
            SELECT exerciseId, MAX(weight) AS maxWeight
            FROM exercise_sets
            GROUP BY exerciseId
        ) max_sets
            ON es.exerciseId = max_sets.exerciseId
           AND es.weight = max_sets.maxWeight
        WHERE e.movementId = :movementId AND w.completed = 1
        ORDER BY w.startTime ASC
    """)
    suspend fun getHeaviestSetsForMovement(
        movementId: Int
    ): List<HeaviestSetWithTime>

}



@Database(
    entities = [MovementDB::class,
                WorkoutDB::class,
                ExerciseDB::class,
                ExerciseSetDB::class],
    version = 8,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TrainingDatabase : RoomDatabase() {
    abstract fun movementDao(): MovementDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun exerciseSetDao(): ExerciseSetDao
    abstract fun statisticsDao(): StatisticsDao
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