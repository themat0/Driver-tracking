package com.example.drivertracking.features.camera.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.drivertracking.DriverTrackingApplication
import com.example.drivertracking.model.dao.EyeOpennessDao
import com.example.drivertracking.model.entities.EyeOpennessRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CameraViewModel(application: Application) : AndroidViewModel(application) {

    private val _medianLeftEyeLiveData = MutableLiveData<Float>()
    val medianLeftEyeLiveData: LiveData<Float>
        get() = _medianLeftEyeLiveData

    private val _medianRightEyeLiveData = MutableLiveData<Float>()
    val medianRightEyeLiveData: LiveData<Float>
        get() = _medianRightEyeLiveData


    private val eyeOpennessDao: EyeOpennessDao = DriverTrackingApplication.database.eyeOpennessDao()
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

    fun calculateMedianForLast5Minutes() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val currentTime = System.currentTimeMillis()
                val fiveMinutesAgo = currentTime - (5 * 60 * 1000)
                val records = eyeOpennessDao.getRecordsBetweenTimestamps(fiveMinutesAgo, currentTime)

                val leftEyeProbabilities = records.mapNotNull { it.leftEyeOpenProbability }
                    .sorted()
                val rightEyeProbabilities = records.mapNotNull { it.rightEyeOpenProbability }
                    .sorted()

                val medianLeftEye = calculateMedian(leftEyeProbabilities)
                val medianRightEye = calculateMedian(rightEyeProbabilities)

                // Update LiveData with the calculated median values
                _medianLeftEyeLiveData.postValue(medianLeftEye)
                _medianRightEyeLiveData.postValue(medianRightEye)

            } catch (e: Exception) {
                Log.e("CameraViewModel", "Error calculating median", e)
                // Handle error as needed
            }
        }
    }

    private fun calculateMedian(list: List<Float>): Float {
        val size = list.size
        return if (size % 2 == 0) {
            (list[size / 2 - 1] + list[size / 2]) / 2
        } else {
            list[size / 2]
        }
    }
}