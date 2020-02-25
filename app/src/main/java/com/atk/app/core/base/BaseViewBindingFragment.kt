package com.atk.app.core.base

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.viewbinding.ViewBinding

abstract class BaseViewBindingFragment<VIEW_BINDING : ViewBinding>(@LayoutRes private val layoutResId: Int) :
    BaseFragment(layoutResId) {

    abstract val bindingHolder: FragmentViewBindingContainer<VIEW_BINDING>
    protected val binding get() = bindingHolder.binding

    final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.onViewCreated(view, savedInstanceState)
    }

    final override fun onStart() {
        super.onStart()
        binding.onStart()
    }

    final override fun onResume() {
        super.onResume()
        binding.onResume()
    }

    final override fun onPause() {
        super.onPause()
        binding.onPause()
    }

    final override fun onStop() {
        super.onStop()
        binding.onStop()
    }

    override fun onDestroyView() {
        binding.onViewDestroyed()
        super.onDestroyView()
    }

    final override fun onBackPressed(): Boolean {
        return binding.onBackPressed()
    }

    protected open fun VIEW_BINDING.onViewCreated(view: View, bundle: Bundle?) = Unit

    protected open fun VIEW_BINDING.onStart() = Unit

    protected open fun VIEW_BINDING.onResume() = Unit

    protected open fun VIEW_BINDING.onPause() = Unit

    protected open fun VIEW_BINDING.onStop() = Unit

    protected open fun VIEW_BINDING.onViewDestroyed() = Unit

    protected open fun VIEW_BINDING.onBackPressed(): Boolean = true
}