package com.atk.app.core.repository.internet.data.model.send

import com.google.gson.annotations.SerializedName

data class Sensor(
    @SerializedName("itemId")
    val itemId: Long,
    @SerializedName("id")
    val id: Long,
    @SerializedName("callMode")
    val callMode: String,
    @SerializedName("unlink")
    val unlink: Long,
    @SerializedName("n")
    val name: String,
    @SerializedName("t")
    val type: String,
    @SerializedName("d")
    val description: String,
    @SerializedName("m")
    val m: String, /* единица измерения */
    @SerializedName("p")
    val params: String, /* параметр */
    @SerializedName("f")
    val flags: Int, /* флаги датчика */
    @SerializedName("c")
    val configuration: String, /* конфигурация */
    @SerializedName("vt")
    val validationType: Int, /* тип валидации */
    @SerializedName("vs")
    val vs: Long, /* ID валидирующего датчика */
    @SerializedName("tbl")
    val tbl: List<SensorParams>
)

data class SensorParams(
    val x: Double,
    val a: Double,
    val b: Double
)