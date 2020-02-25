package com.atk.app.core.usecases.atk

import com.atk.app.BuildConfig
import com.atk.app.core.repository.AtkRepository
import com.atk.app.core.repository.internet.data.model.atk.send.EquipmentUpdated
import com.atk.app.core.repository.internet.data.model.atk.send.SimCardUpdated
import com.atk.app.core.repository.model.Company
import com.atk.app.core.repository.model.Dut
import com.atk.app.core.repository.model.Equipment
import com.atk.app.core.repository.model.SimCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UpdateAtkStateUseCase @Inject constructor(private val atkRepository: AtkRepository) {
    suspend fun update(
        selectedEquipment: Equipment?,
        selectedSimCard: SimCard?,
        selectedCompany: Company?,
        selectedAction: String,
        dutAdded: List<Dut>
    ) = withContext(Dispatchers.IO) {
        if (BuildConfig.DEBUG) return@withContext
        launch {
            selectedEquipment?.let {
                atkRepository.updateEquipmentInfo(
                    EquipmentUpdated(
                        it.eid, it.imei, selectedCompany?.cid ?: -1,
                        selectedAction
                    )
                )
            }
        }
        launch {
            selectedSimCard?.let {
                atkRepository.updateSimCardInfo(
                    SimCardUpdated(it.sid, it.iccid, it.phone, selectedCompany?.cid ?: -1)
                )
            }
        }
        launch {
            dutAdded.forEach {
                atkRepository.updateEquipmentInfo(
                    EquipmentUpdated(
                        it.id, it.value, selectedCompany?.cid ?: -1,
                        selectedAction
                    )
                )
            }
        }
    }
}