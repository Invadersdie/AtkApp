package com.atk.app.core.repository.database

import androidx.room.TypeConverter
import com.atk.app.core.repository.internet.data.model.recieve.MessageData
import com.atk.app.core.repository.model.Company
import com.atk.app.core.repository.model.Dut
import com.atk.app.core.repository.model.Equipment
import com.atk.app.core.repository.model.SimCard
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object DataConverter {

    @TypeConverter
    @JvmStatic
    fun fromDutList(dutList: List<Dut>): String {
        return Gson().toJson(dutList)
    }

    @TypeConverter
    @JvmStatic
    fun toDutList(value: String): List<Dut> {
        return Gson().fromJson(value, object : TypeToken<List<Dut>>() {}.type)
    }

    @TypeConverter
    @JvmStatic
    fun fromSimCard(dutList: SimCard): String {
        return Gson().toJson(dutList)
    }

    @TypeConverter
    @JvmStatic
    fun toSimCard(value: String): SimCard {
        return Gson().fromJson(value, object : TypeToken<SimCard>() {}.type)
    }

    @TypeConverter
    @JvmStatic
    fun fromCompany(dutList: Company): String {
        return Gson().toJson(dutList)
    }

    @TypeConverter
    @JvmStatic
    fun toCompany(value: String): Company {
        return Gson().fromJson(value, object : TypeToken<Company>() {}.type)
    }

    @TypeConverter
    @JvmStatic
    fun fromEquipment(dutList: Equipment): String {
        return Gson().toJson(dutList)
    }

    @TypeConverter
    @JvmStatic
    fun toEquipment(value: String): Equipment {
        return Gson().fromJson(value, object : TypeToken<Equipment>() {}.type)
    }

    @TypeConverter
    @JvmStatic
    fun fromMessageMap(value: Map<String, MessageData>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    @JvmStatic
    fun toMessageMap(value: String): Map<String, MessageData> {
        return Gson().fromJson(value, object : TypeToken<Map<String, MessageData>>() {}.type)
    }
}