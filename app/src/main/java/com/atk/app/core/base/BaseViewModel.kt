package com.atk.app.core.base

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.atk.app.core.models.ExecuteOnce
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext

abstract class BaseViewModel : ViewModel() {

    val permissionRequest = MutableLiveData<ExecuteOnce<Array<String>>>()

    val toastMessage = MutableLiveData<ExecuteOnce<String>>()

    val toastMessageResource = MutableLiveData<ExecuteOnce<Int>>()

    val dialog = MutableLiveData<ExecuteOnce<DialogData>>()
    val alert = MutableLiveData<ExecuteOnce<Throwable>>()
    val alertRepeatable = MutableLiveData<ExecuteOnce<AlertDataRepeatable>>()

    val navigateAction = MutableLiveData<ExecuteOnce<Uri>>()

    protected val exceptionHandler =
        CoroutineExceptionHandler { _, throwable -> showAlert(throwable) }

    protected suspend inline fun showDialogCo(dialogData: DialogData) =
        withContext(Main) { dialog.value = ExecuteOnce(dialogData) }

    protected suspend inline fun showAlertCo(throwable: Throwable) =
        withContext(Main) { alert.value = ExecuteOnce(throwable) }

    private fun showAlert(throwable: Throwable) {
        alert.value = ExecuteOnce(throwable)
    }

    protected suspend fun showAlertRepeatable(showAlertDataRepeatable: AlertDataRepeatable) =
        withContext(Main) {
            alertRepeatable.value = ExecuteOnce(showAlertDataRepeatable)
        }

    protected suspend inline fun sendToast(text: String) = withContext(Main) {
        toastMessage.value = ExecuteOnce(text)
    }

    protected suspend inline fun sendToast(text: Int) = withContext(Main) {
        toastMessageResource.value = ExecuteOnce(text)
    }
}