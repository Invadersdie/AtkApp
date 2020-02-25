package com.atk.app.core.repository.internet.data.model.recieve

import com.atk.app.core.repository.wialon.WialonResponse

data class UpdatePhoneResponse(
    val ph: String,
    override val error: Long = WialonResponse.NO_ERROR.code
) : HasError