package com.example.drivertracking.features.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {
    private val _maxFPS = MutableStateFlow(24)
    val maxFPS: StateFlow<Int> get() = _maxFPS

    private val _fatigueDetectionSensitivity = MutableStateFlow(5)
    val fatigueDetectionSensitivity: StateFlow<Int> get() = _fatigueDetectionSensitivity

    private val _isMaxFPSEnabled = MutableStateFlow(true)
    val isMaxFPSEnabled: StateFlow<Boolean> get() = _isMaxFPSEnabled

    fun setMaxFPS(value: Int) {
        viewModelScope.launch {
            _maxFPS.value = value
        }
    }

    fun setFatigueDetectionSensitivity(value: Int) {
        viewModelScope.launch {
            _fatigueDetectionSensitivity.value = value
        }
    }

    fun setIsMaxFPSEnabled(enabled: Boolean) {
        viewModelScope.launch {
            _isMaxFPSEnabled.value = enabled
        }
    }
}
