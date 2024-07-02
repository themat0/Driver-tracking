package com.example.drivertracking.model.DAO

import Event
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface EventDAO {
    @Insert
    fun insertEvent(event: Event)

    @Update
    fun updateEvent(event: Event)

    @Delete
    fun deleteEvent(event: Event)

    @Query("SELECT * FROM events_table")
    fun getAll(): LiveData<List<Event>>

    @Transaction
    @Query("DELETE FROM events_table")
    fun deleteAllEvents()
}