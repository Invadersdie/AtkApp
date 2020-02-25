package com.atk.app.screens.login

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.atk.app.core.base.BaseViewModel
import com.atk.app.core.base.UiState
import com.atk.app.core.models.ExecuteOnce
import com.atk.app.core.repository.AtkRepository
import com.atk.app.core.repository.wialon.WialonRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

const val TAG = "ATK_TAG"

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val wialonRepository: WialonRepositoryImpl,
    private val atkRepository: AtkRepository
) : BaseViewModel() {

    val finishApp = MutableLiveData<ExecuteOnce<Boolean>>()

    val wialonLocalLogin = MutableLiveData<UiState>()
    val wialonHostingLogin = MutableLiveData<UiState>()
    val atkLogin = MutableLiveData<UiState>(UiState.Complete)

    private val wialonUpdateTimer = Timer()

    init {
        wialonUpdateTimer.schedule(object : TimerTask() {
            override fun run() {
                loginWialon()
            }
        }, 0, 270 * 1000)
    }

    val stateLogin = MediatorLiveData<Boolean>().apply {
        var atk: UiState = UiState.Loading
        var wialonHosting: UiState = UiState.Loading
        var wialonLocal: UiState = UiState.Loading

        fun finishIfAllCompleted() {
            if (atk is UiState.Complete && wialonHosting is UiState.Complete && wialonLocal is UiState.Complete) {
                postValue(true)
            }
        }

        addSource(wialonLocalLogin) {
            wialonLocal = it; finishIfAllCompleted()
        }
        addSource(wialonHostingLogin) {
            wialonHosting = it; finishIfAllCompleted()
        }
        addSource(atkLogin) {
            atk = it; finishIfAllCompleted()
        }
    }

    init {
//        loginAtk()
        loginWialon()
    }

    fun loginAtk() {
        viewModelScope.launch {
            Log.d("Coroutine_Test", "ATK_LOGIN_Started")
            atkLogin.value = UiState.Loading
            atkLogin.value = atkRepository.login().toUiState()
            Log.d("Coroutine_Test", "ATK_LOGIN_Finished")
        }
    }

    fun localWialonLogin() = viewModelScope.launch {
        Log.d("Coroutine_Test", "2")
        wialonLocalLogin.value = UiState.Loading
        wialonLocalLogin.value = wialonRepository.loginLocal().toUiState()
        Log.d("Coroutine_Test", "22")
    }

    fun hostingWialonLogin() = viewModelScope.launch {
        Log.d("Coroutine_Test", "1")
        wialonHostingLogin.value = UiState.Loading
        wialonHostingLogin.value = wialonRepository.loginHosting().toUiState()
        Log.d("Coroutine_Test", "12")
    }

    fun loginWialon() {
        localWialonLogin()
        hostingWialonLogin()
    }
}