package com.example.drivertracking.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.drivertracking.model.entities.EulerRecord
import com.example.drivertracking.model.entities.EyeOpennessRecord

@Dao
interface EulerDao {
    @Insert
    fun insert(record: EulerRecord): Long

    @Query("SELECT * FROM euler_records ORDER BY timestamp DESC")
    fun getAllRecords(): LiveData<List<EulerRecord>>

    //Delete old record from database
    @Query("DELETE FROM euler_records WHERE timestamp < :timestamp")
    fun deleteOldRecords(timestamp: Long)

    @Query("SELECT * FROM euler_records WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    fun getRecordsBetweenTimestamps(startTime: Long, endTime: Long): List<EulerRecord>

}