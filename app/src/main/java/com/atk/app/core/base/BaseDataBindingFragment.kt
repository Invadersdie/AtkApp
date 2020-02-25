package com.atk.app.core.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseDataBindingFragment<VIEW_DATA_BINDING : ViewDataBinding>(@LayoutRes private val layoutResId: Int) :
    BaseFragment(layoutResId) {
    private var viewDataBinding: VIEW_DATA_BINDING? = null

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return DataBindingUtil.inflate<VIEW_DATA_BINDING>(inflater, layoutResId, container, false)
            .run {
                viewDataBinding = this
                lifecycleOwner = this@BaseDataBindingFragment
                viewDataBinding?.onViewCreated(root, savedInstanceState)
                root
            }
    }

    final override fun onStart() {
        super.onStart()
        viewDataBinding?.onStart()
    }

    final override fun onResume() {
        super.onResume()
        viewDataBinding?.onResume()
    }

    final override fun onPause() {
        super.onPause()
        viewDataBinding?.onPause()
    }

    final override fun onStop() {
        super.onStop()
        viewDataBinding?.onStop()
    }

    override fun onDestroyView() {
        viewDataBinding?.onViewDestroyed()
        viewDataBinding = null
        super.onDestroyView()
    }

    final override fun onBackPressed(): Boolean {
        return viewDataBinding?.onBackPressed() ?: true
    }

    protected open fun VIEW_DATA_BINDING.onViewCreated(view: View, bundle: Bundle?) = Unit

    protected open fun VIEW_DATA_BINDING.onStart() = Unit

    protected open fun VIEW_DATA_BINDING.onResume() = Unit

    protected open fun VIEW_DATA_BINDING.onPause() = Unit

    protected open fun VIEW_DATA_BINDING.onStop() = Unit

    protected open fun VIEW_DATA_BINDING.onViewDestroyed() = Unit

    protected open fun VIEW_DATA_BINDING.onBackPressed(): Boolean = true
}