package com.atk.app.core.repository.internet.data.model.send

data class UpdateGroupItems(
    val itemId: Long, // id группы
    val units: List<Long> // все id группы
)