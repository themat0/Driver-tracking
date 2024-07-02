package com.example.drivertracking.features.logs.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn // Import LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drivertracking.features.logs.viewmodel.LogsViewModel
import com.example.drivertracking.model.entities.EyeOpennessRecord


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogsScreen(viewModel: LogsViewModel = viewModel()) {
    val records by viewModel.allRecords.observeAsState(emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Logs") }
            )
        },
        content = { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                items(records) { record ->
                    // Display Records
                    DisplayRecord(record)
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DisplayRecord(record: EyeOpennessRecord) {
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
