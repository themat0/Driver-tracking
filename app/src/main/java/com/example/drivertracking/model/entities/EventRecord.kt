package com.example.drivertracking.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "event_records")
data class EventRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val description: String,
)