package com.atk.app.core.repository.internet.wialon.authorization

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthorizationRepository @Inject constructor() {

    var atkToken: String = ""

    var localSid: String = ""

    var hostingSid: String = ""

    fun unlogging() {
        localSid = ""
        hostingSid = ""
        atkToken = ""
    }
}