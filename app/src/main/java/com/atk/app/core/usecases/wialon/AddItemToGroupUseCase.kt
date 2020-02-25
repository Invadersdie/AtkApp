package com.atk.app.core.usecases.wialon

import android.util.Log
import com.atk.app.core.repository.ResponseResult
import com.atk.app.core.repository.internet.api.GroupUpdateResponse
import com.atk.app.core.repository.internet.data.model.send.UpdateGroupItems
import com.atk.app.core.repository.wialon.WialonRepositoryImpl
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddItemToGroupUseCase @Inject constructor(private val wialonRepository: WialonRepositoryImpl) {
    suspend fun addToGroup(id: Long, isLocal: Boolean): ResponseResult<GroupUpdateResponse> =
        withContext(IO) {
            val groupName: String = if (isLocal) LOCAL_GROUP else HOSTING_GROUP
            val group = wialonRepository.getGroupItemList(groupName, isLocal).getValueOrThrow()
            Log.d("GROUP_USECASE", group.toString())

            val updatedGroupData = UpdateGroupItems(group.first, group.second.plus(id))

            return@withContext wialonRepository.updateGroupItems(updatedGroupData, isLocal)
        }

    companion object {
        const val LOCAL_GROUP: String = "!!!!!local"
        const val HOSTING_GROUP: String = "! brest_alex"
    }
}