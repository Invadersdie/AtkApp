package com.atk.app.core.base

sealed class UiState {
    object Complete : UiState()
    object Loading : UiState()
    class Error(val throwable: Throwable = Throwable()) : UiState()
}