package com.example.drivertracking.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.drivertracking.model.entities.EyeOpennessRecord

@Dao
interface EyeOpennessDao {
    @Insert
    fun insert(record: EyeOpennessRecord): Long

    @Query("SELECT * FROM eye_openness_records ORDER BY timestamp DESC")
    fun getAllRecords(): LiveData<List<EyeOpennessRecord>>

    //Delete old record from database
    @Query("DELETE FROM eye_openness_records WHERE timestamp < :timestamp")
    fun deleteOldRecords(timestamp: Long)
}
