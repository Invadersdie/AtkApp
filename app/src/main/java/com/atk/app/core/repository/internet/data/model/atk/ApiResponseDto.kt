package com.atk.app.core.repository.internet.data.model.atk

data class ApiResponseDto<T>(
    val data: List<T>,
    val draw: Int,
//    val input: List<Any>,
    val recordsFiltered: Int,
    val recordsTotal: Int
)