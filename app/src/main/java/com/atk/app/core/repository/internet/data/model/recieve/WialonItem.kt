package com.atk.app.core.repository.internet.data.model.recieve

import com.google.gson.annotations.SerializedName

sealed class Item
data class WialonItem(
    val cls: Long,
    val id: Long,
    val mu: Long,
    val nm: String,
    val uacl: Long,
    val uid2: String,
    val uid: String,
    val hw: Long,
    val ph: String,
    val ph2: String,
    val psw: String
) : Item()

data class GroupItem(
    @SerializedName("nm")
    val name: String, // название
    val cls: Long, // ID базового класса "avl_unit_group"
    val id: Long, // ID группы
    @SerializedName("u")
    val listOfItems: List<Long>, // массив идентификаторов объектов, входящих в группу
    val uacl: Long // уровень доступа к группе у текущего пользователя
) : Item()

data class MessageItem(
    val prms: Map<String, MessageData> // список параметров сообщений
) : Item()

data class MessageData(
    @SerializedName("v")
    val value: String, // значение параметра
    @SerializedName("ct")
    val lastChangeTime: Long, // время последнего изменения значения
    val at: Long
)