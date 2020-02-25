package com.atk.app.screens.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.atk.app.core.base.BaseViewModel
import com.atk.app.core.repository.ResponseResult
import com.atk.app.core.repository.internet.data.model.recieve.WialonItem
import com.atk.app.core.repository.wialon.WialonRepositoryImpl
import com.atk.app.screens.createunit.adddut.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val wialonRepository: WialonRepositoryImpl
) : BaseViewModel() {

    val searchQuery = MutableLiveData("")
    val searchLocal = MutableLiveData(true)
    val data = MutableLiveData<List<WialonItem>>(emptyList())
    val loading = MutableLiveData(false)

    var currentLast = PAGE_SIZE

    fun changeQuery(query: String) {
        currentLast = PAGE_SIZE
        searchQuery.value = query
        data.value = emptyList()
        downloadNextPage()
        log(query)
    }

    fun downloadNextPage() {
        viewModelScope.launch {
            loading.value = true
            val query = searchQuery.value.orEmpty()
            val result = wialonRepository.searchObjectsByName(
                query,
                searchLocal.value!!,
                currentLast - PAGE_SIZE,
                currentLast
            )
            when (result) {
                is ResponseResult.Success -> {
                    data.value = data.value!!.plus(result.data.items)
                }
            }
            currentLast += PAGE_SIZE
            loading.value = false
        }
    }

    companion object {
        const val PAGE_SIZE = 20
    }
}