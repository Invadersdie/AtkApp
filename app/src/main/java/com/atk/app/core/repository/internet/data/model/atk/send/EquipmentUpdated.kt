package com.atk.app.core.repository.internet.data.model.atk.send

import com.google.gson.annotations.SerializedName

data class EquipmentUpdated(
    val id: Int,
    @SerializedName("IMEI")
    val imei: String,
    @SerializedName("CompanyID")
    val companyId: Int,
    @SerializedName("action")
    val action: String
)