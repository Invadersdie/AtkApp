package com.atk.app.core.usecases.wialon

import com.atk.app.BuildConfig
import com.atk.app.core.dagger.SENSORS_DATA
import com.atk.app.core.repository.ResponseResult
import com.atk.app.core.repository.internet.data.model.recieve.UnitItem
import com.atk.app.core.repository.internet.data.model.send.CustomField
import com.atk.app.core.repository.internet.data.model.send.CustomFieldType
import com.atk.app.core.repository.internet.data.model.send.Sensor
import com.atk.app.core.repository.model.Dut
import com.atk.app.core.repository.model.Equipment
import com.atk.app.core.repository.model.SimCard
import com.atk.app.core.repository.wialon.Flags
import com.atk.app.core.repository.wialon.WialonRepositoryImpl
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Named

const val CLIENTS = "клиента"
const val OURS = "наш"

class CreateUnitUseCase @Inject constructor(
    private val wialonRepository: WialonRepositoryImpl,
    private val addItemToGroupUseCase: AddItemToGroupUseCase,
    @Named(SENSORS_DATA) private val sensorsData: HashMap<String, List<Sensor>>
) {

    private var executeStage = Array(5) { true }
    private var name = ""
    private var unitId = ""
    private var unitItem: UnitItem? = null

    private fun refreshUseCaseData() {
        executeStage = Array(6) { true }
        name = ""
        unitId = ""
        unitItem = null
    }

    private fun getSimCardOwner(simCard: SimCard?): String {
        return if (simCard == null || simCard.sid == -1) CLIENTS else OURS
    }

    private fun getDutOwner(dut: Dut): String {
        return if (dut.id == -1) CLIENTS else OURS
    }

    private fun getEquipmentOwner(equipment: Equipment): String {
        return if (equipment.eid == -1) CLIENTS else OURS
    }

    private fun getDefaultDutList(company: String, sim: SimCard?, equipment: Equipment) = listOf(
        Dut(-1, CustomFieldType.CLIENT, company),
        Dut(
            -1,
            CustomFieldType.SIM,
            sim?.simType.orEmpty() + " " + getSimCardOwner(sim)
        ),
        Dut(
            -1,
            CustomFieldType.START,
            SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())
        ),
        Dut(-1, CustomFieldType.STATUS, "OK"),
        Dut(-1, CustomFieldType.CASH, "NO"),
        Dut(-1, CustomFieldType.CURRENCY, ""),
        Dut(-1, CustomFieldType.PRICE, ""),
        Dut(-1, CustomFieldType.EQUIPMENT, equipment.type + " " + getEquipmentOwner(equipment))
    )

    private suspend fun updateImeiAsync(unitImei: String, isLocal: Boolean) = withContext(IO) {
        async { wialonRepository.updateImei(unitItem!!.item, unitImei, isLocal) }
    }

    private suspend fun updatePhoneAsync(phoneNumber: String, isLocal: Boolean) = withContext(IO) {
        async {
            wialonRepository.updatePhoneNumber(unitItem!!.item.id, phoneNumber, isLocal)
        }
    }

    private suspend fun updateCustomFields(
        customFieldList: List<Dut>,
        company: String,
        sim: SimCard?,
        equipment: Equipment,
        isLocal: Boolean
    ) = withContext(IO) {
        customFieldList
            .map { it.copy(name = it.name + " " + getDutOwner(it)) }
            .plus(getDefaultDutList(company, sim, equipment))
            .map {
                val customField = CustomField(unitItem!!.item.id, it.name, it.value)
                return@map async {
                    wialonRepository.updateCustomField(customField, isLocal)
                }
            }
    }

    private suspend fun addObjectToGroupAsync(isLocal: Boolean) = withContext(IO) {
        async {
            addItemToGroupUseCase.addToGroup(unitItem!!.item.id, isLocal)
        }
    }

    private suspend fun addSensorsToObjectAsync(list: List<Sensor>, isLocal: Boolean) =
        withContext(IO) {
            list.map {
                async {
                    wialonRepository.createSensor(unitItem!!.item.id, it, isLocal)
                }
            }
        }

    suspend fun create(
        isLocal: Boolean,
        unitName: String,
        unitTypeId: Long,
        phoneNumber: String,
        company: String,
        sim: SimCard?,
        equipment: Equipment,
        unitImei: String,
        customFieldList: List<Dut>,
        retry: Boolean = false,
        dataFlags: Long = Flags.BASE or Flags.ADDITIONAL_PROPERTIES
    ): ResponseResult<Long> = withContext(IO) {
        if (BuildConfig.DEBUG) return@withContext ResponseResult.Success(22510896)

        if (!retry) refreshUseCaseData()

//        val checkExists = wialonRepository.checkUnitExists(unitImei.toLong())
//        if (checkExists is ResponseResult.Success) {

        if (executeStage[0]) {
            val createdObject = withContext(IO) {
                wialonRepository.createUnit(unitName, unitTypeId, dataFlags, isLocal)
            }
            if (createdObject is ResponseResult.Success) {
                unitItem = createdObject.data
                executeStage[0] = false
            } else {
                return@withContext (createdObject as ResponseResult.Failure)
            }
        }
        val imeiResult = if (executeStage[1]) updateImeiAsync(unitImei, isLocal) else null
        val phoneResult = if (executeStage[2]) updatePhoneAsync(phoneNumber, isLocal) else null
        val customFieldResult = if (executeStage[3]) updateCustomFields(
            customFieldList,
            company,
            sim,
            equipment, isLocal
        ) else null
        val addToGroupResult = if (executeStage[4]) addObjectToGroupAsync(isLocal) else null
        val addSensorsResult = if (executeStage[5] && sensorsData.containsKey(equipment.type)) {
            addSensorsToObjectAsync(sensorsData[equipment.type]!!, isLocal)
        } else null

        val listOfThrowable = mutableListOf<Throwable>()
        imeiResult.putExecResultReturnThrowable(1)?.let { listOfThrowable.add(it) }
        phoneResult.putExecResultReturnThrowable(2)?.let { listOfThrowable.add(it) }
        customFieldResult.putExecResultReturnThrowable(3)?.let { listOfThrowable.add(it) }
        addToGroupResult.putExecResultReturnThrowable(4)?.let { listOfThrowable.add(it) }
        addSensorsResult.putExecResultReturnThrowable(5)?.let { listOfThrowable.add(it) }

        if (listOfThrowable.isEmpty()) return@withContext ResponseResult.Success(unitItem!!.item.id) else
            ResponseResult.Failure(listOfThrowable.first())

//        } else {
//            return@withContext checkExists
//        }
    }

    private suspend fun <T> Deferred<ResponseResult<T>>?.putExecResultReturnThrowable(index: Int): Throwable? {
        if (this == null) return null
        val value = this.await()
        executeStage[index] = value is ResponseResult.Failure
        return if (value is ResponseResult.Failure) value.error else null
    }

    private suspend fun <T> List<Deferred<ResponseResult<T>>>?.putExecResultReturnThrowable(index: Int): Throwable? {
        if (this == null) return null
        val value = this.awaitAll()
        executeStage[index] = value.any { it is ResponseResult.Failure }
        return (value.firstOrNull { it is ResponseResult.Failure } as? ResponseResult.Failure)?.error
    }
}