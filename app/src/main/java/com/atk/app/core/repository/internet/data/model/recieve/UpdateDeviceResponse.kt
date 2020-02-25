package com.atk.app.core.repository.internet.data.model.recieve

import com.atk.app.core.repository.wialon.WialonResponse

data class UpdateDeviceResponse(
    val hw: Long,
    val uid: String,
    val hwd: Long,
    override val error: Long = WialonResponse.NO_ERROR.code
) : HasError