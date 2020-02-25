package com.atk.app.core.repository

import androidx.lifecycle.liveData
import com.atk.app.core.base.UiState
import com.atk.app.core.repository.internet.data.model.recieve.HasError
import com.atk.app.core.repository.wialon.WialonResponse
import com.atk.app.core.repository.wialon.WialonThrowable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend inline fun <T> safeApiCall(
    crossinline body: suspend () -> T
): ResponseResult<T> {
    return try {
        // blocking block
        val users = withContext(Dispatchers.IO) {
            body()
        } ?: return ResponseResult.Failure(Throwable("Empty"))
        ResponseResult.Success(users)
    } catch (e: Exception) {
        ResponseResult.Failure(e)
    }
}

suspend inline fun <T : HasError> safeWialonApiCall(
    crossinline body: suspend () -> T
): ResponseResult<T> {
    val result: ResponseResult<T> = try {
        // blocking block
        val response = withContext(Dispatchers.IO) {
            body()
        }
        if (response.error != WialonResponse.NO_ERROR.code && response.error != WialonResponse.SUCCESS.code) {
            ResponseResult.Failure(WialonThrowable(WialonResponse.getWialonResponse(response.error.toInt())))
        } else ResponseResult.Success(response)
    } catch (e: Throwable) {
        ResponseResult.Failure(e)
    }
    if (result is ResponseResult.Failure && result.error is WialonThrowable && result.error.value == WialonResponse.INVALID_SESSION) throw result.error

    return result
}

inline fun <T> liveResponse(crossinline body: suspend () -> ResponseResult<T>) =
    liveData(Dispatchers.IO) {
        emit(ResponseResult.Pending)
        val result = body()
        emit(result)
    }

sealed class ResponseResult<out T> {
    data class Success<out T>(val data: T) : ResponseResult<T>()
    data class Failure(val error: Throwable) : ResponseResult<Nothing>()
    object Pending : ResponseResult<Nothing>()

    fun getValueOrNull(): T? = if (this is Success) data else null
    fun getValueOrThrow(): T = if (this is Success) data else throw (this as Failure).error

    fun throwError() {
        if (this is Failure) throw this.error
    }

    fun <H> map(mapper: (T) -> H): ResponseResult<H> {
        return when (this) {
            is Failure -> Failure(error)
            is Pending -> this
            is Success -> Success(mapper(data))
        }
    }

    fun toUiState(): UiState {
        return when (this) {
            is Pending -> UiState.Loading
            is Success -> UiState.Complete
            is Failure -> UiState.Error(this.error)
        }
    }

    fun throwUnauthorized() {
        if (this is Failure && this.error is WialonThrowable && this.error.value == WialonResponse.INVALID_SESSION) throw this.error
    }
}