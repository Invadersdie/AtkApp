package com.atk.app.core.base

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewbinding.ViewBinding

/***
 * Sample usage in [BaseFragment]:
 * class SampleFragment : Fragment(R.layout.fragment_sample) { // Use construction layout id injection.
 *      private val bindingContainer
 *          get() = FragmentViewBindingContainer(
 *             viewLifecycleOwner,
 *             FragmentSampleBinding.bind(requireView())
 *          )
 *      private val binding
 *          get() = bindingContainer.binding
 * }
 */
class FragmentViewBindingContainer<T : ViewBinding> constructor(
    viewLifecycleOwner: LifecycleOwner,
    binding: T
) {
    private var _binding: T? = binding
    val binding
        get() = requireNotNull(_binding)

    init {
        viewLifecycleOwner.lifecycle.addObserver(
            object : LifecycleObserver {
                @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                fun onDestroy() {
                    _binding = null
                }
            }
        )
    }
}