package com.atk.app.screens.search

import android.view.View
import com.atk.app.R
import com.atk.app.core.repository.internet.data.model.recieve.WialonItem
import com.atk.app.databinding.SearchItemBinding
import com.xwray.groupie.viewbinding.BindableItem

class SearchItem(private val data: WialonItem) :
    BindableItem<SearchItemBinding>() {
    override fun bind(viewBinding: SearchItemBinding, position: Int) {
        viewBinding.apply {
            parameter.text = data.nm
            value.text = data.toString()
        }
    }

    override fun getLayout(): Int = R.layout.search_item

    override fun initializeViewBinding(view: View) = SearchItemBinding.bind(view)
}