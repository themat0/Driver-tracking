package com.example.drivertracking.features.camera.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.drivertracking.DriverTrackingApplication
import com.example.drivertracking.model.dao.EulerDao
import com.example.drivertracking.model.dao.EyeOpennessDao
import com.example.drivertracking.model.dao.StatsDao
import com.example.drivertracking.model.entities.EulerRecord
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

    private val _medianEulerAngleXLiveData = MutableLiveData<Float>()
    val medianEulerAngleXLiveData: LiveData<Float>
        get() = _medianEulerAngleXLiveData

    private val _recordCountLiveData = MutableLiveData<Int>()
    val recordCountLiveData: LiveData<Int>
        get() = _recordCountLiveData

    private val _calculationTimeLiveData = MutableLiveData<Long>()
    val calculationTimeLiveData: LiveData<Long>
        get() = _calculationTimeLiveData

    private val eyeOpennessDao: EyeOpennessDao = DriverTrackingApplication.database.eyeOpennessDao()
    private val eulerDao: EulerDao = DriverTrackingApplication.database.eulerDao()
    private val statsDao: StatsDao = DriverTrackingApplication.database.statsDao()
    val allRecords: LiveData<List<EyeOpennessRecord>> = eyeOpennessDao.getAllRecords()

    fun insert(eyeOpennessRecord: EyeOpennessRecord, eulerRecord: EulerRecord) {
        Log.i("CameraViewModel", "Inserting record: $eyeOpennessRecord, $eulerRecord")
        viewModelScope.launch(Dispatchers.IO) {
            eyeOpennessDao.insert(eyeOpennessRecord)
            eulerDao.insert(eulerRecord)
        }
    }

    fun deleteOldRecords() {
        val timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 1 // 1 hour
        viewModelScope.launch(Dispatchers.IO) {
            eyeOpennessDao.deleteOldRecords(timestamp)
            eulerDao.deleteOldRecords(timestamp)
        }
    }

    fun calculateMedianForLast5Minutes() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val calculationTime = measureTimeMillis {
                    val currentTime = System.currentTimeMillis()
                    val fiveMinutesAgo = currentTime - (5 * 60 * 1000)
                    val eyeOpennessRecord = eyeOpennessDao.getRecordsBetweenTimestamps(fiveMinutesAgo, currentTime)
                    val eulerRecord = eulerDao.getRecordsBetweenTimestamps(fiveMinutesAgo, currentTime)

                    val leftEyeProbabilities = eyeOpennessRecord.mapNotNull { it.leftEyeOpenProbability }
                        .sorted()
                    val rightEyeProbabilities = eyeOpennessRecord.mapNotNull { it.rightEyeOpenProbability }
                        .sorted()
                    val headEulerAngleX = eulerRecord.mapNotNull { it.headEulerAngleX }
                        .sorted()


                    if(leftEyeProbabilities.isEmpty() || rightEyeProbabilities.isEmpty() || headEulerAngleX.isEmpty()) {
                        Log.i("CameraViewModel", "No records found in the last 5 minutes")
                        return@launch
                    }

                    val medianLeftEye = calculateMedian(leftEyeProbabilities)
                    val medianRightEye = calculateMedian(rightEyeProbabilities)
                    val medianEulerAngleX = calculateMedian(headEulerAngleX)

                    // Update LiveData with the calculated median values
                    _medianLeftEyeLiveData.postValue(medianLeftEye)
                    _medianRightEyeLiveData.postValue(medianRightEye)
                    _medianEulerAngleXLiveData.postValue(medianEulerAngleX)

                    // Update LiveData with the number of records
                    _recordCountLiveData.postValue(eyeOpennessRecord.size)

                    // Insert the stats into the database
                    val stats = StatsRecord(
                        timestamp = currentTime,
                        medianLeftEye = medianLeftEye,
                        medianRightEye = medianRightEye,
                        headEulerAngleX = medianEulerAngleX,
                        recordCount = eyeOpennessRecord.size,
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
                    if (newStats.medianLeftEye < oldStats.medianLeftEye *0.9 || newStats.medianRightEye < oldStats.medianRightEye*0.9 || newStats.headEulerAngleX < oldStats.headEulerAngleX*0.8) {
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
