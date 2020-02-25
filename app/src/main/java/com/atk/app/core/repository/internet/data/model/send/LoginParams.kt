package com.atk.app.core.repository.internet.data.model.send

data class LoginParams(
    val token: String,
    val operatesAs: String = "sdk_test"
)