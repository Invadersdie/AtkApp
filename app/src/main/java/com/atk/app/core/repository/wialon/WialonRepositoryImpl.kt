package com.atk.app.core.repository.wialon

import com.atk.app.R
import com.atk.app.core.dagger.HOSTING
import com.atk.app.core.dagger.LOCAL
import com.atk.app.core.repository.ResponseResult
import com.atk.app.core.repository.internet.api.GroupUpdateResponse
import com.atk.app.core.repository.internet.api.WialonApi
import com.atk.app.core.repository.internet.data.model.recieve.*
import com.atk.app.core.repository.internet.data.model.send.*
import com.atk.app.core.repository.internet.wialon.authorization.AuthorizationRepository
import com.atk.app.core.repository.safeWialonApiCall
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class WialonRepositoryImpl @Inject constructor(
    private val gson: Gson,
    @Named(LOCAL) private val wialonLocalApi: WialonApi,
    @Named(HOSTING) private val wialonHostingApi: WialonApi,
    private val authorizationRepository: AuthorizationRepository
) : WialonRepository {
    private fun getCurrentUser(isLocal: Boolean) = if (isLocal) localUser else hostingUser

    private fun getWialonApi(isLocal: Boolean): WialonApi =
        if (isLocal) wialonLocalApi else wialonHostingApi

    private var hostingUser: Long = 0
    private var localUser: Long = 0

    fun isLoggedIn(): Boolean =
        authorizationRepository.hostingSid.isNotEmpty() && authorizationRepository.localSid.isNotEmpty()

    override suspend fun trackObject(
        wialonId: Long,
        isLocal: Boolean
    ): ResponseResult<SearchResult<MessageItem>> =
        safeWialonApiCall {
            val searchParams = SearchParams(
                SearchSpec(
                    itemsType = "avl_unit",
                    propName = "sys_id",
                    propValueMask = wialonId.toString(),
                    sortType = "sys_id"
                ), 1, Flags.MESSAGES, 0, 0
            )
            getWialonApi(isLocal).searchItemMessagesById(gson.toJson(searchParams))
        }

    override suspend fun searchObjectsByName(name: String, isLocal: Boolean, from: Int, to: Int) =
        safeWialonApiCall {
            val searchParams = SearchParams(
                SearchSpec(
                    itemsType = "avl_unit",
                    propName = "sys_name",
                    propValueMask = name,
                    sortType = "sys_id"
                ), 1, Flags.BASE, from, to
            )
            getWialonApi(isLocal).searchItems(gson.toJson(searchParams))
        }

    override suspend fun checkUnitExists(
        id: Long,
        isLocal: Boolean,
        flags: Long
    ): ResponseResult<Unit> {
        val searchById = SearchById(id, flags)
        return when (val responseResult =
            safeWialonApiCall { getWialonApi(isLocal).searchItemById(gson.toJson(searchById)) }) {
            is ResponseResult.Success -> if (responseResult.data.item.nm.isNotBlank()) {
                ResponseResult.Success(Unit)
            } else {
                ResponseResult.Failure(WialonThrowable(WialonResponse.UNIT_CREATE_ERROR_UNIT_EXISTS))
            }
            is ResponseResult.Failure -> responseResult
            else -> throw IllegalStateException()
        }
    }

    override suspend fun createUnit(
        unitName: String,
        unitTypeId: Long,
        dataFlags: Long,
        isLocal: Boolean
    ): ResponseResult<UnitItem> {
        val jsonCreatedUnit = CreateUnit(unitName, getCurrentUser(isLocal), unitTypeId, dataFlags)
        return safeWialonApiCall { getWialonApi(isLocal).createUnit(gson.toJson(jsonCreatedUnit)) }
    }

    override suspend fun loginHosting(loginParams: LoginParams) =
        safeWialonApiCall { wialonHostingApi.login(gson.toJson(loginParams)) }.map {
            authorizationRepository.hostingSid = it.eid
            hostingUser = it.user.id
            ResponseResult.Success(Unit)
        }

    override suspend fun loginLocal(loginParams: LoginParams) =
        safeWialonApiCall { wialonLocalApi.login(gson.toJson(loginParams)) }.map {
            authorizationRepository.localSid = it.eid
            localUser = it.user.id
            ResponseResult.Success(Unit)
        }

    override suspend fun updateImei(wialonItem: WialonItem, unitImei: String, isLocal: Boolean) =
        safeWialonApiCall {
            getWialonApi(isLocal).updateDeviceType(
                gson.toJson(UpdateDeviceType(wialonItem.id, wialonItem.hw, unitImei))
            )
        }

    override suspend fun updatePhoneNumber(id: Long, phone: String, isLocal: Boolean) =
        safeWialonApiCall {
            getWialonApi(isLocal).updatePhoneNumber(gson.toJson(UpdatePhoneNumber(id, phone)))
        }

    override suspend fun createSensor(id: Long, sensor: Sensor, isLocal: Boolean) =
        safeWialonApiCall {
            getWialonApi(isLocal).updateSensors(
                gson.toJson(
                    sensor.copy(
                        itemId = id,
                        id = 0,
                        callMode = "create",
                        unlink = 1
                    )
                )
            )
        }

    override suspend fun updateGroupItems(
        updateGroupItems: UpdateGroupItems,
        isLocal: Boolean
    ): ResponseResult<GroupUpdateResponse> = safeWialonApiCall {
        getWialonApi(isLocal).updateGroupItems(gson.toJson(updateGroupItems))
    }

    override suspend fun getGroupItemList(
        group: String,
        isLocal: Boolean
    ): ResponseResult<Pair<Long, List<Long>>> =
        withContext(IO) {
            val responseResult = safeWialonApiCall {
                val searchParams = SearchParams(
                    SearchSpec(
                        itemsType = "avl_unit_group",
                        propName = "sys_name",
                        propValueMask = group,
                        sortType = "sys_id"
                    ), 1, Flags.BASE, 0, 0
                )
                getWialonApi(isLocal).searchGroup(gson.toJson(searchParams))
            }
            return@withContext responseResult.map { Pair(it.items[0].id, it.items[0].listOfItems) }
        }

    override suspend fun updateCustomField(customField: CustomField, isLocal: Boolean) =
        safeWialonApiCall {
            val data = gson.toJson(customField)
            if (isLocal) {
                wialonLocalApi.updateAdminField(data)
            } else {
                wialonHostingApi.updateCustomField(data)
            }
        }

    override suspend fun getHwTypesHosting(getHwTypes: GetHwTypes) =
        safeWialonApiCall { wialonHostingApi.getHwTypes(gson.toJson(getHwTypes)) }

    override suspend fun getHwTypesLocal(getHwTypes: GetHwTypes) =
        safeWialonApiCall { wialonLocalApi.getHwTypes(gson.toJson(getHwTypes)) }
}

