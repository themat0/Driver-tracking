package com.example.drivertracking.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.drivertracking.DriverTrackingApplication
import com.example.drivertracking.room.entities.EyeOpennessRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EyeOpennessViewModel(application: Application) : AndroidViewModel(application) {
    private val eyeOpennessDao = DriverTrackingApplication.database.eyeOpennessDao()
    val allRecords: LiveData<List<EyeOpennessRecord>> = eyeOpennessDao.getAllRecords()

    fun insert(record: EyeOpennessRecord) {
        viewModelScope.launch(Dispatchers.IO) {
            eyeOpennessDao.insert(record)
        }
    }
}
