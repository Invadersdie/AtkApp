package com.atk.app.screens.login

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.atk.app.BuildConfig
import com.atk.app.R
import com.atk.app.core.base.BaseDataBindingFragment
import com.atk.app.core.base.UiState
import com.atk.app.core.repository.internet.utils.NetworkUtils
import com.atk.app.databinding.LoginFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseDataBindingFragment<LoginFragmentBinding>(R.layout.login_fragment) {

    override val viewModel: LoginViewModel by activityViewModels()

    @Inject
    lateinit var networkUtils: NetworkUtils

    private val isDeviceOnline get() = networkUtils.isNetworkAvailable()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, this.javaClass.name.toString() + "created")
        super.onCreate(savedInstanceState)
    }

    override fun LoginFragmentBinding.onViewCreated(view: View, bundle: Bundle?) {
        vm = viewModel

        viewModel.stateLogin.observeInViewLiveCycle {
            if (it) {
                findNavController().navigate(
                    if (BuildConfig.DEBUG) {
                        LoginFragmentDirections.actionLoginFragmentToSearchFragment()
                    } else {
                        LoginFragmentDirections.actionLoginFragmentToCreateUnitFragment()
                    }
                )
            }
        }
        viewModel.finishApp.observeInViewLiveCycle { event ->
            event.getContentIfNotHandled()?.let {
                if (it) {
                    Toast.makeText(requireContext(), R.string.no_access, Toast.LENGTH_LONG).show()
                    requireActivity().finish()
                }
            }
        }
        viewModel.atkLogin.observeInViewLiveCycle {
            atkLoginStatus.visibility = View.GONE
            atkLoginStatusImage.visibility = View.VISIBLE

            when (it) {
                is UiState.Error -> atkLoginStatusImage.setImageDrawable(
                    resources.getDrawable(R.drawable.ic_cross, null)
                )
                is UiState.Complete -> atkLoginStatusImage.setImageDrawable(
                    resources.getDrawable(R.drawable.ic_done, null)
                )
                UiState.Loading -> {
                    atkLoginStatus.visibility = View.VISIBLE
                    atkLoginStatusImage.visibility = View.GONE
                }
            }
        }

        viewModel.wialonLocalLogin.observeInViewLiveCycle {
            wialonLocalLoginStatusProgress.visibility = View.GONE
            wialonLocalLoginStatusImage.visibility = View.VISIBLE
            when (it) {
                is UiState.Error -> {
                    wialonLocalLoginStatusImage.setImageDrawable(
                        resources.getDrawable(R.drawable.ic_cross, null)
                    )
                    showAlert(it.throwable)
                }
                is UiState.Complete -> wialonLocalLoginStatusImage.setImageDrawable(
                    resources.getDrawable(R.drawable.ic_done, null)
                )
                UiState.Loading -> {
                    wialonLocalLoginStatusProgress.visibility = View.VISIBLE
                    wialonLocalLoginStatusImage.visibility = View.GONE
                }
            }
        }

        viewModel.wialonHostingLogin.observeInViewLiveCycle {
            wialonHostingLoginStatusProgress.visibility = View.GONE
            wialonHostingLoginStatusImage.visibility = View.VISIBLE
            when (it) {
                is UiState.Error -> {
                    wialonHostingLoginStatusImage.setImageDrawable(
                        resources.getDrawable(R.drawable.ic_cross, null)
                    )
                    showAlert(it.throwable)
                }
                is UiState.Complete -> wialonHostingLoginStatusImage.setImageDrawable(
                    resources.getDrawable(R.drawable.ic_done, null)
                )
                UiState.Loading -> {
                    wialonHostingLoginStatusProgress.visibility = View.VISIBLE
                    wialonHostingLoginStatusImage.visibility = View.GONE
                }
            }
        }

//        viewModel.uiState.observeInViewLiveCycle {
//            progressBar.visibility = View.GONE
//            when {
//                it is UiState.Loading -> progressBar.visibility = View.VISIBLE
//                it is UiState.Error && it.error != WialonResponse.NO_ERROR -> showAlertWithRepeat(
//                    it.error.text,
//                    it.funToRepeat
//                )
//                it is UiState.Error && it.throwable is UnknownHostException ->
//            }
//        }
    }

    companion object {
        private const val PREF_ACCOUNT_NAME = "accountName"
    }
}