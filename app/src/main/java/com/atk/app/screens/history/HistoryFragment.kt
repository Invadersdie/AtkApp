package com.atk.app.screens.history

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.atk.app.R
import com.atk.app.core.base.BaseViewBindingFragment
import com.atk.app.core.base.FragmentViewBindingContainer
import com.atk.app.core.base.toolbar.ToolbarConfiguration
import com.atk.app.core.base.toolbar.configuration
import com.atk.app.databinding.HistoryFragmentBinding
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : BaseViewBindingFragment<HistoryFragmentBinding>(R.layout.history_fragment) {

    override val viewModel: HistoryViewModel by viewModels()

    override val bindingHolder: FragmentViewBindingContainer<HistoryFragmentBinding>
        get() = FragmentViewBindingContainer(
            viewLifecycleOwner,
            HistoryFragmentBinding.bind(requireView())
        )

    override fun HistoryFragmentBinding.onViewCreated(view: View, bundle: Bundle?) {
        val adapter = GroupAdapter<GroupieViewHolder>()
        toolbarHolder.toolbar.configuration = ToolbarConfiguration(title = "История")
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
        }
        adapter.setOnItemClickListener { item, _ ->
            if (item is CreatedObjectItem) {
                findNavController().navigate(
                    HistoryFragmentDirections.actionHistoryFragmentToTrackInfoFragment(
                        item.getNavigationItemId(), item.isLocal()
                    )
                )
            }
        }
        viewModel.dataList.observeInViewLiveCycle { list ->
            adapter.addAll(list.map { CreatedObjectItem(it) })
        }
    }
}