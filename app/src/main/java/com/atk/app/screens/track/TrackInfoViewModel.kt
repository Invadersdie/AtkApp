package com.atk.app.screens.track

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.atk.app.core.base.BaseViewModel
import com.atk.app.core.repository.ResponseResult
import com.atk.app.core.repository.wialon.WialonRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TrackInfoViewModel @Inject constructor(
    private val wialonRepository: WialonRepositoryImpl,
    private val compareMessageDataUseCase: CompareMessageDataUseCase
) : BaseViewModel() {

    val data = MutableLiveData<Map<String, MessageDataExtended>>()
    lateinit var trackableObject: Pair<Long, Boolean>

    private val timer = Timer()
    override fun onCleared() {
        super.onCleared()
        timer.cancel()
    }

    fun initData(id: Long, isLocal: Boolean) {
        trackableObject = Pair(id, isLocal)
        timer.schedule(object : TimerTask() {
            override fun run() {
                refreshData()
            }
        }, 0, 1000 * 60 * 5)
    }

    fun refreshData() {
        viewModelScope.launch {
            val request =
                wialonRepository.trackObject(trackableObject.first, trackableObject.second)
            when (request) {
                is ResponseResult.Success -> {
                    val values = request.data.items[0].prms.mapValues {
                        val previousValue = data.value?.get(it.key)
                        val newValue = it.value
                        compareMessageDataUseCase.compare(previousValue, newValue)
                    }
                    data.value = values
                }
            }
        }
    }
}

data class MessageDataExtended(
    val value: String, /*значение параметра */
    val valueChange: ValueChange,
    val lastChangeTime: Long, /* время последнего изменения значения */
    val at: Long
)

enum class ValueChange {
    INIT,
    NOT_COMPARABLE,
    INCREASED,
    SAME,
    DECREASED
}