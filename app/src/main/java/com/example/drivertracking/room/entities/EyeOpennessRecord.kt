package com.example.drivertracking.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "eye_openness_records")
data class EyeOpennessRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val leftEyeOpenProbability: Float,
    val rightEyeOpenProbability: Float
)
