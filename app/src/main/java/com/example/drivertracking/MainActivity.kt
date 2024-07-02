package com.example.drivertracking

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drivertracking.ui.EyeOpennessViewModel
import com.example.drivertracking.room.entities.EyeOpennessRecord
import com.example.drivertracking.ui.theme.DriverTrackingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DriverTrackingTheme {
                DriverApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val viewModel: EyeOpennessViewModel = viewModel()
    val records by viewModel.allRecords.observeAsState(emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Driver Tracking") }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Add Record Button for testing
                Button(onClick = {
                    val timestamp = System.currentTimeMillis()
                    val leftEyeOpenProbability = 0.8f // Replace with actual data
                    val rightEyeOpenProbability = 0.9f // Replace with actual data
                    viewModel.insert(EyeOpennessRecord(timestamp = timestamp, leftEyeOpenProbability = leftEyeOpenProbability, rightEyeOpenProbability = rightEyeOpenProbability))
                }) {
                    Text("Add Record")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Displaying the records from the database
                records.forEach { record ->
                    Text("Timestamp: ${record.timestamp}, Left Eye: ${record.leftEyeOpenProbability}, Right Eye: ${record.rightEyeOpenProbability}")
                }
            }
        }
    )
}
