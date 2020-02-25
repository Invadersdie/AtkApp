package com.atk.app.core.repository.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

abstract class PickableObject {
    abstract val showField: String
    override fun toString() = showField
}

data class Equipment(
    @SerializedName("id")
    val eid: Int = -1,
    val imei: String = "",
    val type: String = ""
) : PickableObject() {
    override val showField: String get() = imei
    override fun toString() = showField
}

data class SimCard(
    @SerializedName("id")
    val sid: Int = -1,
    val iccid: String = "",
    val phone: String = "",
    val simType: String = ""
) : PickableObject() {
    override val showField: String get() = iccid
    override fun toString() = showField
}

data class Company(
    @SerializedName("id")
    val cid: Int = -1,
    @SerializedName("Company")
    val name: String = "",
    @SerializedName("Server")
    val server: String = ""
) : PickableObject() {
    override val showField: String get() = name
    override fun toString() = showField
}

@Parcelize
data class Dut(
    val id: Int = -1,
    val name: String = "",
    val value: String = ""
) : PickableObject(), Parcelable {
    override val showField: String get() = value
    override fun toString() = showField
}