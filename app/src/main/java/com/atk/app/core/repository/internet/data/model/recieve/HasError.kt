package com.atk.app.core.repository.internet.data.model.recieve

interface HasError {
    val error: Long
}

data class WialonError(override val error: Long) : HasError