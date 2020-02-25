package com.atk.app.core.repository.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.atk.app.core.repository.internet.data.model.recieve.MessageData

@Entity
data class TrackableObject(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val wialonId: Long = 0,
    val isLocal: Boolean = true,
    val data: Map<String, MessageData> = emptyMap()
)