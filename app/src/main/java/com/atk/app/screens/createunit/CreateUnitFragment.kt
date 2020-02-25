package com.atk.app.screens.createunit

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.atk.app.BuildConfig
import com.atk.app.R
import com.atk.app.core.SendScreenshotUseCase
import com.atk.app.core.base.BaseDataBindingFragment
import com.atk.app.core.base.UiState
import com.atk.app.core.base.toolbar.ToolbarConfiguration
import com.atk.app.core.base.toolbar.configuration
import com.atk.app.core.repository.ResponseResult
import com.atk.app.core.repository.model.Company
import com.atk.app.core.repository.model.Equipment
import com.atk.app.core.repository.model.SimCard
import com.atk.app.core.repository.wialon.WialonResponse
import com.atk.app.core.repository.wialon.WialonThrowable
import com.atk.app.core.utils.andReturn
import com.atk.app.databinding.CreateUnitFragmentBinding
import com.atk.app.databinding.CustomEditRowBinding
import com.atk.app.databinding.CustomFieldItemViewBinding
import com.atk.app.screens.SharedViewModel
import com.atk.app.screens.login.LoginViewModel
import com.atk.app.screens.qrcode.EventOrError
import dagger.hilt.android.AndroidEntryPoint
import java.net.UnknownHostException

