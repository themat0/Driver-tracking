package com.example.drivertracking.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.drivertracking.model.entities.StatsRecord

@Dao
interface StatsDao {

    @Insert
    fun insert(statsRecord: StatsRecord)

    @Query("SELECT * FROM stats ORDER BY timestamp DESC")
    fun getAllStatsRecords(): LiveData<List<StatsRecord>>
    @Query("SELECT * FROM stats ORDER BY timestamp DESC LIMIT 1")
    fun getLastStatsRecord(): StatsRecord?
    @Query("SELECT * FROM stats ORDER BY timestamp DESC LIMIT 4")
    fun getLastFourStatsRecords(): List<StatsRecord>
}
