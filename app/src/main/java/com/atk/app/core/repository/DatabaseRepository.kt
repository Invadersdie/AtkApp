package com.atk.app.core.repository

import com.atk.app.core.repository.database.AppDatabase
import com.atk.app.core.repository.database.entity.CreatedObject
import com.atk.app.core.repository.database.entity.TrackableObject
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DatabaseRepository @Inject constructor(database: AppDatabase) {

    private val createdObjectDao = database.createdObjectDao()
    private val trackableObjectDao = database.trackableObjectDao()

    fun getAllTrackableObjects() = trackableObjectDao.getAll()
    fun getTrackableObjectById(id: Long) = trackableObjectDao.getById(id)

    suspend fun updateTrackableObjects(trackableObject: TrackableObject) =
        trackableObjectDao.update(trackableObject)

    suspend fun addTrackable(trackableObject: TrackableObject) = withContext(IO) {
        trackableObjectDao.insert(trackableObject)
    }

    fun getAll() = createdObjectDao.getAll()

    suspend fun getCreatedObjectById(id: Long) =
        withContext(IO) { createdObjectDao.getItemByIdAll(id) }

    suspend fun insertCreatedObject(createdObject: CreatedObject) = withContext(IO) {
        createdObjectDao.insertAll(createdObject)
    }
}