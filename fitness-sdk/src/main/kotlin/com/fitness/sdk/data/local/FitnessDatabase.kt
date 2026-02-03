package com.fitness.sdk.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fitness.sdk.data.local.dao.ExerciseDao
import com.fitness.sdk.data.local.dao.WorkoutDao
import com.fitness.sdk.data.local.entity.ExerciseEntity
import com.fitness.sdk.data.local.entity.WorkoutEntity

/**
 * Room database for the Fitness SDK.
 */
@Database(
    entities = [
        WorkoutEntity::class,
        ExerciseEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class FitnessDatabase : RoomDatabase() {

    abstract fun workoutDao(): WorkoutDao
    abstract fun exerciseDao(): ExerciseDao

    companion object {
        private const val DEFAULT_DATABASE_NAME = "fitness_sdk_database"

        @Volatile
        private var INSTANCE: FitnessDatabase? = null

        fun getInstance(
            context: Context,
            databaseName: String = DEFAULT_DATABASE_NAME
        ): FitnessDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context, databaseName).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context, databaseName: String): FitnessDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                FitnessDatabase::class.java,
                databaseName
            )
                .fallbackToDestructiveMigration()
                .build()
        }

        /**
         * Create an in-memory database for testing.
         */
        fun createInMemoryDatabase(context: Context): FitnessDatabase {
            return Room.inMemoryDatabaseBuilder(
                context.applicationContext,
                FitnessDatabase::class.java
            ).build()
        }

        /**
         * Close and clear the database instance.
         * Useful for testing.
         */
        fun closeDatabase() {
            INSTANCE?.close()
            INSTANCE = null
        }
    }
}
