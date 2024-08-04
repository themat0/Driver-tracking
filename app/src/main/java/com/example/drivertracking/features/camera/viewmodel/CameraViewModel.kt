package com.example.drivertracking.features.camera.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.drivertracking.DriverTrackingApplication
import com.example.drivertracking.model.dao.EulerDao
import com.example.drivertracking.model.dao.EventDao
import com.example.drivertracking.model.dao.EyeOpennessDao
import com.example.drivertracking.model.dao.StatsDao
import com.example.drivertracking.model.entities.EulerRecord
import com.example.drivertracking.model.entities.EventRecord
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
    private val eventDao: EventDao = DriverTrackingApplication.database.eventDao()
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
            statsDao.deleteOldRecords(timestamp)
        }
    }

    fun calculateMedianForChunk() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val calculationTime = measureTimeMillis {
                    val currentTime = System.currentTimeMillis()
                    val fiveMinutesAgo = currentTime - (30 * 1000)
                    val eyeOpennessRecord =
                        eyeOpennessDao.getRecordsBetweenTimestamps(fiveMinutesAgo, currentTime)
                    val eulerRecord =
                        eulerDao.getRecordsBetweenTimestamps(fiveMinutesAgo, currentTime)

                    val leftEyeProbabilities =
                        eyeOpennessRecord.mapNotNull { it.leftEyeOpenProbability }
                            .sorted()
                    val rightEyeProbabilities =
                        eyeOpennessRecord.mapNotNull { it.rightEyeOpenProbability }
                            .sorted()
                    val headEulerAngleX = eulerRecord.mapNotNull { it.headEulerAngleX }
                        .sorted()
                   if (leftEyeProbabilities.isEmpty() || rightEyeProbabilities.isEmpty() || headEulerAngleX.isEmpty()) {
                        Log.i("CameraViewModel", "No records found in the last 5 minutes")
                        return@launch
                    }

                }

                // Update LiveData with the time taken for the calculation
                _calculationTimeLiveData.postValue(calculationTime)

            } catch (e: Exception) {
                Log.e("CameraViewModel", "Error calculating median", e)
                // Handle error as needed
            }
        }
    }

    fun checkMedianForNotification(showNotification: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            statsDao.getLastFourStatsRecords()
                .takeIf { it.size == 4 }
                ?.let { stat ->
                    stat.sortedBy { it.timestamp }.let { statsRecords ->
                        val medianLeftEyeValues =
                            statsRecords.map { it.medianLeftEye }.toFloatArray()
                        val medianRightEyeValues =
                            statsRecords.map { it.medianRightEye }.toFloatArray()
                        val headEulerAngleXValues =
                            statsRecords.map { it.headEulerAngleX }.toFloatArray()

                        val movingAverageLeftEye = medianLeftEyeValues.toList()
                            .windowed(size = 3, step = 1, partialWindows = true) { window ->
                                window.average()
                            }.toDoubleArray()
                        val movingAverageRightEye = medianRightEyeValues.toList()
                            .windowed(size = 3, step = 1, partialWindows = true) { window ->
                                window.average()
                            }.toDoubleArray()
                        val movingAverageHeadEuler = headEulerAngleXValues.toList()
                            .windowed(size = 3, step = 1, partialWindows = true) { window ->
                                window.average()
                            }.toDoubleArray()
                        Log.i(
                            "CameraViewModel",
                            "Moving average left eye: ${movingAverageLeftEye.contentToString()}"
                        )
                        Log.i(
                            "CameraViewModel",
                            "Moving average right eye: ${movingAverageRightEye.contentToString()}"
                        )
                        Log.i(
                            "CameraViewModel",
                            "Moving average head euler: ${movingAverageHeadEuler.contentToString()}"
                        )
                        if (isDecreasing(movingAverageLeftEye) || isDecreasing(movingAverageRightEye) || isDecreasing(
                                movingAverageHeadEuler
                            )
                        ) {
                            var decreasing = ""
                            if (isDecreasing(movingAverageLeftEye)){
                                decreasing += "Obniżenie otwarcia lewego oka\n"
                            }
                            if (isDecreasing(movingAverageRightEye)){
                                decreasing += "Obniżenie otwarcia prawgo oka\n"
                            }
                            if (isDecreasing(movingAverageHeadEuler)){
                                decreasing += "Obniżenie twarzy użytkownika\n"
                            }
                            eventDao.insert(EventRecord( timestamp = System.currentTimeMillis(), description = "Wykryto stałe obniżenie parametrów użytkownika:\n$decreasing"))
                            showNotification()
                        }
                    }
                }
        }
    }


    // Funkcja sprawdzająca tendencję malejącą
    private fun isDecreasing(array: DoubleArray): Boolean {
        for (i in 1 until array.size) {
            if (array[i] > array[i - 1]) {
                return false
            }
        }
        return true
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