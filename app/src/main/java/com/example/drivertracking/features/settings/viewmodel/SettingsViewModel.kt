package com.example.drivertracking.features.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drivertracking.utils.DataStoreManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingsViewModel(private val dataStoreManager: DataStoreManager) : ViewModel() {

    val maxFPS: StateFlow<Int> = dataStoreManager.maxFPS.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = 24
    )

    val fatigueDetectionSensitivity: StateFlow<Int> = dataStoreManager.sensitivity.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = 5
    )

    val isMaxFPSEnabled: StateFlow<Boolean> = dataStoreManager.isMaxFPSEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = true
    )

    fun setMaxFPS(value: Int) {
        viewModelScope.launch {
            dataStoreManager.setMaxFPS(value)
        }
    }

    fun setFatigueDetectionSensitivity(value: Int) {
        viewModelScope.launch {
            dataStoreManager.setSensitivity(value)
        }
    }

    fun setIsMaxFPSEnabled(enabled: Boolean) {
        viewModelScope.launch {
            dataStoreManager.setIsMaxFPSEnabled(enabled)
        }
    }
}
