package com.example.drivertracking.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stats")
data class StatsRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val medianLeftEye: Float,
    val medianRightEye: Float,
    val headEulerAngleX: Float,
    val recordCount: Int,
    val calculationTime: Long
)
