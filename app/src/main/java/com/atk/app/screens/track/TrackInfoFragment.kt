package com.atk.app.screens.track

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.atk.app.R
import com.atk.app.core.base.BaseViewBindingFragment
import com.atk.app.core.base.FragmentViewBindingContainer
import com.atk.app.databinding.ToolbarRecyclerViewLayoutBinding
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackInfoFragment :
    BaseViewBindingFragment<ToolbarRecyclerViewLayoutBinding>(R.layout.toolbar_recycler_view_layout) {

    override val viewModel: TrackInfoViewModel by viewModels()
    private val navArgs: TrackInfoFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.initData(navArgs.wialonId, navArgs.isLocal)
    }

    override val bindingHolder: FragmentViewBindingContainer<ToolbarRecyclerViewLayoutBinding>
        get() = FragmentViewBindingContainer(
            viewLifecycleOwner,
            ToolbarRecyclerViewLayoutBinding.bind(requireView())
        )

    override fun ToolbarRecyclerViewLayoutBinding.onViewCreated(view: View, bundle: Bundle?) {
        val adapter = GroupAdapter<GroupieViewHolder>()
        recyclerView.apply {
            setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    TypedValue().apply {
                        requireContext().theme.resolveAttribute(
                            R.attr.colorOnPrimary,
                            this,
                            true
                        )
                    }.data
                )
            )
            this.adapter = adapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }

        viewModel.data.observeInViewLiveCycle {
            adapter.update(it.entries.map { TrackParameterItem(it) })
        }
    }
}