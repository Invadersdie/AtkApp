package com.atk.app.screens.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.atk.app.core.base.BaseViewModel
import com.atk.app.core.repository.DatabaseRepository
import com.atk.app.core.repository.database.entity.CreatedObject
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    databaseRepository: DatabaseRepository
) : BaseViewModel() {

    val dataList: LiveData<List<CreatedObject>> = databaseRepository.getAll().asLiveData()
}