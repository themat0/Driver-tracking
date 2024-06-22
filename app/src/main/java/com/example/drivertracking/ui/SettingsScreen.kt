package com.example.drivertracking.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drivertracking.SettingsViewModel

@Composable
fun SettingsScreen(settingsViewModel: SettingsViewModel = viewModel()) {
    val frameRate by settingsViewModel.frameRate.collectAsState()
    val useFrontCamera by settingsViewModel.useFrontCamera.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(20.dp))

        // Frame Rate Input
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Frame Rate: ", modifier = Modifier.padding(end = 8.dp))
            BasicTextField(
                value = frameRate,
                onValueChange = { settingsViewModel.setFrameRate(it) },
                modifier = Modifier
                    .border(1.dp, MaterialTheme.colorScheme.primary)
                    .padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Camera Switch
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Use Front Camera: ", modifier = Modifier.padding(end = 8.dp))
            Checkbox(
                checked = useFrontCamera,
                onCheckedChange = { settingsViewModel.setUseFrontCamera(it) }
            )
        }
    }
}
