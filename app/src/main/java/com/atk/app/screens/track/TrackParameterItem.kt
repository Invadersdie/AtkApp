package com.atk.app.screens.track

import android.view.View
import androidx.core.content.ContextCompat
import com.atk.app.R
import com.atk.app.databinding.TrackParameterItemBinding
import com.xwray.groupie.viewbinding.BindableItem

class TrackParameterItem(private val data: Map.Entry<String, MessageDataExtended>) :
    BindableItem<TrackParameterItemBinding>() {
    override fun bind(viewBinding: TrackParameterItemBinding, position: Int) {
        viewBinding.apply {
            parameter.text = data.key
            value.text = data.value.value
            val color = ContextCompat.getColor(
                viewBinding.root.context, when (data.value.valueChange) {
                    ValueChange.INIT, ValueChange.NOT_COMPARABLE -> R.color.white
                    ValueChange.INCREASED -> android.R.color.holo_orange_dark
                    ValueChange.SAME -> android.R.color.darker_gray
                    ValueChange.DECREASED -> android.R.color.holo_red_light
                }
            )
            value.setBackgroundColor(color)
        }
    }

    override fun getLayout(): Int = R.layout.track_parameter_item

    override fun initializeViewBinding(view: View) = TrackParameterItemBinding.bind(view)
}