package com.atk.app.core.repository.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.atk.app.core.repository.database.entity.TrackableObject
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackableObjectDao : RoomBaseDao<TrackableObject> {

    @Query("SELECT * from TrackableObject")
    fun getAll(): Flow<List<TrackableObject>>

    @Query("SELECT * from TrackableObject where id = :id")
    fun getById(id: Long): Flow<TrackableObject?>
}