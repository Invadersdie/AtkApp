package com.atk.app.core.repository.internet.api

import com.atk.app.core.repository.internet.data.model.recieve.*
import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

interface WialonApi {

    @GET("ajax.html?svc=token/login")
    suspend fun login(@Query("params") params: String): LoginResponse

    @GET("ajax.html?svc=core/create_unit")
    suspend fun createUnit(@Query("params") params: String): UnitItem

    @GET("ajax.html?svc=core/get_hw_types")
    suspend fun getHwTypes(@Query("params") params: String): ListOfHwTypes

    @GET("ajax.html?svc=unit/update_device_type")
    suspend fun updateDeviceType(@Query("params") params: String): UpdateDeviceResponse

    @GET("ajax.html?svc=unit/update_phone")
    suspend fun updatePhoneNumber(@Query("params") params: String): UpdatePhoneResponse

    @GET("ajax.html?svc=core/search_item")
    suspend fun searchItemById(@Query("params") params: String): UnitItem

    @GET("ajax.html?svc=core/search_items")
    suspend fun searchItems(@Query("params") params: String): SearchResult<WialonItem>

    @GET("ajax.html?svc=core/search_items")
    suspend fun searchGroup(@Query("params") params: String): SearchResult<GroupItem>

    @GET("ajax.html?svc=core/search_items")
    suspend fun searchItemMessagesById(@Query("params") params: String): SearchResult<MessageItem>

    @GET("ajax.html?svc=item/update_custom_field")
    suspend fun updateCustomField(@Query("params") params: String): CustomFieldResponse

    @GET("ajax.html?svc=item/update_admin_field")
    suspend fun updateAdminField(@Query("params") params: String): CustomFieldResponse

    @GET("ajax.html?svc=unit_group/update_units")
    suspend fun updateGroupItems(@Query("params") params: String): GroupUpdateResponse

    @GET("ajax.html?svc=unit/update_sensor")
    suspend fun updateSensors(@Query("params") params: String): ArrayPrimitiveAndObjectResponse<Long, SensorResponse>
}

data class GroupUpdateResponse(
    @SerializedName("u")
    val list: List<Long>,
    override val error: Long
) : HasError