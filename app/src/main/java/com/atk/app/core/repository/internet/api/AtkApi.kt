package com.atk.app.core.repository.internet.api

import com.atk.app.core.repository.internet.data.model.atk.ApiResponseDto
import com.atk.app.core.repository.internet.data.model.atk.EquipmentDto
import com.atk.app.core.repository.internet.data.model.atk.SimCardDto
import com.atk.app.core.repository.internet.data.model.atk.send.EquipmentUpdated
import com.atk.app.core.repository.internet.data.model.atk.send.SimCardUpdated
import com.atk.app.core.repository.internet.data.model.recieve.UnitItem
import com.atk.app.core.repository.model.Company
import retrofit2.http.*

interface AtkApi {

    @GET(".")
    suspend fun login(): AtkResponseDto

    @GET("getEquipmentList")
    suspend fun getEquipment(): ApiResponseDto<EquipmentDto>

    @GET("getSimList")
    suspend fun getSimCards(): ApiResponseDto<SimCardDto>

    @GET("getCompanyList")
    suspend fun getCompanies(): ApiResponseDto<Company>

    @GET(".")
    suspend fun searchItemById(@Query("params") params: String): UnitItem

    @PUT("equipment/{equipment}")
    suspend fun updateEquipment(
        @Path("equipment") equipmentId: Int,
        @Body equipment: EquipmentUpdated
    ): AtkResponseDto

    @PUT("simcard/{simcard}")
    suspend fun updateSimCard(
        @Path("simcard") simId: Int,
        @Body simCard: SimCardUpdated
    ): AtkResponseDto
}

data class AtkResponseDto(
    val data: String,
    val error: String?
)