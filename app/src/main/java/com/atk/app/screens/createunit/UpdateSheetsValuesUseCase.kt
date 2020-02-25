package com.atk.app.screens.createunit

import com.atk.app.core.repository.AtkRepository
import javax.inject.Inject

class UpdateSheetsValuesUseCase @Inject constructor(private val atkRepository: AtkRepository) {
//
//    suspend fun updateGoogleSheets(
//        selectedEquipment: Equipment?,
//        selectedSimCard: SimCard?,
//        company: String?,
//        selectedAction: String
//    ) {
//        val selectedEquipmentImei = selectedEquipment?.imei ?: -1
//        val selectedSimIccid = selectedSimCard?.iccid ?: -1
//        if (selectedEquipmentImei == -1 && selectedSimIccid == -1) return
//
//        var simCardIndex = 0
//        var simCardList: MutableList<String> = ArrayList()
//        var equipmentIndex = 0
//        var equipmentList: MutableList<String> = ArrayList()
//        atkRepository.getValues(FULL_ATK_RANGE).forEachIndexed { index, list ->
//            if (list.size < 2) return@forEachIndexed
//            when (list[2].toString()) {
//                selectedEquipmentImei -> {
//                    equipmentIndex = index + 1
//                    equipmentList = MutableList(8) { elementIndex ->
//                        list.map { it.toString() }.elementAtOrElse(elementIndex) { "" }
//                    }
//
//                    equipmentList[4] = company ?: ""
//                    equipmentList[5] = selectedAction
//                    equipmentList[7] = LocalDate.now().formatToAtkFormat()
//                }
//                selectedSimIccid -> {
//                    simCardIndex = index + 1
//                    simCardList = MutableList(8) { elementIndex ->
//                        list.map { it.toString() }.elementAtOrElse(elementIndex) { "" }
//                    }
//                    simCardList[4] = company ?: ""
//                    simCardList[5] = "Активна\\Установлена"
//                    simCardList[7] = LocalDate.now().formatToAtkFormat()
//                }
//            }
//        }
//        if (simCardIndex != 0) {
//            val range = String.format(ROW_ATK, simCardIndex, simCardIndex)
//            val values = listOf(simCardList)
//            atkRepository.updateValue(range, values)
//        }
//        if (equipmentIndex != 0) {
//            val range = String.format(ROW_ATK, equipmentIndex, equipmentIndex)
//            val values = listOf(equipmentList)
//            atkRepository.updateValue(range, values)
//        }
//    }
//
//    private fun LocalDate.formatToAtkFormat(): String = atkDateTimeFormatter.print(this)
//
//    companion object {
//        private val atkDateTimeFormatter = DateTimeFormat.forPattern("dd.MM.yyyy")
//    }
}