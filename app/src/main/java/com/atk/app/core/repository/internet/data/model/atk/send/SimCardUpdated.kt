package com.atk.app.core.repository.internet.data.model.atk.send

import com.google.gson.annotations.SerializedName

data class SimCardUpdated(
    val id: Int,
    @SerializedName("IMEI")
    val iccid: String,
    @SerializedName("Number")
    val phone: String,
    @SerializedName("CompanyID")
    val company: Int
)