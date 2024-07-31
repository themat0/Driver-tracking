package com.example.drivertracking.features.logs.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.drivertracking.DriverTrackingApplication
import com.example.drivertracking.model.entities.EventRecord
import com.example.drivertracking.model.entities.StatsRecord

class LogsViewModel(application: Application) : AndroidViewModel(application) {
//    private val statsDao = DriverTrackingApplication.database.statsDao()
//    val allStatsRecords: LiveData<List<StatsRecord>> = statsDao.getAllStatsRecords()

    private val eventDao = DriverTrackingApplication.database.eventDao()
    val allEventRecords: LiveData<List<EventRecord>> = eventDao.getAllRecords()
}
