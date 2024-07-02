package com.example.drivertracking.features.settings.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel : ViewModel() {
    private val _frameRate = MutableStateFlow("")
    val frameRate: StateFlow<String> = _frameRate

    private val _useFrontCamera = MutableStateFlow(false)
    val useFrontCamera: StateFlow<Boolean> = _useFrontCamera

    fun setFrameRate(rate: String) {
        _frameRate.value = rate
    }

    fun setUseFrontCamera(useFront: Boolean) {
        _useFrontCamera.value = useFront
    }
}
