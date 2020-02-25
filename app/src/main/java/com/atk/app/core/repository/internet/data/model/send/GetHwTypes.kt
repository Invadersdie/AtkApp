package com.atk.app.core.repository.internet.data.model.send

data class GetHwTypes(
    val filterType: String = "",
    val filterValue: String = "",
    val includeType: Boolean = true,
    val ignoreRename: Boolean = false
)