/**
 * spec	условия поиска
 * itemsType	тип искомых элементов (см. список ниже), если оставить пустым, то поиск будет осуществляться по все типам
 * propName	имя свойства, по которому будет осуществляться поиск (см. список возможных свойств ниже)
 * propValueMask	значение свойства: может быть использован знак «*»
 * sortType	имя свойства, по которому будет осуществляться сортировка ответа
 * propType	тип свойства (см. список типов ниже)
 * force	0 - если такой поиск уже запрашивался, то вернет полученный результат, 1 - будет искать заново
 * flags	флаги видимости для возвращаемого результата (Значение данного параметра зависит от типа элемента, который вы хотите найти. Форматы всех элементов, а так же их флаги описаны в разделе Форматы данных.)
 * from	индекс, начиная с которого возвращать элементы результирующего списка (для нового поиска используйте значение 0)
 * to	индекс последнего возвращаемого элемента (если 0, то вернет все элементы, начиная с указанного в параметре «from»)
 * or_logic	флаг «ИЛИ»-логики для propName-поля (см. ниже)
 */

data class SearchParams(
    val spec: SearchSpec,
    val force: Int,
    val flags: Long,
    val from: Int,
    val to: Int,
    val or_logic: Boolean? = null
)

object Flags {
    const val BASE: Long = 1

    const val ADDITIONAL_PROPERTIES: Long = 256
    const val LAST_MESSAGE_AND_LOCATION: Long = 1024
    const val SENSORS: Long = 4096
    const val MESSAGES: Long = 1048576
}

data class WialonThrowable(val value: WialonResponse) : Throwable()

