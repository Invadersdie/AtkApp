package com.atk.app.core.repository.internet.data.model.atk

data class EquipmentDto(
    val Arrival: String?,
    val CompanyID: Int?,
    val Description1: String?,
    val Description2: String?,
    val Description3: String?,
    val IMEI: String?,
    val ModelID: Int?,
    val ProviderID: Int?,
    val SetupDate: String?,
    val StatusID: Int?,
    val company: Company?,
    val created_at: Any?,
    val id: Int,
    val model: Model?,
    val provider: Provider?,
    val status: Status?,
    val updated_at: Any?
)