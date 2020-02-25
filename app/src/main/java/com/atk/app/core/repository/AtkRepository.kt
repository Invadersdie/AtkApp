package com.atk.app.core.repository

import com.atk.app.core.repository.internet.api.AtkApi
import com.atk.app.core.repository.internet.data.model.atk.send.EquipmentUpdated
import com.atk.app.core.repository.internet.data.model.atk.send.SimCardUpdated
import com.atk.app.core.repository.model.Dut
import com.atk.app.core.repository.model.Equipment
import com.atk.app.core.repository.model.SimCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AtkRepository @Inject constructor(private val atkApi: AtkApi) {

    var atkToken = ""

    suspend fun login(): ResponseResult<Unit> = withContext(Dispatchers.IO) {
        return@withContext when (val responseResult = safeApiCall { atkApi.login() }) {
            is ResponseResult.Success -> {
                atkToken = responseResult.data.data
                ResponseResult.Success(Unit)
            }
            is ResponseResult.Failure -> responseResult
            else -> ResponseResult.Failure(Throwable("Illegal state exception"))
        }
    }

    suspend fun getSimCards() = safeApiCall { atkApi.getSimCards() }.map {
        it.data.map {
            SimCard(
                sid = it.id,
                iccid = it.IMEI ?: "Not Defined",
                phone = it.Number ?: "0",
                simType = it.provider?.Provider ?: "Not Defined"
            )
        }
    }

    suspend fun getCompanies() = safeApiCall { atkApi.getCompanies() }.map { it.data }

    suspend fun getDuts() = safeApiCall { atkApi.getEquipment() }
        .map { response ->
            response.data.filter { it.IMEI != null && it.IMEI.length < 13 }
                .map { equipment ->
                    Dut(
                        equipment.id,
                        equipment.model?.model.orEmpty(),
                        equipment.IMEI!!
                    )
                }
        }

    suspend fun getEquipment() = safeApiCall { atkApi.getEquipment() }.map {
        it.data.filter { it.IMEI != null && it.IMEI.length > 11 }.map {
            Equipment(
                it.id,
                it.IMEI ?: "Not Defined",
                (it.provider?.Provider ?: "Not Defined") + " " + (it.model?.model ?: "Not Defined")
            ) /*TODO()*/
        }
    }

    suspend fun updateEquipmentInfo(equipment: EquipmentUpdated) =
        safeApiCall { atkApi.updateEquipment(equipment.id, equipment) }

    suspend fun updateSimCardInfo(simCard: SimCardUpdated) =
        safeApiCall { atkApi.updateSimCard(simCard.id, simCard) }
}