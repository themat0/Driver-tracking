package com.example.drivertracking.model.entities

import android.annotation.SuppressLint
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "euler_records")
data class EulerRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val headEulerAngleX:  Float,
){
    @SuppressLint("SimpleDateFormat")
    override fun toString(): String {
        //timestamp to time
        val time = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(java.util.Date(timestamp))
        return "EyeOpennessRecord(id=$id, time=$time, headEulerAngleX=$headEulerAngleX,)"
    }
}