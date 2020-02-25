package com.atk.app.core.repository.internet.data.model.recieve

import com.atk.app.core.repository.wialon.WialonResponse

data class LoginResponse(
    val classes: Classes,
    val eid: String,
    val host: String,
    val tm: Int,
    val user: User,
    override val error: Long = WialonResponse.NO_ERROR.code
) : HasError

data class Classes(
    val avl_hw: Int,
    val avl_resource: Int,
    val avl_retranslator: Int,
    val avl_route: Int,
    val avl_unit: Int,
    val avl_unit_group: Int,
    val user: Int
)

data class Prp(
    val addr_provider: String,
    val cfmt: String,
    val city: String,
    val dst: String,
    val fpnl: String,
    val language: String,
    val show_log: String,
    val tz: String,
    val user_settings_hotkeys: String
)

data class Token(
    val app: String,
    val at: Int,
    val ct: Int,
    val dur: Int,
    val fl: Int,
    val items: List<Any>,
    val p: String
)

data class User(
    val bact: Int,
    val cls: Int,
    val crt: Int,
    val fl: Int,
    val hm: String,
    val id: Long,
    val nm: String,
    val prp: Prp,
    val th: String,
    val token: Token,
    val uacl: Int
)