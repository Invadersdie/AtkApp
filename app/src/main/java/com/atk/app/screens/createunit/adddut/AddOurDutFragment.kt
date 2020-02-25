package com.atk.app.screens.createunit.adddut

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import com.atk.app.R
import com.atk.app.core.repository.ResponseResult
import com.atk.app.core.repository.model.Dut
import com.atk.app.databinding.AddOurDutFragmentBinding
import com.atk.app.screens.SharedViewModel
import com.atk.app.screens.createunit.ContainsArrayAdapter

class AddOurDutFragment : DialogFragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val viewModelClient: AddOurDutViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_our_dut_fragment, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        setStyle(STYLE_NO_TITLE, R.style.RoundedCornersDialogFragment)
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = AddOurDutFragmentBinding.bind(view)

        @Suppress("UNCHECKED_CAST")
        val duts = requireArguments().getParcelableArray("DUTS").orEmpty().toList() as List<Dut>
        sharedViewModel.dutList.observeInViewLiveCycle {
            if (it is ResponseResult.Success) {
                binding.dutEditText.setAdapter(
                    ContainsArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        it.data.minus(duts)
                    )
                )
            } else {
                TODO()
            }
        }

        binding.dutEditText.apply {
            onItemClickListener =
                AdapterView.OnItemClickListener { parent, _, position, _ ->
                    val dut = parent.getItemAtPosition(position) as Dut
                    binding.selectedDutName.text = dut.name
                    viewModelClient.selectedDut(dut)
                }

            addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(p0: Editable?) {}

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, start: Int, before: Int, count: Int) {
                    if (before > count && before - count == 1) {
                        viewModelClient.removeSelectedDut()
                        log(sharedViewModel.dutList.value.toString())
                    }
                }
            })
        }

        viewModelClient.selectedDut.observeInViewLiveCycle {
            binding.okButton.isEnabled = it != null
        }

        binding.okButton.setOnClickListener {
            sharedViewModel.setSelectedDut(viewModelClient.selectedDut.value!!)
            log(sharedViewModel.dutList.value.toString())
            findNavController().popBackStack()
            dialog?.dismiss()
        }
        binding.cancelButton.setOnClickListener {
            findNavController().popBackStack()
            dialog?.dismiss()
        }
    }

    private inline fun <T> LiveData<T>.observeInViewLiveCycle(crossinline block: (T) -> Unit) {
        observe(viewLifecycleOwner, { block(it) })
    }
}