package com.atk.app.core.repository.internet.data.model.atk

import com.google.gson.annotations.SerializedName

data class Model(
    @SerializedName("Model")
    val model: String,
    val created_at: Any,
    val id: Int,
    val updated_at: Any
)