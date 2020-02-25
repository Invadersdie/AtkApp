package com.atk.app.environment

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

private val personProfileQuery = Regex("\\/api\\/v2\\/people\\/[0-9]*\$")

class MockServerConfigurator(
    private val dispatcher: PredicateDispatcher,
    private val verifyInfo: MockServerVerifyInfoImpl,
    private val baseUrl: String
) {

    private fun selectedFuncForPredicate(
        recordedRequest: RecordedRequest,
        apiCall: ApiCall
    ): Boolean =
        when (apiCall.compareFunc) {
            ApiCall.CompareFunc.StartsWith -> recordedRequest.path!!.startsWith(apiCall.path)
            ApiCall.CompareFunc.Equals -> recordedRequest.path == apiCall.path
            is ApiCall.CompareFunc.Matches -> recordedRequest.path!!.matches(apiCall.compareFunc.regex)
        }

    fun returnDataOnApiCallFromFile(apiCall: ApiCall, fileName: String) {
        dispatcher.handleRequest(
            { selectedFuncForPredicate(it, apiCall) },
            {
                logAction(it, apiCall)
                MockResponse().setBody(AssetsReader.readFromAssets(fileName))
            }
        )
    }

    fun definedApiReturnsError(apiCall: ApiCall, errorCode: Int) {
        dispatcher.handleRequest(
            { selectedFuncForPredicate(it, apiCall) },
            {
                logAction(it, apiCall)
                MockResponse().setResponseCode(errorCode)
            }
        )
    }

    fun returnDetailsOnApiCallFromFile(apiCall: ApiCall, fileName: String) {
        dispatcher.handleRequest(
            { selectedFuncForPredicate(it, apiCall) },
            {
                val id =
                    it.path!!.split("/").find { element -> element.toLongOrNull() != null }
                        ?.toLong()
                        ?: Long.MIN_VALUE
                logAction(it, apiCall, id)
                MockResponse().setBody(AssetsReader.readFromAssets(fileName))
            }
        )
    }

    fun definedApiReturnsSomethingToAvoidErrorPopup(apiCall: ApiCall) {
        dispatcher.handleRequest(
            { selectedFuncForPredicate(it, apiCall) },
            { MockResponse().setBody("{}") }
        )
    }

    fun peopleApiAvatarReturnsSomeImage() {
        dispatcher.handleRequest(
            { it.path!!.startsWith("/api/v2/people/avatar/") },
            {
                logAccessToken(it, "avatar")
                MockResponse()
                    .setHeader("Content-Type", "image/png")
                    .setBody(AssetsReader.readFromAssets("test-avatar.png"))
            }
        )
    }

    fun authApiRefreshesToken() {
        dispatcher.handleRequest(
            { it.path!!.startsWith("//auth/realms/GTE-UAT/protocol/openid-connect/token") },
            {
                verifyInfo.refreshTokenCallCount++
                MockResponse().setBody(AssetsReader.readFromAssets("authentication-actual-token-response.json"))
            }
        )
    }

    fun setupOAtuhConfig() {
        dispatcher.handleRequest(
            { it.path!!.endsWith(".well-known/openid-configuration") },
            {
                MockResponse().setBody(
                    AssetsReader.readTextFromAssets("openid-configuration.json").replace(
                        "\$baseUrl",
                        baseUrl
                    )
                )
            }
        )
    }

    fun logoutReturns204() {
        dispatcher.handleRequest(
            { it.path!!.contains("logout") },
            {
                verifyInfo.lastLogoutRequest = it
                verifyInfo.logoutRequestCallCount++
                MockResponse().setResponseCode(204)
            }
        )
    }

    private fun logAction(
        recordedRequest: RecordedRequest,
        apiCall: ApiCall,
        id: Long = Long.MIN_VALUE
    ) {
        logAccessToken(recordedRequest, apiCall.logTag)
        when (apiCall) {
            ApiCall.ATK_EQUIPMENT_LIST_CALL -> verifyInfo.peopleApiCallCount++
            ApiCall.PEOPLE_API_LIST_CALL -> verifyInfo.peopleApiCallCount++
            ApiCall.PEOPLE_API_PERSON_CALL -> {
                verifyInfo.peopleApiCallCount++
                verifyInfo.addIdToCalledAtPersonalProfile(id)
            }
            ApiCall.HOLIDAYS_API_ANY_CALL -> verifyInfo.holidaysApiCallCount++
        }
    }

    private fun logAccessToken(request: RecordedRequest, tag: String) {
        val accessTokenValue = request.headers["Authorization"] ?: "no token"
        android.util.Log.i("access_token_$tag", accessTokenValue)
    }

    fun holidayApiReturnEmployeeRequestListFromFile(fileName: String) {
        dispatcher.handleRequest(
            { it.path!!.endsWith("/requests") },
            { MockResponse().setBody(AssetsReader.readFromAssets(fileName)) }
        )
    }

    fun holidayApiReturnTeamRequestListFromFile(fileName: String) {
        dispatcher.handleRequest(
            { it.path!!.endsWith("/requests/dashboard") },
            { MockResponse().setBody(AssetsReader.readFromAssets(fileName)) }
        )
    }

    fun holidayApiReturnHolidaysBalanceFromFile(fileName: String) {
        dispatcher.handleRequest(
            { it.path!!.endsWith("/balances") },
            { MockResponse().setBody(AssetsReader.readFromAssets(fileName)) }
        )
    }

    fun holidayApiReturnSicknessCreatedRequestFromFile(fileName: String) {
        dispatcher.handleRequest(
            { it.path!!.endsWith("/holidays/requests/sicknesses") },
            { MockResponse().setBody(AssetsReader.readFromAssets(fileName)) }
        )
    }

    fun holidayApiReturnPublicHolidaysFromFile(fileName: String) {
        dispatcher.handleRequest(
            { it.path!!.endsWith("/public-holidays") },
            { MockResponse().setBody(AssetsReader.readFromAssets(fileName)) }
        )
    }
}

enum class ApiCall(
    val path: String,
    val logTag: String,
    val compareFunc: CompareFunc = CompareFunc.StartsWith
) {
    ATK_EQUIPMENT_LIST_CALL("/getEquipmentList", "equipment", CompareFunc.StartsWith),
    PEOPLE_API_PERSON_CALL("", "person details", CompareFunc.Matches(personProfileQuery)),
    PEOPLE_API_LIST_CALL("/api/v2/people/?page=0&size=0", "people", CompareFunc.Equals),

    HOLIDAYS_API_ANY_CALL("/holiday-api/api/v1/holidays", "holiday");

    sealed class CompareFunc {
        object Equals : CompareFunc()
        object StartsWith : CompareFunc()
        data class Matches(val regex: Regex) : CompareFunc()
    }
}