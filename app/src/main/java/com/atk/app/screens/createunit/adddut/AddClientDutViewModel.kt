package com.atk.app.screens.createunit.adddut

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.atk.app.core.base.BaseViewModel
import com.atk.app.core.repository.model.Dut

class AddClientDutViewModel : BaseViewModel() {
    fun getSelectedDut() = Dut(-1, dutName.value!!, dutImei.value!!)

    val dutName = MutableLiveData("")
    val dutImei = MutableLiveData("")
    val enableToCreate = MediatorLiveData<Boolean>().apply {
        value = false
        addSource(dutName) {
            postValue(it.isNotBlank() && dutImei.value!!.isNotBlank())
        }
        addSource(dutImei) {
            postValue(it.isNotBlank() && dutName.value!!.isNotBlank())
        }
    }
}