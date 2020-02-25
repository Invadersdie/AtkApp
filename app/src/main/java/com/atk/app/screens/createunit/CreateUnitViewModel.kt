package com.atk.app.screens.createunit

import android.Manifest
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.atk.app.core.base.AlertDataRepeatable
import com.atk.app.core.base.BaseViewModel
import com.atk.app.core.base.DialogData
import com.atk.app.core.base.UiState
import com.atk.app.core.models.ExecuteOnce
import com.atk.app.core.repository.AtkRepository
import com.atk.app.core.repository.DatabaseRepository
import com.atk.app.core.repository.ResponseResult
import com.atk.app.core.repository.database.entity.CreatedObject
import com.atk.app.core.repository.internet.data.model.recieve.HwTypes
import com.atk.app.core.repository.internet.data.model.send.GetHwTypes
import com.atk.app.core.repository.model.Company
import com.atk.app.core.repository.model.Dut
import com.atk.app.core.repository.model.Equipment
import com.atk.app.core.repository.model.SimCard
import com.atk.app.core.repository.wialon.WialonRepositoryImpl
import com.atk.app.core.repository.wialon.WialonResponse
import com.atk.app.core.repository.wialon.WialonThrowable
import com.atk.app.core.usecases.atk.UpdateAtkStateUseCase
import com.atk.app.core.usecases.wialon.CreateUnitUseCase
import com.atk.app.screens.qrcode.EventOrError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CreateUnitViewModel @Inject constructor(
    private val wialonRepository: WialonRepositoryImpl,
    private val createUnitUseCase: CreateUnitUseCase,
    private val updateAtkStateUseCase: UpdateAtkStateUseCase,
    private val atkRepository: AtkRepository,
    private val databaseRepository: DatabaseRepository
) : BaseViewModel() {

    var isImeiQr = true

    val navigateToQrCode = MutableLiveData<ExecuteOnce<EventOrError<Boolean>>>()

    val navigateToLogin = MutableLiveData<ExecuteOnce<Boolean>>()

    private val _uiState = MutableLiveData<UiState>(UiState.Complete)
    val uiState: LiveData<UiState> get() = _uiState

    val simCardsListData = MutableLiveData<ResponseResult<List<SimCard>>>()
    val equipmentListData = MutableLiveData<ResponseResult<List<Equipment>>>()
    val companiesData = MutableLiveData<ResponseResult<List<Company>>>()

    val sendScreenshotData = MutableLiveData<ExecuteOnce<CreatedObject>>()

    private val _dutAdded = MutableLiveData<List<Dut>>(emptyList())
    val dutAdded: LiveData<List<Dut>> get() = _dutAdded

    private var selectedEquipment: Equipment? = null
    private var selectedSimCard: SimCard? = null
    private var selectedCompany: Company? = null
    private var selectedAction: String = ""

    val unitImei = MutableLiveData("")
    val unitType = MutableLiveData("")
    val unitIccid = MutableLiveData("")
    val companyLiveData = MutableLiveData("")
    val unitPhone = MutableLiveData("")
    val phoneOperator = MutableLiveData("")
    val unitName = MutableLiveData("")

    val isClear = MediatorLiveData<Boolean>().apply {
        var name = ""
        var type = ""
        var imei = ""
        var phone = ""
        var iccid = ""
        var company = ""
        var dutList = 0
        fun post() =
            postValue(type.isEmpty() && name.isEmpty() && company.isEmpty() && imei.isEmpty() && phone.isEmpty() && iccid.isEmpty() && dutList == 0)
        addSource(unitImei) { imei = it; post() }
        addSource(companyLiveData) { company = it; post() }
        addSource(unitType) { type = it; post() }
        addSource(unitIccid) { iccid = it; post() }
        addSource(unitPhone) { phone = it; post() }
        addSource(unitName) { name = it; post() }
        addSource(dutAdded) { dutList = it.size; post() }
    }
    private val hwTypesLocal = MutableLiveData<ResponseResult<List<HwTypes>>>()
    private val hwTypesHosting = MutableLiveData<ResponseResult<List<HwTypes>>>()

    val hwTypes = MediatorLiveData<ResponseResult<List<HwTypes>>>().apply {
        addSource(hwTypesLocal) { postValue(it) }
    }

    init {
        permissionRequest.postValue(
            ExecuteOnce(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        )
//        if (!wialonRepository.isLoggedIn()) navigateToLogin.value = ExecuteOnce(true)
        getInitValues()
    }

    private fun sendScreenshot(createdObject: CreatedObject) {
        sendScreenshotData.value = ExecuteOnce(createdObject)
    }

    private fun getInitValues() {
        viewModelScope.launch(exceptionHandler) {
            launch { getHwTypesLocal() }
            launch { getHwTypesHosting() }
            launch { getDataBaseData() }
        }
    }

    fun refreshAtkValues() {
        viewModelScope.launch(exceptionHandler) {
            getDataBaseData()
        }
    }

    private suspend fun getDataBaseData() {
        coroutineScope {
            launch { getSimValues() }
            launch { getEquipValues() }
            launch { getCompanies() }
        }
    }

    val atkState = MediatorLiveData<UiState>().apply {
        var simCard: UiState = UiState.Loading
        var equipment: UiState = UiState.Loading
        var companies: UiState = UiState.Loading
        fun post() {
            value = when {
                simCard is Error -> simCard
                equipment is Error -> equipment
                companies is Error -> companies
                simCard is UiState.Loading || equipment is UiState.Loading || companies is UiState.Loading -> UiState.Loading
                simCard is UiState.Complete && equipment is UiState.Complete && companies is UiState.Complete -> UiState.Complete
                else -> UiState.Loading
            }
        }

        addSource(companiesData) {
            companies = companiesData.value?.toUiState() ?: UiState.Loading; post()
        }
        addSource(equipmentListData) {
            equipment = equipmentListData.value?.toUiState() ?: UiState.Loading; post()
        }
        addSource(simCardsListData) {
            simCard = simCardsListData.value?.toUiState() ?: UiState.Loading; post()
        }
    }

    private suspend fun getCompanies() = withContext(Main) {
        companiesData.value = ResponseResult.Pending
        companiesData.value = atkRepository.getCompanies()
    }

    private suspend fun getEquipValues() = withContext(Main) {
        equipmentListData.value = ResponseResult.Pending
        equipmentListData.value = atkRepository.getEquipment()
    }

    private suspend fun getSimValues() = withContext(Main) {
        simCardsListData.value = ResponseResult.Pending
        simCardsListData.value = atkRepository.getSimCards()
    }

    private suspend fun getHwTypesHosting() = withContext(Main) {
        hwTypesHosting.value = ResponseResult.Pending
        when (val response = wialonRepository.getHwTypesHosting(GetHwTypes())) {
            is ResponseResult.Success -> hwTypesHosting.value =
                ResponseResult.Success(response.data.hwTypes)
            is ResponseResult.Failure -> hwTypesHosting.value = response
        }
    }

    private suspend fun getHwTypesLocal() = withContext(Main) {
        hwTypesLocal.value = ResponseResult.Pending
        when (val response = wialonRepository.getHwTypesLocal(GetHwTypes())) {
            is ResponseResult.Success -> hwTypesLocal.value =
                ResponseResult.Success(response.data.hwTypes)
            is ResponseResult.Failure -> hwTypesLocal.value = response
        }
    }

    private val isLocal get() = selectedCompany?.server == "Локал"

    fun createUnitTask(retry: Boolean = false) {
        val unitTypeId =
            hwTypes.value?.getValueOrNull()?.find { it.name == unitType.value }?.id?.toLong()
                ?: -1L
        val unitName = unitName.value
        val unitImei = unitImei.value
        val unitPhone = unitPhone.value.orEmpty()
        val dutList = dutAdded.value.orEmpty()
        val company = selectedCompany
        when {
            unitTypeId == -1L -> _uiState.value =
                UiState.Error(WialonThrowable(WialonResponse.UNIT_CREATE_ERROR_NO_TYPE))
            unitName.isNullOrEmpty() -> _uiState.value =
                UiState.Error(WialonThrowable(WialonResponse.UNIT_CREATE_ERROR_NO_NAME))
            unitImei.isNullOrEmpty() -> _uiState.value =
                UiState.Error(WialonThrowable(WialonResponse.UNIT_CREATE_ERROR_NO_IMEI))
            company == null -> _uiState.value =
                UiState.Error(WialonThrowable(WialonResponse.UNIT_CREATE_ERROR_NO_COMPANY))

            else -> viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
                dialog.value = ExecuteOnce(
                    DialogData(
                        "Произошла ошибка",
                        throwable.message.orEmpty() + "\n" + "Повторить?",
                        { },
                        { createUnitTask(true) })
                )
                _uiState.value = UiState.Complete
            }) {
                _uiState.value = UiState.Loading

                val equipment =
                    selectedEquipment ?: Equipment(-1, unitImei, unitType.value.orEmpty())
                val simCard =
                    selectedSimCard ?: SimCard(-1, unitIccid.value.orEmpty(), unitPhone, "")

                val createResult = createUnitUseCase.create(
                    isLocal,
                    unitName,
                    unitTypeId,
                    unitPhone,
                    company.name,
                    selectedSimCard,
                    equipment,
                    unitImei,
                    dutList,
                    retry
                )

                when (createResult) {
                    is ResponseResult.Success -> {
                        val wialonId = createResult.data
                        sendToast("Объект успешно создан")
                        val createdObject = CreatedObject(
                            uid = 0,
                            wialonId = wialonId,
                            unitName = unitName,
                            done = true,
                            isLocal = isLocal,
                            unitImei = unitImei,
                            typeId = unitTypeId,
                            simCard = simCard,
                            company = company,
                            equipment = equipment,
                            action = selectedAction,
                            dutList = dutList
                        )
                        databaseRepository.insertCreatedObject(createdObject)

                        updateAtkStateUseCase.update(
                            selectedEquipment,
                            selectedSimCard,
                            selectedCompany,
                            selectedAction,
                            dutList
                        )
                        sendToast("Статус обновлен")

//                        if (track) {
//                            databaseRepository.addTrackable(TrackableObject(0, wialonId, isLocal))
//                            turnOnTracker()
//                        }

                        if (storageWritePermission && storageReadPermission) {
                            showDialogCo(
                                DialogData(
                                    "Скриншот",
                                    "Отправить скриншот экрана в телеграмм?",
                                    ::refreshView
                                ) {
                                    sendScreenshot(createdObject)
                                }
                            )
                        } else {
                            showAlertCo(Throwable("Невозможно отправить скриншот без доступа к памяти"))
                        }
                    }
                    is ResponseResult.Failure -> {
                        databaseRepository.insertCreatedObject(
                            CreatedObject(
                                uid = 0,
                                done = false,
                                wialonId = -1,
                                isLocal = isLocal,
                                unitName = unitName,
                                unitImei = unitImei,
                                typeId = unitTypeId,
                                simCard = simCard,
                                company = company,
                                equipment = equipment,
                                action = selectedAction,
                                dutList = dutList
                            )
                        )

                        showAlertRepeatable(
                            AlertDataRepeatable(
                                "\nПовторить?",
                                createResult.error,
                                { },
                                { createUnitTask(true) })
                        )
                    }
                    else -> showAlertCo(Throwable("Unreachable state MainViewModel"))
                }
                _uiState.value = UiState.Complete
            }
        }
    }

