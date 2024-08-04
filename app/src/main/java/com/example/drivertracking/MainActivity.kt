package com.example.drivertracking

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drivertracking.features.settings.viewmodel.SettingsViewModel
import com.example.drivertracking.features.settings.viewmodel.SettingsViewModelFactory
import com.example.drivertracking.ui.theme.DriverTrackingTheme
import com.example.drivertracking.utils.DataStoreManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dataStoreManager = DataStoreManager(applicationContext)

        setContent {
            DriverTrackingTheme {
                // Create the SettingsViewModel with the factory
                val settingsViewModel: SettingsViewModel = viewModel(
                    factory = SettingsViewModelFactory(dataStoreManager)
                )

                // Display the main application with settings screen
                DriverApp(settingsViewModel)
            }
        }
    }
}
