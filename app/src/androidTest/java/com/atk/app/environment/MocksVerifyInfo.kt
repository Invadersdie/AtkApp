package com.atk.app.environment

import okhttp3.mockwebserver.RecordedRequest

interface MocksVerifyInfo {
    val refreshTokenCallCount: Int
    val allApiCalls: Int
    val peopleApiCallCount: Int // TODO replace
    val projectApiCallCount: Int
    val divisionsApiCallCount: Int
    val holidaysApiCallCount: Int
    fun wasProfileLoadedFor(id: Long): Boolean
    fun wasProjectLoadedFor(id: Long): Boolean
    val allLoadedProfiles: List<Long>
    val allLoadedProjects: List<Long>
    val lastLogoutRequest: RecordedRequest?
    val logoutRequestCallCount: Int
}