//    private fun turnOnTracker() {
//        workManager.beginUniqueWork(
//            TRACK_WORKER,
//            ExistingWorkPolicy.REPLACE,
//            OneTimeWorkRequestBuilder<TrackAlarmCreatorWorker>().build()
//        ).enqueue()
//    }

    private var cameraPermissions = false
    private var storageWritePermission = false
    private var storageReadPermission = false

    fun setInitPermission(permission: HashMap<String, Boolean>) {
        cameraPermissions = permission[Manifest.permission.CAMERA] ?: (false)
        storageWritePermission = permission[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: (false)
        storageReadPermission = permission[Manifest.permission.READ_EXTERNAL_STORAGE] ?: (false)
    }

    fun cameraPermissionsGranted() {
        cameraPermissions = true
    }

    fun takeQrCode(isImei: Boolean) {
        if (cameraPermissions) {
            isImeiQr = isImei
            navigateToQrCode.postValue(ExecuteOnce(EventOrError.Event(true)))
        } else permissionRequest.postValue(
            ExecuteOnce(arrayOf(Manifest.permission.CAMERA))
        )
    }

    fun refreshView() {
        unitType.value = ""
        unitName.value = ""
        unitImei.value = ""
        unitPhone.value = ""
        unitIccid.value = ""
        companyLiveData.value = ""
        phoneOperator.value = ""
        selectedEquipment = null
        selectedSimCard = null
        selectedCompany = null
        clearDuts()
    }

    fun getSelectedEquipmentFromWialon(equipment: Equipment) {
        selectedEquipment = equipment
        viewModelScope.launch(exceptionHandler) {
            withContext(IO) {
                (hwTypes.value as? ResponseResult.Success)?.data?.find { hwTypes ->
                    if (equipment.type.contains("SMART S-2")) {
                        when {
                            equipment.type.contains("SMART S-24") -> hwTypes.name.contains("SMART S-24")
                            equipment.type.contains("SMART S-23") -> hwTypes.name.contains("SMART S-23")
                            else -> throw IllegalStateException()
                        }
                    } else {
                        equipment.type.split(" ").all { equipmentName ->
                            hwTypes.name.contains(equipmentName, true)
                        }
                    }
                }!!.let { unitType.postValue(it.name) }
            }
        }
    }

    fun selectedCompany(company: Company) {
        selectedCompany = company
        localHostingStateChanged(isLocal)
    }

    fun selectedIccid(iccid: String) {
        simCardsListData.value?.let { response ->
            if (response is ResponseResult.Success) {
                response.data.firstOrNull { it.iccid == iccid }?.let(::selectedSimCard)
            }
        }
    }

    fun selectedSimCard(simCard: SimCard?) {
        selectedSimCard = simCard
        phoneOperator.value = simCard?.simType.orEmpty()
        val phone = if (simCard?.phone == null) "" else "+${simCard.phone}"
        unitPhone.value = phone
    }

    fun removePhoneNumber() {
        selectedSimCard = null
        phoneOperator.value = ""
        unitIccid.value = ""
    }

    fun removeIccidNumber() {
        selectedSimCard = null
        unitPhone.value = ""
        phoneOperator.value = ""
    }

    fun selectedEquipmentAction(action: String) {
        selectedAction = action
    }

    private fun localHostingStateChanged(isLocal: Boolean) {
        if (isLocal) {
            hwTypes.removeSource(hwTypesHosting)
            try {
                hwTypes.addSource(hwTypesLocal) { hwTypes.postValue(it) }
            } catch (exp: IllegalArgumentException) {
            }
        } else {
            hwTypes.removeSource(hwTypesLocal)
            try {
                hwTypes.addSource(hwTypesHosting) { hwTypes.postValue(it) }
            } catch (exp: IllegalArgumentException) {
            }
        }
    }

    fun addDut(it: Dut) {
        _dutAdded.value = dutAdded.value.orEmpty().plus(it)
    }

    private fun clearDuts() {
        _dutAdded.value = emptyList()
    }

    fun deleteDut(dut: Dut) {
        _dutAdded.value = dutAdded.value!!.filter { it != dut }
    }
}