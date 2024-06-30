package com.example.drivertracking.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.drivertracking.room.entities.EyeOpennessRecord

@Dao
interface EyeOpennessDao {
    @Insert
    fun insert(record: EyeOpennessRecord): Long

    @Query("SELECT * FROM eye_openness_records ORDER BY timestamp DESC")
    fun getAllRecords(): LiveData<List<EyeOpennessRecord>>
}
