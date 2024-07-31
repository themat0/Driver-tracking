package com.example.drivertracking.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.drivertracking.model.entities.EventRecord

@Dao
interface EventDao {
    @Insert
    fun insert(record: EventRecord): Long

    @Query("SELECT * FROM event_records ORDER BY timestamp DESC")
    fun getAllRecords(): LiveData<List<EventRecord>>

    @Query("DELETE FROM event_records")
    fun deleteRecords()
}
