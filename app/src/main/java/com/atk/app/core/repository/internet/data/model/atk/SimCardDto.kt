package com.atk.app.core.repository.internet.data.model.atk

data class SimCardDto(
    val IMEI: String?,
    val Number: String?,
    val ProviderID: Int?,
    val company: Any?,
    val id: Int,
    val model: Any?,
    val provider: Provider?,
    val status: Any?
)