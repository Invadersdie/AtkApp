package com.atk.app.core.repository.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.atk.app.core.repository.database.entity.CreatedObject
import kotlinx.coroutines.flow.Flow

@Dao
interface CreatedObjectDao : RoomBaseDao<CreatedObject> {
    @Query("SELECT * FROM createdObject ORDER BY uid DESC")
    fun getAll(): Flow<List<CreatedObject>>

    @Query("SELECT * FROM createdObject WHERE uid = :id")
    fun getItemByIdAll(id: Long): Flow<CreatedObject?>

    @Insert
    suspend fun insertAll(vararg users: CreatedObject)

    @Delete
    suspend fun delete(user: CreatedObject)
}