@AndroidEntryPoint
class CreateUnitFragment :
    BaseDataBindingFragment<CreateUnitFragmentBinding>(R.layout.create_unit_fragment) {

    override val viewModel: CreateUnitViewModel by viewModels()
    private val sharedVM: SharedViewModel by activityViewModels()
    private val loginViewModel: LoginViewModel by activityViewModels()

    private val CreateUnitFragmentBinding.toolbar: Toolbar
        get() = toolbarHolder.toolbar

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (perms.contains(Manifest.permission.CAMERA)) viewModel.cameraPermissionsGranted()
        viewModel.setInitPermission(
            hashMapOf(
                Manifest.permission.CAMERA to
                    (PermissionChecker.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.CAMERA
                    ) == PERMISSION_GRANTED),
                Manifest.permission.READ_EXTERNAL_STORAGE to
                    (PermissionChecker.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PERMISSION_GRANTED),
                Manifest.permission.WRITE_EXTERNAL_STORAGE to
                    (PermissionChecker.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PERMISSION_GRANTED)
            )
        )
        super.onPermissionsGranted(requestCode, perms)
    }

    lateinit var sendScreenshotUseCase: SendScreenshotUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sendScreenshotUseCase = SendScreenshotUseCase(requireActivity())
        if (savedInstanceState == null) {
            viewModel.setInitPermission(
                hashMapOf(
                    Manifest.permission.CAMERA to
                        (PermissionChecker.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.CAMERA
                        ) == PERMISSION_GRANTED),
                    Manifest.permission.READ_EXTERNAL_STORAGE to
                        (PermissionChecker.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PERMISSION_GRANTED),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE to
                        (PermissionChecker.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) == PERMISSION_GRANTED)
                )
            )
        }
    }

    override fun CreateUnitFragmentBinding.onViewCreated(view: View, bundle: Bundle?) {
        vm = viewModel
        toolbar.configToolbar()
        configGeneralViewState()
        configNavigationOptions()

        configCompany()

        configHwTypes()
        configEquipment()
        configEquipmentAction()

        configIccid()
        configPhone()

        configName()

        configDut()
    }

    private fun Toolbar.configToolbar() {
        configuration = ToolbarConfiguration(

            homeIconResId = R.drawable.ic_atk_logo,
            menuResId = R.menu.init_menu,
            menuItemClick = { menuItem ->
                when (menuItem.itemId) {
                    R.id.clearFieldsItem -> viewModel.refreshView() andReturn true
                    R.id.history -> {
                        findNavController().navigate(CreateUnitFragmentDirections.actionCreateUnitFragmentToHistoryFragment())
                        true
                    }
                    else -> false
                }
            },
            applyOnCreate = hashMapOf(
                R.id.hostingState to {
                    CustomEditRowBinding.bind(it.actionView).apply {
                        root.setOnClickListener { loginViewModel.hostingWialonLogin() }
                        text.setText(R.string.hosting_state)
                    }
                },
                R.id.localState to {
                    CustomEditRowBinding.bind(it.actionView).apply {
                        root.setOnClickListener { loginViewModel.localWialonLogin() }
                        text.setText(R.string.local_state)
                    }
                },
                R.id.atkState to {
                    CustomEditRowBinding.bind(it.actionView).apply {
                        root.setOnClickListener { viewModel.refreshAtkValues() }
                        text.setText(R.string.atk_base)
                    }
                },
                R.id.history to { it.isVisible = BuildConfig.DEBUG }
            )
        )
    }

    private fun CreateUnitFragmentBinding.configEquipmentAction() {
        val values = resources.getStringArray(R.array.equipment_actions)
        viewModel.selectedEquipmentAction(values[0])

        equipmentAction.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) = viewModel.selectedEquipmentAction(values[position])
        }
    }

    private fun CreateUnitFragmentBinding.configEquipment() {
        viewModel.equipmentListData.observeInViewLiveCycle {
            imeiProgress.visibleOrGone(it)
            if (it is ResponseResult.Success) {
                imei.setAdapter(
                    ContainsArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        it.data
                    )
                )
            }
        }
        imei.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            viewModel.getSelectedEquipmentFromWialon(parent.getItemAtPosition(position) as Equipment)
        }
    }

    private fun CreateUnitFragmentBinding.configHwTypes() {
        viewModel.hwTypes.observeInViewLiveCycle {
            hwTypesProgress.visibleOrGone(it)
            when (it) {
                is ResponseResult.Success -> hwTypeView.setAdapter(
                    ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        it.data.map { types -> types.name })
                )
                is ResponseResult.Failure -> showAlert(it.error)
            }
        }

        hwTypeView.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, start: Int, count: Int, after: Int) {
                val index = p0?.indexOfLast { it == ' ' } ?: -1
                if (count - after == 1 && count > 1) {
                    hwTypeView.setText(
                        if (index == -1) "" else p0.toString().substring(0, index)
                    )
                    Selection.setSelection(hwTypeView.text, hwTypeView.text.length)
                }
            }

            override fun onTextChanged(p0: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun CreateUnitFragmentBinding.configPhone() {
        phone.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val newText = p0.toString()
                if (!newText.startsWith("+") && newText.isNotEmpty()) {
                    phone.setText("+$newText")
                    Selection.setSelection(phone.text, phone.text.length)
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, after: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, before: Int, count: Int) {
                if (before > count && before - count == 1) {
                    viewModel.removePhoneNumber()
                }
            }
        })
    }

    private fun CreateUnitFragmentBinding.configName() {
        name.addTextChangedListener(object : TextWatcher {
            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(p0: Editable?) {
                val newText = p0.toString()
                if (!newText.startsWith("!!!") && newText.isNotEmpty()) {
                    name.setText("!!!" + p0.toString())
                    Selection.setSelection(name.text, name.text.length)
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, after: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, before: Int, count: Int) {}
        })
    }

    private fun CreateUnitFragmentBinding.configGeneralViewState() {
        viewModel.sendScreenshotData.observeInViewLiveCycle { event ->
            event.getContentIfNotHandled()?.let {
                sendScreenshotUseCase.sendScreenshotToTelegram(it)
                viewModel.refreshView()
            }
        }
        createUnit.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Создание объекта")
                .setMessage("Вы действительно хотите создать объект?")
                .setNeutralButton("Отмена") { dialog, _ -> dialog.dismiss() }
                .setPositiveButton("Создать") { dialog, _ ->
                    dialog.dismiss()
                    viewModel.createUnitTask()
                }
                .setCancelable(false)
                .setIcon(R.drawable.ic_add_black)
                .show().setCanceledOnTouchOutside(false)
        }
        viewModel.uiState.observeInViewLiveCycle {
            progressBar.visibility = View.GONE
            when {
                it is UiState.Loading -> progressBar.visibility = View.VISIBLE
                it is UiState.Error && it.throwable is WialonThrowable && it.throwable.value != WialonResponse.NO_ERROR -> showAlert(
                    it.throwable
                )
                it is UiState.Error && it.throwable is UnknownHostException -> showAlert(it.throwable)
            }
        }
        viewModel.isClear.observeInViewLiveCycle {
            toolbar.menu.findItem(R.id.clearFieldsItem).isVisible = !it
        }
        loginViewModel.wialonHostingLogin.observeInViewLiveCycle {
            val itemBinding =
                CustomEditRowBinding.bind(toolbar.menu.findItem(R.id.hostingState).actionView)
            itemBinding.stateLoading.visibility = View.GONE
            itemBinding.stateIcon.visibility = View.VISIBLE

            when (it) {
                is UiState.Error -> itemBinding.stateIcon.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_cross)
                )
                is UiState.Complete -> itemBinding.stateIcon.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_done)
                )
                UiState.Loading -> {
                    itemBinding.stateLoading.visibility = View.VISIBLE
                    itemBinding.stateIcon.visibility = View.GONE
                }
            }
        }

        loginViewModel.wialonLocalLogin.observeInViewLiveCycle {
            val itemBinding =
                CustomEditRowBinding.bind(toolbar.menu.findItem(R.id.localState).actionView)
            itemBinding.stateLoading.visibility = View.GONE
            itemBinding.stateIcon.visibility = View.VISIBLE
            when (it) {
                is UiState.Error -> {
                    itemBinding.stateIcon.setImageDrawable(
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_cross)
                    )
                    showAlert(it.throwable)
                }
                is UiState.Complete -> itemBinding.stateIcon.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_done)
                )
                UiState.Loading -> {
                    itemBinding.stateLoading.visibility = View.VISIBLE
                    itemBinding.stateIcon.visibility = View.GONE
                }
            }
        }
        viewModel.atkState.observeInViewLiveCycle {
            val itemBinding =
                CustomEditRowBinding.bind(toolbar.menu.findItem(R.id.atkState).actionView)
            itemBinding.stateLoading.visibility = View.GONE
            itemBinding.stateIcon.visibility = View.VISIBLE
            when (it) {
                is UiState.Error -> {
                    itemBinding.stateIcon.setImageDrawable(
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_cross)
                    )
                    showAlert(it.throwable)
                }
                is UiState.Complete -> itemBinding.stateIcon.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_done)
                )
                UiState.Loading -> {
                    itemBinding.stateLoading.visibility = View.VISIBLE
                    itemBinding.stateIcon.visibility = View.GONE
                }
            }
        }
    }

    private fun CreateUnitFragmentBinding.configIccid() {
        iccid.apply {
            onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
                val simCard = parent.getItemAtPosition(position) as SimCard
                viewModel.selectedSimCard(simCard)
            }

            addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(p0: Editable?) {}

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, start: Int, before: Int, count: Int) {
                    if (before > count && before - count == 1) {
                        viewModel.removeIccidNumber()
                    }
                }
            })
        }
        viewModel.simCardsListData.observeInViewLiveCycle {
            iccidProgress.visibleOrGone(it)
            if (it is ResponseResult.Success) {
                iccid.setAdapter(
                    ContainsArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        it.data
                    )
                )
            }
        }
    }

    private fun CreateUnitFragmentBinding.configCompany() {
        viewModel.companiesData.observeInViewLiveCycle {
            companyProgress.visibleOrGone(it)
            when (it) {
                is ResponseResult.Success -> {
                    companies.setAdapter(
                        ContainsArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_dropdown_item_1line,
                            it.data
                        )
                    )
                }
            }
        }
        companies.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                val company = parent.getItemAtPosition(position) as Company
                viewModel.selectedCompany(company)
            }
    }

    private fun configNavigationOptions() {
        viewModel.navigateToLogin.observeInViewLiveCycle { event ->
            event.getContentIfNotHandled()?.let {
                val navigationDirections =
                    CreateUnitFragmentDirections.actionCreateUnitFragmentToLoginFragment()
                findNavController().navigate(navigationDirections)
            }
        }
        sharedVM.capturedQrCode.observeInViewLiveCycle { event ->
            event.getContentIfNotHandled()?.let {
                if (viewModel.isImeiQr) {
                    viewModel.unitImei.postValue(it)
                } else {
                    viewModel.unitIccid.postValue(it)
                    viewModel.selectedIccid(it)
                }
            }
        }

        viewModel.permissionRequest.observeInViewLiveCycle {
            it.getContentIfNotHandled()?.let { listOfPermRequest ->
                requestPermissions(listOfPermRequest, REQUEST_PERMISSIONS)
            }
        }

        viewModel.navigateToQrCode.observeInViewLiveCycle { event ->
            event.getContentIfNotHandled()?.let {
                when (it) {
                    is EventOrError.Event -> {
                        val navigationDirections =
                            CreateUnitFragmentDirections.actionCreateUnitFragmentToQrCodeFragment()
                        findNavController().navigate(navigationDirections)
                    }
                    is EventOrError.Error -> showAlert(it.text)
                }
            }
        }
    }

    private fun CreateUnitFragmentBinding.configDut() {
        addDut.setOnClickListener {
            val data = Bundle().apply {
                putParcelableArray(
                    "DUTS",
                    viewModel.dutAdded.value.orEmpty().toTypedArray()
                )
            }
            AlertDialog.Builder(requireContext())
                .setTitle("Добавление доп оборудования")
                .setMessage("Владелец оборудования?")
                .setNeutralButton("Отмена") { dialog, _ -> dialog.dismiss() }
                .setNegativeButton("ATK") { dialog, _ ->
                    dialog.dismiss()
                    findNavController().navigate(
                        R.id.action_createUnitFragment_to_addOurDutFragment,
                        data
                    )
                }
                .setPositiveButton("Клиент") { dialog, _ ->
                    dialog.dismiss()
                    findNavController().navigate(
                        R.id.action_createUnitFragment_to_addClientDutFragment,
                        data
                    )
                }
                .setIcon(R.drawable.ic_add_black)
                .show()
        }

        viewModel.dutAdded.observeInViewLiveCycle {
            dutHolder.removeAllViews()
            it.forEach { dut ->
                CustomFieldItemViewBinding.inflate(layoutInflater, dutHolder, true).apply {
                    dutName.text = dut.name
                    dutImei.text = dut.value
                    deleteDutButton.setOnClickListener { viewModel.deleteDut(dut) }
                }
            }
        }

        sharedVM.selectedDut.observeInViewLiveCycle { event ->
            event.getContentIfNotHandled()?.let { viewModel.addDut(it) }
        }
    }

    companion object {
        const val REQUEST_PERMISSIONS = 0
    }
}

fun <T> View.visibleOrGone(responseResult: ResponseResult<T>) {
    visibility = if (responseResult is ResponseResult.Pending) View.VISIBLE else View.GONE
}