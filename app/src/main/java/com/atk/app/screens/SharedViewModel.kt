package com.atk.app.screens

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.atk.app.core.base.BaseViewModel
import com.atk.app.core.models.ExecuteOnce
import com.atk.app.core.repository.AtkRepository
import com.atk.app.core.repository.ResponseResult
import com.atk.app.core.repository.model.Dut
import com.atk.app.screens.createunit.adddut.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val atkRepository: AtkRepository
) : BaseViewModel() {

    val capturedQrCode = MutableLiveData<ExecuteOnce<String>>()

    private val _dutList = MutableLiveData<ResponseResult<List<Dut>>>()
    val dutList: LiveData<ResponseResult<List<Dut>>> get() = _dutList

    private val _selectedDut = MutableLiveData<ExecuteOnce<Dut>>()
    val selectedDut: LiveData<ExecuteOnce<Dut>> get() = _selectedDut

    fun setSelectedDut(dut: Dut) {
        _selectedDut.value = ExecuteOnce(dut)
    }

    init {
        log("init")
        viewModelScope.launch {
            _dutList.value = atkRepository.getDuts()
            log(_dutList.value.toString())
        }
    }
}