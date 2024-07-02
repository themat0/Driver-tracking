package com.example.drivertracking.features.camera.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.drivertracking.DriverTrackingApplication
import com.example.drivertracking.model.entities.EyeOpennessRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class CameraViewModel(application: Application) : AndroidViewModel(application) {
    private val eyeOpennessDao = DriverTrackingApplication.database.eyeOpennessDao()
    val allRecords: LiveData<List<EyeOpennessRecord>> = eyeOpennessDao.getAllRecords()


    fun insert(record: EyeOpennessRecord) {
        Log.i("CameraViewModel", "Inserting record: $record")
        viewModelScope.launch(Dispatchers.IO) {
            eyeOpennessDao.insert(record)
        }
    }

    fun deleteOldRecords() {
        val timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 1 // 1 hour
        viewModelScope.launch(Dispatchers.IO) {
            eyeOpennessDao.deleteOldRecords(timestamp)
        }
    }
}
