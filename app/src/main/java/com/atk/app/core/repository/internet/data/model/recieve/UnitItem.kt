package com.atk.app.core.repository.internet.data.model.recieve

import com.atk.app.core.repository.wialon.WialonResponse
import com.google.gson.annotations.SerializedName

data class UnitItem(
    val flags: Long,
    @SerializedName("item")
    val item: WialonItem,
    override val error: Long = WialonResponse.NO_ERROR.code
) : HasError