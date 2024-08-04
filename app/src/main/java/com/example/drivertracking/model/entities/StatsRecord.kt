package com.example.drivertracking.model.entities

import android.annotation.SuppressLint
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
) {
    @SuppressLint("SimpleDateFormat")
    override fun toString(): String {
        //timestamp to time
        val time = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(java.util.Date(timestamp))
        return "StatsRecord(id=$id, time=$time)"
    }
}