enum class WialonResponse(val code: Long, val text: Int = 0) {
    NO_ERROR(-1, R.string.no_error),
    SUCCESS(0, R.string.success),
    INVALID_SESSION(1, R.string.invalid_session),
    WRONG_SERVICE_NAME(2, R.string.wrong_service_name),
    WRONG_RESULT(3, R.string.wrong_result),
    WRONG_INPUT(4, R.string.wrong_input),
    ERROR_EXUCUTE_REQUEST(5, R.string.error_exucute_request),
    UNKNOWN_WIALON_ERROR(6, R.string.unknown_wialon_error),
    ACCESS_DENIED(7, R.string.access_denied),
    WRONG_USER_NAME_OR_PASSWORD(8, R.string.wrong_user_name_or_password),
    AUTHORIZATION_SERVER_UNAVAILABLE(9, R.string.authorization_server_unavailable),
    LIMIT_ONE_MOMENT_REQUESTS(10, R.string.limit_one_moment_requests),
    ERROR_PERFORM_DROP_PASSWORD(11, R.string.error_perform_drop_password),
    BILLING_ERROR(14, R.string.billing_error),
    NO_MESSAGES_FOR_SELECTED_INTERVAL(1001, R.string.no_messages_for_selected_interval),
    SUCH_ELEMENT_ALREADY_EXISTS_BILLING_LIMITATIONS(
        1002,
        R.string.such_element_already_exists_billing_limitations
    ),
    ONLY_ONE_REQUEST_ALLOWED(1003, R.string.only_one_request_allowed),
    MESSAGES_LIMIT_EXCEED(1004, R.string.messages_limit_exceed),
    TIMEOUT(1005, R.string.timeout),
    LOGIN_ATTEMPTS_LIMIT(1006, R.string.login_attempts_limit),
    SESSION_EXPIRED(1011, R.string.session_expired),
    CURRENT_USER_CANT_BE_USED(2014, R.string.current_user_cant_be_used),
    DELETE_FORBIDEN_USED_IN_ANOTHER_OBJECT(
        2015,
        R.string.delete_forbiden_used_in_another_object
    ),
    UNKNOWN_ANDROID_ERROR(900, R.string.unknown_android_error),
    JSON_PARSER_ERROR(901, R.string.json_parser_error),
    UNIT_CREATE_ERROR_NO_COMPANY(990, R.string.unit_create_not_company),
    UNIT_CREATE_ERROR_NO_NAME(991, R.string.unit_create_not_name),
    UNIT_CREATE_ERROR_NO_IMEI(992, R.string.unit_create_not_imei),
    UNIT_CREATE_ERROR_NO_TYPE(993, R.string.unit_create_not_type),
    UNIT_CREATE_ERROR_NO_PHONE(994, R.string.unit_create_not_phone),
    UNIT_CREATE_ERROR_UNIT_EXISTS(899, R.string.unit_create_unit_exists);

    companion object {
        fun getWialonResponse(errorCode: Int) =
            when (errorCode) {
                -1 -> NO_ERROR
                0 -> SUCCESS
                1 -> INVALID_SESSION
                2 -> WRONG_SERVICE_NAME
                3 -> WRONG_RESULT
                4 -> WRONG_INPUT
                5 -> ERROR_EXUCUTE_REQUEST
                6 -> UNKNOWN_WIALON_ERROR
                7 -> ACCESS_DENIED
                8 -> WRONG_USER_NAME_OR_PASSWORD
                9 -> AUTHORIZATION_SERVER_UNAVAILABLE
                10 -> LIMIT_ONE_MOMENT_REQUESTS
                11 -> ERROR_PERFORM_DROP_PASSWORD
                14 -> BILLING_ERROR
                1001 -> NO_MESSAGES_FOR_SELECTED_INTERVAL
                1002 -> SUCH_ELEMENT_ALREADY_EXISTS_BILLING_LIMITATIONS
                1003 -> ONLY_ONE_REQUEST_ALLOWED
                1004 -> MESSAGES_LIMIT_EXCEED
                1005 -> TIMEOUT
                1006 -> LOGIN_ATTEMPTS_LIMIT
                1011 -> SESSION_EXPIRED
                2014 -> CURRENT_USER_CANT_BE_USED
                2015 -> DELETE_FORBIDEN_USED_IN_ANOTHER_OBJECT
                900 -> UNKNOWN_ANDROID_ERROR
                901 -> JSON_PARSER_ERROR
                990 -> UNIT_CREATE_ERROR_NO_COMPANY
                991 -> UNIT_CREATE_ERROR_NO_NAME
                992 -> UNIT_CREATE_ERROR_NO_IMEI
                993 -> UNIT_CREATE_ERROR_NO_TYPE
                994 -> UNIT_CREATE_ERROR_NO_PHONE
                899 -> UNIT_CREATE_ERROR_UNIT_EXISTS

                else -> UNKNOWN_WIALON_ERROR
            }
    }
}