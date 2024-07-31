package com.example.drivertracking.features.settings.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drivertracking.features.settings.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(settingsViewModel: SettingsViewModel = viewModel()) {
    val maxFPSState by settingsViewModel.maxFPS.collectAsState()
    val sensitivityState by settingsViewModel.fatigueDetectionSensitivity.collectAsState()
    val isMaxFPSEnabled by settingsViewModel.isMaxFPSEnabled.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(20.dp))

        // Enable Max FPS Checkbox
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = isMaxFPSEnabled,
                onCheckedChange = { settingsViewModel.setIsMaxFPSEnabled(it) }
            )
            Text("Enable Max FPS", modifier = Modifier.padding(start = 8.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Max FPS Slider
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Max FPS (6-24):")
            Slider(
                value = maxFPSState.toFloat(),
                onValueChange = {
                    if (isMaxFPSEnabled) {
                        settingsViewModel.setMaxFPS(it.toInt())
                    }
                },
                valueRange = 6f..24f,
                steps = 18,  // steps = (max-min)/step - 1 => (24-6)/1 - 1 = 17 steps
                enabled = isMaxFPSEnabled
            )
            Text("${maxFPSState.toInt()}")
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Fatigue Sensitivity Slider
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Fatigue Sensitivity (2-10):")
            Slider(
                value = sensitivityState.toFloat(),
                onValueChange = {
                    settingsViewModel.setFatigueDetectionSensitivity(it.toInt())
                },
                valueRange = 2f..10f,
                steps = 8  // steps = (max-min)/step - 1 => (10-2)/1 - 1 = 7 steps
            )
            Text("${sensitivityState.toInt()}")
        }

        Button(onClick = { settingsViewModel.clearEventDatabase() }) {
            Text("Delete events from database")
        }
    }
}
