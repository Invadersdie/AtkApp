package com.atk.app.core.repository.internet.data.model.send

import com.google.gson.annotations.SerializedName

data class SearchById(
    @SerializedName("id")
    val itemId: Long,
    val flags: Long
)