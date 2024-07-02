package com.example.drivertracking

import android.app.Application
import com.example.drivertracking.model.DriverTrackingDatabase

class DriverTrackingApplication : Application() {
    companion object {
        lateinit var database: DriverTrackingDatabase
            private set
    }

    override fun onCreate() {
        super.onCreate()
        database = DriverTrackingDatabase.getDatabase(this)
    }
}
