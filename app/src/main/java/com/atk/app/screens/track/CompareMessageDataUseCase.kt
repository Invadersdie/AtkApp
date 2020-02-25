package com.atk.app.screens.track

import com.atk.app.core.repository.internet.data.model.recieve.MessageData
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CompareMessageDataUseCase @Inject constructor() {
    suspend fun compare(
        previousValueEntity: MessageDataExtended?,
        newValueEntity: MessageData
    ): MessageDataExtended = withContext(IO) {
        try {
            val previousValue = previousValueEntity?.value
            val newValue = newValueEntity.value.toFloat()

            val change = when {
                previousValue == null -> ValueChange.INIT
                previousValue.toFloat() < newValue -> ValueChange.INCREASED
                previousValue.toFloat() == newValue -> ValueChange.SAME
                previousValue.toFloat() > newValue -> ValueChange.DECREASED
                else -> ValueChange.NOT_COMPARABLE
            }
            MessageDataExtended(
                newValueEntity.value,
                change,
                newValueEntity.lastChangeTime,
                newValueEntity.at
            )
        } catch (throwable: Throwable) {
            MessageDataExtended(
                newValueEntity.value,
                ValueChange.NOT_COMPARABLE,
                newValueEntity.lastChangeTime,
                newValueEntity.at
            )
        }
    }
}