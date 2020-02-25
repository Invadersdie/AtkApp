package com.atk.app.core.repository.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.atk.app.core.repository.database.dao.CreatedObjectDao
import com.atk.app.core.repository.database.dao.TrackableObjectDao
import com.atk.app.core.repository.database.entity.CreatedObject
import com.atk.app.core.repository.database.entity.TrackableObject

@Database(
    entities = [CreatedObject::class, TrackableObject::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DataConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun createdObjectDao(): CreatedObjectDao
    abstract fun trackableObjectDao(): TrackableObjectDao
}