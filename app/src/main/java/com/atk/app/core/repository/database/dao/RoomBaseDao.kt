package com.atk.app.core.repository.database.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

interface RoomBaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg obj: T): List<Long>

    @Update
    suspend fun update(vararg obj: T)

    @Delete
    fun delete(vararg obj: T)
}