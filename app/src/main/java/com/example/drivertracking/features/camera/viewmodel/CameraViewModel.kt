package com.example.drivertracking.features.camera.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.drivertracking.DriverTrackingApplication
import com.example.drivertracking.model.dao.EyeOpennessDao
import com.example.drivertracking.model.dao.StatsDao
import com.example.drivertracking.model.entities.EyeOpennessRecord
import com.example.drivertracking.model.entities.StatsRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.system.measureTimeMillis

class CameraViewModel(application: Application) : AndroidViewModel(application) {

    private val _medianLeftEyeLiveData = MutableLiveData<Float>()
    val medianLeftEyeLiveData: LiveData<Float>
        get() = _medianLeftEyeLiveData

    private val _medianRightEyeLiveData = MutableLiveData<Float>()
    val medianRightEyeLiveData: LiveData<Float>
        get() = _medianRightEyeLiveData

    private val _recordCountLiveData = MutableLiveData<Int>()
    val recordCountLiveData: LiveData<Int>
        get() = _recordCountLiveData

    private val _calculationTimeLiveData = MutableLiveData<Long>()
    val calculationTimeLiveData: LiveData<Long>
        get() = _calculationTimeLiveData

    private val eyeOpennessDao: EyeOpennessDao = DriverTrackingApplication.database.eyeOpennessDao()
    private val statsDao: StatsDao = DriverTrackingApplication.database.statsDao()
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
                val calculationTime = measureTimeMillis {
                    val currentTime = System.currentTimeMillis()
                    val fiveMinutesAgo = currentTime - (5 * 60 * 1000)
                    val records = eyeOpennessDao.getRecordsBetweenTimestamps(fiveMinutesAgo, currentTime)

                    val leftEyeProbabilities = records.mapNotNull { it.leftEyeOpenProbability }
                        .sorted()
                    val rightEyeProbabilities = records.mapNotNull { it.rightEyeOpenProbability }
                        .sorted()

                    if(leftEyeProbabilities.isEmpty() || rightEyeProbabilities.isEmpty()){
                        Log.i("CameraViewModel", "No records found in the last 5 minutes")
                        return@launch
                    }

                    val medianLeftEye = calculateMedian(leftEyeProbabilities)
                    val medianRightEye = calculateMedian(rightEyeProbabilities)

                    // Update LiveData with the calculated median values
                    _medianLeftEyeLiveData.postValue(medianLeftEye)
                    _medianRightEyeLiveData.postValue(medianRightEye)

                    // Update LiveData with the number of records
                    _recordCountLiveData.postValue(records.size)

                    // Insert the stats into the database
                    val stats = StatsRecord(
                        timestamp = currentTime,
                        medianLeftEye = medianLeftEye,
                        medianRightEye = medianRightEye,
                        recordCount = records.size,
                        calculationTime = 123
                    )
                    statsDao.insert(stats)
                }

                // Update LiveData with the time taken for the calculation
                _calculationTimeLiveData.postValue(calculationTime)

            } catch (e: Exception) {
                Log.e("CameraViewModel", "Error calculating median", e)
                // Handle error as needed
            }
        }
    }

    fun checkMedianForNotification(showNotification: () -> Unit){
        viewModelScope.launch(Dispatchers.IO) {
            statsDao.getLastTwoStatsRecords()
                .takeIf { it.size == 2 }
                ?.let { (newStats, oldStats) ->
                    if (newStats.medianLeftEye < oldStats.medianLeftEye *0.9|| newStats.medianRightEye < oldStats.medianRightEye*0.9) {
                        showNotification()
                    }
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
