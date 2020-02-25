package com.atk.app.screens.history

import android.view.View
import androidx.core.content.ContextCompat
import com.atk.app.R
import com.atk.app.core.repository.database.entity.CreatedObject
import com.atk.app.databinding.CreatedObjectItemBinding
import com.xwray.groupie.viewbinding.BindableItem

class CreatedObjectItem(private val createdObject: CreatedObject) :
    BindableItem<CreatedObjectItemBinding>() {

    fun getNavigationItemId() = createdObject.wialonId
    fun isLocal() = createdObject.isLocal

    override fun bind(viewBinding: CreatedObjectItemBinding, position: Int) {
        viewBinding.apply {
            imei.text = createdObject.unitImei
            status.setImageDrawable(
                ContextCompat.getDrawable(
                    root.context,
                    if (createdObject.done) R.drawable.ic_done else R.drawable.ic_cross
                )
            )
            company.text = createdObject.company.name
        }
    }

    override fun getLayout() = R.layout.created_object_item

    override fun initializeViewBinding(view: View) = CreatedObjectItemBinding.bind(view)
}