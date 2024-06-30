package com.example.drivertracking.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drivertracking.room.entities.EyeOpennessRecord
import com.example.drivertracking.ui.theme.DriverTrackingTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogsScreen(viewModel: EyeOpennessViewModel = viewModel()) {
    val records by viewModel.allRecords.observeAsState(emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Logs") }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Add Record Button
                AddRecordButton(viewModel)

                Spacer(modifier = Modifier.height(16.dp))

                // Display Records
                DisplayRecords(records)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddRecordButton(viewModel: EyeOpennessViewModel) {
    Button(
        onClick = {
            val timestamp = System.currentTimeMillis()
            val leftEyeOpenProbability = 0.8f // Replace with actual data
            val rightEyeOpenProbability = 0.9f // Replace with actual data
            viewModel.insert(
                EyeOpennessRecord(
                    timestamp = timestamp,
                    leftEyeOpenProbability = leftEyeOpenProbability,
                    rightEyeOpenProbability = rightEyeOpenProbability
                )
            )
        },
//        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
    ) {
        Text("Add Record")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DisplayRecords(records: List<EyeOpennessRecord>) {
    Column {
        records.forEach { record ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Timestamp: ${record.timestamp}")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Left Eye: ${record.leftEyeOpenProbability}")
                    Text("Right Eye: ${record.rightEyeOpenProbability}")
                }
            }
        }
    }
}
