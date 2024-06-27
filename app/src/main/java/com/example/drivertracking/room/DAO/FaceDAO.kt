package com.example.drivertracking.room.DAO

import Face
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FaceDAO {
    @Insert
    fun insertFace(face: Face)

    @Update
    fun updateFace(face: Face)

    @Delete
    fun deleteFace(face: Face)

    @Query("SELECT * FROM faces_table")
    fun getAll(): LiveData<List<Face>>

    //Get last 10 faces
    @Query("SELECT * FROM faces_table ORDER BY faceID DESC LIMIT 10")
    fun getLast10Faces(): LiveData<List<Face>>

    @Transaction
    @Query("DELETE FROM faces_table")
    fun deleteAllFaces()
}