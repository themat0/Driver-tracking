package com.example.drivertracking.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.drivertracking.model.dao.EulerDao
import com.example.drivertracking.model.dao.EyeOpennessDao
import com.example.drivertracking.model.dao.StatsDao
import com.example.drivertracking.model.entities.EulerRecord
import com.example.drivertracking.model.entities.EyeOpennessRecord
import com.example.drivertracking.model.entities.StatsRecord

@Database(entities = [EyeOpennessRecord::class, StatsRecord::class, EulerRecord::class], version = 2, exportSchema = false)
abstract class DriverTrackingDatabase : RoomDatabase() {
    abstract fun eyeOpennessDao(): EyeOpennessDao
    abstract  fun eulerDao(): EulerDao
    abstract fun statsDao(): StatsDao

    companion object {
        @Volatile
        private var INSTANCE: DriverTrackingDatabase? = null

        fun getDatabase(context: Context): DriverTrackingDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DriverTrackingDatabase::class.java,
                    "driver_tracking_database"
                )
                    .addMigrations(MIGRATION_1_2) // Add your migration here
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Perform necessary database changes here
                database.execSQL("CREATE TABLE IF NOT EXISTS `stats` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `timestamp` INTEGER NOT NULL, `medianLeftEye` REAL NOT NULL, `medianRightEye` REAL NOT NULL, `recordCount` INTEGER NOT NULL, `calculationTime` INTEGER NOT NULL)")
            }
        }
    }
}
