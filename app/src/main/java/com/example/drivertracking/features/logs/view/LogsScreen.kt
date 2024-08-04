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
import com.example.drivertracking.model.entities.EventRecord
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogsScreen(viewModel: LogsViewModel = viewModel()) {
    //val statsRecords by viewModel.allStatsRecords.observeAsState(emptyList())
    val eventRecords by viewModel.allEventRecords.observeAsState(emptyList())
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

                items(eventRecords) { record ->
                    // Display Stats Records
                    DisplayEventsRecord(record)
                }
            }
        }
    )
}
@Composable
private fun DisplayEventsRecord(record: EventRecord) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("DateTime: ${Date(record.timestamp)}")
            Spacer(modifier = Modifier.height(4.dp))
            Text(record.description)
        }
    }
}