package com.atk.app.core.repository.wialon

import com.atk.app.core.repository.ResponseResult
import com.atk.app.core.repository.internet.api.GroupUpdateResponse
import com.atk.app.core.repository.internet.data.model.recieve.*
import com.atk.app.core.repository.internet.data.model.send.*

class TestWialonRepository : WialonRepository {
    override suspend fun trackObject(
        wialonId: Long,
        isLocal: Boolean
    ): ResponseResult<SearchResult<MessageItem>> {
        TODO("Not yet implemented")
    }

    override suspend fun searchObjectsByName(
        name: String,
        isLocal: Boolean,
        from: Int,
        to: Int
    ): ResponseResult<SearchResult<WialonItem>> {
        TODO("Not yet implemented")
    }

    override suspend fun checkUnitExists(
        id: Long,
        isLocal: Boolean,
        flags: Long
    ): ResponseResult<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun createUnit(
        unitName: String,
        unitTypeId: Long,
        dataFlags: Long,
        isLocal: Boolean
    ): ResponseResult<UnitItem> {
        TODO("Not yet implemented")
    }

    override suspend fun loginHosting(loginParams: LoginParams): ResponseResult<ResponseResult.Success<Unit>> {
        TODO("Not yet implemented")
    }

    override suspend fun loginLocal(loginParams: LoginParams): ResponseResult<ResponseResult.Success<Unit>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateImei(
        wialonItem: WialonItem,
        unitImei: String,
        isLocal: Boolean
    ): ResponseResult<UpdateDeviceResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun updatePhoneNumber(
        id: Long,
        phone: String,
        isLocal: Boolean
    ): ResponseResult<UpdatePhoneResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun createSensor(
        id: Long,
        sensor: Sensor,
        isLocal: Boolean
    ): ResponseResult<ArrayPrimitiveAndObjectResponse<Long, SensorResponse>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateGroupItems(
        updateGroupItems: UpdateGroupItems,
        isLocal: Boolean
    ): ResponseResult<GroupUpdateResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getGroupItemList(
        group: String,
        isLocal: Boolean
    ): ResponseResult<Pair<Long, List<Long>>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateCustomField(
        customField: CustomField,
        isLocal: Boolean
    ): ResponseResult<CustomFieldResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getHwTypesHosting(getHwTypes: GetHwTypes): ResponseResult<ListOfHwTypes> {
        TODO("Not yet implemented")
    }

    override suspend fun getHwTypesLocal(getHwTypes: GetHwTypes): ResponseResult<ListOfHwTypes> {
        TODO("Not yet implemented")
    }
}