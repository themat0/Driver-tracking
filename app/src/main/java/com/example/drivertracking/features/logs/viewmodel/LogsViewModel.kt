package com.example.drivertracking.features.logs.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.drivertracking.DriverTrackingApplication
import com.example.drivertracking.model.entities.EyeOpennessRecord


class LogsViewModel(application: Application) : AndroidViewModel(application) {
    private val eyeOpennessDao = DriverTrackingApplication.database.eyeOpennessDao()
    val allRecords: LiveData<List<EyeOpennessRecord>> = eyeOpennessDao.getAllRecords()


}
