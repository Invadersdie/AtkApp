package com.atk.app.screens.qrcode

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.atk.app.R
import com.atk.app.core.base.BaseViewModel
import com.atk.app.core.models.ExecuteOnce
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class QrCodeViewModel @Inject constructor() : BaseViewModel() {

    private var _qrTaken = MutableLiveData<String>()
    val qrTaken: LiveData<String> get() = _qrTaken

    val navigateWithQrCode = MutableLiveData<ExecuteOnce<EventOrError<String>>>()

    fun takeQrCode() {
        val qrCode = _qrTaken.value
        navigateWithQrCode.postValue(
            ExecuteOnce(
                if (qrCode != null && qrCode.isNotEmpty()) EventOrError.Event(qrCode)
                else EventOrError.Error<String>((R.string.no_qr_code_error))
            )
        )
    }

    fun onBarCodeDetected(qrCode: String) {
        _qrTaken.postValue(qrCode)
    }
}

sealed class EventOrError<T> {
    data class Event<T>(val eventValue: T) : EventOrError<T>()
    data class Error<T>(val text: Int) : EventOrError<T>()
}