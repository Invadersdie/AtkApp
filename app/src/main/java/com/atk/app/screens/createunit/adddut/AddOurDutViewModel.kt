package com.atk.app.screens.createunit.adddut

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.atk.app.core.base.BaseViewModel
import com.atk.app.core.repository.model.Dut

class AddOurDutViewModel : BaseViewModel() {

    fun removeSelectedDut() {
        _selectedDut.value = null
    }

    fun selectedDut(dut: Dut) {
        _selectedDut.value = dut
    }

    val selectedDut: LiveData<Dut?> get() = _selectedDut
    private val _selectedDut = MutableLiveData<Dut?>(null)
}