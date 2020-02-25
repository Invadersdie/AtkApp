package com.atk.app.core.repository.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.atk.app.core.repository.model.Company
import com.atk.app.core.repository.model.Dut
import com.atk.app.core.repository.model.Equipment
import com.atk.app.core.repository.model.SimCard

@Entity
data class CreatedObject(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    val wialonId: Long,
    val unitName: String,
    val done: Boolean,
    val isLocal: Boolean,
    val typeId: Long,
    val unitImei: String,
    val action: String,
    val simCard: SimCard,
    val company: Company,
    val equipment: Equipment,
    val dutList: List<Dut>
)