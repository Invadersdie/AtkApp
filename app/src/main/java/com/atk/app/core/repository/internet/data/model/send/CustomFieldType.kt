package com.atk.app.core.repository.internet.data.model.send

import com.google.gson.annotations.SerializedName

object CustomFieldType {
    const val CLIENT = "CLIENT1"
    const val SIM = "sim"
    const val START = "START"
    const val STATUS = "STATUS"
    const val CURRENCY = "CURRENCY"
    const val CASH = "CASH"
    const val PRICE = "PRICE1"
    const val EQUIPMENT = "Device"
}

data class CustomField(
    @SerializedName("itemId")
    val itemId: Long,
    @SerializedName("n")
    val name: String,
    @SerializedName("v")
    val value: String,
    @SerializedName("callMode")
    val callMode: String = "create",
    @SerializedName("id")
    val id: Long = 0
)