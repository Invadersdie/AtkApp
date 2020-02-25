package com.atk.app.screens.createunit.adddut

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.atk.app.R
import com.atk.app.databinding.AddClientDutFragmentBinding
import com.atk.app.screens.SharedViewModel
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

private const val TAG = "DEBUG"

class AddClientDutFragment : DialogFragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val viewModel: AddClientDutViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_client_dut_fragment, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        setStyle(STYLE_NO_TITLE, R.style.RoundedCornersDialogFragment)
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = AddClientDutFragmentBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            vm = viewModel
        }

        viewModel.enableToCreate.observe(viewLifecycleOwner, {
            binding.okButton.isEnabled = it
        })
        binding.okButton.setOnClickListener {
            sharedViewModel.setSelectedDut(viewModel.getSelectedDut())
            log(sharedViewModel.dutList.value.toString())
            findNavController().popBackStack()
            dialog?.dismiss()
        }
        binding.cancelButton.setOnClickListener {
            findNavController().popBackStack()
            dialog?.dismiss()
        }
    }
}

fun Fragment.log(text: String) {
    Log.d(TAG + this.javaClass.canonicalName, text)
}

fun ViewModel.log(text: String) {
    Log.d(TAG + this.javaClass.canonicalName, text)
}

fun JvmType.Object.log(text: String) {
    Log.d(TAG + this.javaClass.canonicalName, text)
}