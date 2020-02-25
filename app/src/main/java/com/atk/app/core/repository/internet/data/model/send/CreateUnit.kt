package com.atk.app.core.repository.internet.data.model.send

data class CreateUnit(
    val name: String,
    val creatorId: Long,
    val hwTypeId: Long,
    val dataFlags: Long
)