package com.atk.app.core.repository.wialon

import com.atk.app.core.repository.ResponseResult
import com.atk.app.core.repository.internet.api.GroupUpdateResponse
import com.atk.app.core.repository.internet.data.model.recieve.*
import com.atk.app.core.repository.internet.data.model.send.*
import com.atk.app.core.repository.internet.utils.Constants

interface WialonRepository {
    suspend fun trackObject(
        wialonId: Long,
        isLocal: Boolean
    ): ResponseResult<SearchResult<MessageItem>>

    suspend fun searchObjectsByName(
        name: String,
        isLocal: Boolean,
        from: Int = 0,
        to: Int = 0
    ): ResponseResult<SearchResult<WialonItem>>

    suspend fun checkUnitExists(
        id: Long,
        isLocal: Boolean,
        flags: Long = 1025
    ): ResponseResult<Unit>

    suspend fun createUnit(
        unitName: String,
        unitTypeId: Long,
        dataFlags: Long,
        isLocal: Boolean
    ): ResponseResult<UnitItem>

    suspend fun loginHosting(loginParams: LoginParams = LoginParams(Constants.LOGIN_HOSTING_TOKEN)): ResponseResult<ResponseResult.Success<Unit>>
    suspend fun loginLocal(loginParams: LoginParams = LoginParams(Constants.LOGIN_LOCAL_TOKEN)): ResponseResult<ResponseResult.Success<Unit>>
    suspend fun updateImei(
        wialonItem: WialonItem,
        unitImei: String,
        isLocal: Boolean
    ): ResponseResult<UpdateDeviceResponse>

    suspend fun updatePhoneNumber(
        id: Long,
        phone: String,
        isLocal: Boolean
    ): ResponseResult<UpdatePhoneResponse>

    suspend fun createSensor(
        id: Long,
        sensor: Sensor,
        isLocal: Boolean
    ): ResponseResult<ArrayPrimitiveAndObjectResponse<Long, SensorResponse>>

    suspend fun updateGroupItems(
        updateGroupItems: UpdateGroupItems,
        isLocal: Boolean
    ): ResponseResult<GroupUpdateResponse>

    suspend fun getGroupItemList(
        group: String,
        isLocal: Boolean
    ): ResponseResult<Pair<Long, List<Long>>>

    suspend fun updateCustomField(
        customField: CustomField,
        isLocal: Boolean
    ): ResponseResult<CustomFieldResponse>

    suspend fun getHwTypesHosting(getHwTypes: GetHwTypes): ResponseResult<ListOfHwTypes>
    suspend fun getHwTypesLocal(getHwTypes: GetHwTypes): ResponseResult<ListOfHwTypes>
}