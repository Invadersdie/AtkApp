package com.atk.app.screens.search

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.atk.app.R
import com.atk.app.core.base.BaseViewBindingFragment
import com.atk.app.core.base.FragmentViewBindingContainer
import com.atk.app.core.base.toolbar.ToolbarConfiguration
import com.atk.app.core.base.toolbar.configuration
import com.atk.app.core.base.toolbar.searchView
import com.atk.app.core.base.toolbar.searchViewMenuItem
import com.atk.app.databinding.SearchLayoutBinding
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment :
    BaseViewBindingFragment<SearchLayoutBinding>(R.layout.search_layout) {

    override val viewModel: SearchViewModel by viewModels()

    override val bindingHolder: FragmentViewBindingContainer<SearchLayoutBinding>
        get() = FragmentViewBindingContainer(
            viewLifecycleOwner,
            SearchLayoutBinding.bind(requireView())
        )

    private var isLoading = false

    override fun SearchLayoutBinding.onViewCreated(view: View, bundle: Bundle?) {
        val adapter = GroupAdapter<GroupieViewHolder>()
        toolbarHolder.toolbar.apply {
            configuration = ToolbarConfiguration(menuResId = R.menu.search_menu)
            searchViewMenuItem?.setOnActionExpandListener(object :
                MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                    viewModel.changeQuery("")
                    return true
                }

                override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                    viewModel.changeQuery("")
                    return true
                }
            })
            searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    viewModel.changeQuery("$query*")
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    viewModel.changeQuery("$newText*")
                    return true
                }
            })
        }
        recyclerView.apply {
            this.adapter = adapter
            val linearLayoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            layoutManager = linearLayoutManager
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (isLoading) return
                    val visibleItemCount = linearLayoutManager.childCount
                    val totalItemCount = linearLayoutManager.itemCount
                    val firstVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition()
                    if (visibleItemCount + firstVisibleItemPosition + THRESHOLD >= totalItemCount) {
                        viewModel.downloadNextPage()
                    }
                }
            })
        }
        createUnit.setOnClickListener {
            findNavController().navigate(SearchFragmentDirections.actionSearchFragmentToCreateUnitFragment())
        }

        viewModel.loading.observeInViewLiveCycle { isLoading = it }
        viewModel.data.observeInViewLiveCycle {
            adapter.update(it.map { SearchItem(it) })
        }
        viewModel.changeQuery("")
    }

    companion object {
        const val THRESHOLD = 2
    }
}