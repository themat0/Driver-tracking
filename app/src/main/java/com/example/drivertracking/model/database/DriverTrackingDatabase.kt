package com.example.drivertracking.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.drivertracking.model.dao.EulerDao
import com.example.drivertracking.model.dao.EventDao
import com.example.drivertracking.model.dao.EyeOpennessDao
import com.example.drivertracking.model.dao.StatsDao
import com.example.drivertracking.model.entities.EulerRecord
import com.example.drivertracking.model.entities.EventRecord
import com.example.drivertracking.model.entities.EyeOpennessRecord
import com.example.drivertracking.model.entities.StatsRecord

@Database(entities = [EyeOpennessRecord::class, StatsRecord::class, EulerRecord::class, EventRecord::class], version = 4, exportSchema = false)
abstract class DriverTrackingDatabase : RoomDatabase() {
    abstract fun eyeOpennessDao(): EyeOpennessDao
    abstract  fun eulerDao(): EulerDao
    abstract fun statsDao(): StatsDao
    abstract fun eventDao(): EventDao

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
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
