package com.atk.app.core.base

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import com.atk.app.R
import com.atk.app.core.repository.wialon.WialonThrowable
import com.atk.app.core.utils.IOnBackPressed
import pub.devrel.easypermissions.EasyPermissions

abstract class BaseFragment(@LayoutRes private val layoutResId: Int) : Fragment(layoutResId),
    IOnBackPressed,
    EasyPermissions.PermissionCallbacks {

    abstract val viewModel: BaseViewModel

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
//        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
//        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun showDialog(dialogData: DialogData) {
        AlertDialog.Builder(requireContext())
            .setTitle(dialogData.title)
            .setMessage(dialogData.message)
            .setNegativeButton(R.string.no) { _, _ -> dialogData.negative() }
            .setPositiveButton(R.string.yes) { _, _ -> dialogData.positive() }
            .setIcon(R.drawable.ic_done)
            .setCancelable(false)
            .show()
            .setCanceledOnTouchOutside(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.navigateAction.observeInViewLiveCycle { event ->
            event.getContentIfNotHandled()?.let {
                findNavController().navigate(it)
            }
        }
        viewModel.dialog.observeInViewLiveCycle { event ->
            event.getContentIfNotHandled()?.let {
                showDialog(it)
            }
        }
        viewModel.alert.observeInViewLiveCycle { event ->
            event.getContentIfNotHandled()?.let {
                showAlert(it)
            }
        }
        viewModel.alertRepeatable.observeInViewLiveCycle { event ->
            event.getContentIfNotHandled()?.let {
                showAlertRepeatable(it)
            }
        }
        viewModel.toastMessage.observeInViewLiveCycle { event ->
            event.getContentIfNotHandled()?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
        viewModel.toastMessageResource.observeInViewLiveCycle { event ->
            event.getContentIfNotHandled()?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
    }

    protected fun showAlert(throwable: Throwable) {
        if (throwable is WialonThrowable) {
            Log.e("ERROR", requireContext().getString(throwable.value.text))
            AlertDialog.Builder(requireContext())
                .setTitle("Произошла ошибка Wialon")
                .setMessage(throwable.value.text)
                .setNegativeButton(R.string.ok, null)
                .setIcon(R.drawable.ic_cross)
                .show()
        } else {
            Log.e("ERROR", throwable.stackTraceToString())
            AlertDialog.Builder(requireContext())
                .setTitle("Произошла ошибка")
                .setMessage(throwable.message.toString())
                .setNegativeButton(R.string.ok, null)
                .setIcon(R.drawable.ic_cross)
                .show()
        }
    }

    protected fun showAlert(@StringRes text: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Произошла ошибка")
            .setMessage(text)
            .setNegativeButton(R.string.ok, null)
            .setIcon(R.drawable.ic_cross)
            .show()
    }

    private fun showAlertRepeatable(alertData: AlertDataRepeatable) {
        val throwable = alertData.throwable
        if (throwable is WialonThrowable) {
            Log.e("ERROR", requireContext().getString(throwable.value.text))
            AlertDialog.Builder(requireContext())
                .setTitle("Ошибка виалон")
                .setMessage(requireContext().getString(throwable.value.text) + alertData.message)
                .setPositiveButton(R.string.repeat_question) { _, _ -> alertData.positive.invoke() }
                .setNeutralButton(R.string.cancel) { _, _ -> alertData.negative.invoke() }
                .setIcon(R.drawable.ic_cross)
                .setCancelable(false)
                .show().setCanceledOnTouchOutside(false)
        } else {
            Log.e("ERROR", throwable.stackTraceToString())
            AlertDialog.Builder(requireContext())
                .setTitle("Произошла ошибка")
                .setMessage(throwable.message.toString() + alertData.message)
                .setPositiveButton(R.string.repeat_question) { _, _ -> alertData.positive.invoke() }
                .setNeutralButton(R.string.cancel) { _, _ -> alertData.negative.invoke() }
                .setIcon(R.drawable.ic_cross)
                .setCancelable(false)
                .show().setCanceledOnTouchOutside(false)
        }
    }

    protected inline fun <T> LiveData<T>.observeInViewLiveCycle(crossinline block: (T) -> Unit) {
        observe(viewLifecycleOwner, { block(it) })
    }
}

data class AlertDataRepeatable(
    val message: String,
    val throwable: Throwable,
    val negative: () -> Unit,
    val positive: () -> Unit
)

data class DialogData(
    val title: String,
    val message: String,
    val negative: () -> Unit,
    val positive: () -> Unit
)