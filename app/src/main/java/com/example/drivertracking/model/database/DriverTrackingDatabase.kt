package com.example.drivertracking.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.drivertracking.model.dao.EyeOpennessDao
import com.example.drivertracking.model.entities.EyeOpennessRecord

@Database(entities = [EyeOpennessRecord::class], version = 1, exportSchema = false)
abstract class DriverTrackingDatabase : RoomDatabase() {
    abstract fun eyeOpennessDao(): EyeOpennessDao

    companion object {
        @Volatile
        private var INSTANCE: DriverTrackingDatabase? = null

        fun getDatabase(context: Context): DriverTrackingDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DriverTrackingDatabase::class.java,
                    "driver_tracking_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
