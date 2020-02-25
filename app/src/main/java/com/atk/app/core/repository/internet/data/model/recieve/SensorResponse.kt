package com.atk.app.core.repository.internet.data.model.recieve

import com.google.gson.annotations.SerializedName

data class SensorResponse(
    @SerializedName("id")
    val id: Long, /* ID датчика */
    @SerializedName("n")
    val name: String, /* название */
    @SerializedName("t")
    val type: String, /* тип */
    @SerializedName("d")
    val description: String, /* описание */
    @SerializedName("m")
    val m: String, /* единица измерения */
    @SerializedName("p")
    val params: String, /* параметр */
    @SerializedName("f")
    val flags: Int, /* флаги датчика */
    @SerializedName("c")
    val configuration: Any, /* конфигурация */
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