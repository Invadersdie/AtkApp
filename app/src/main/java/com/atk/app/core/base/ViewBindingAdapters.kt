package com.atk.app.core.base

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("goneIfNoText")
fun setViewGoneIfNoText(view: View, text: CharSequence?) {
    view.visibility = if (text.isNullOrBlank()) View.GONE else View.VISIBLE
}

@BindingAdapter("visibleOrInvisible")
fun setViewVisibleOrInvisible(view: View, visibleOrInvisible: Boolean?) {
    view.visibility = if (visibleOrInvisible == true) View.VISIBLE else View.INVISIBLE
}

@BindingAdapter("visibleOrGone")
fun setViewVisibleOrGone(view: View, visibleOrGone: Boolean) {
    view.visibility = if (visibleOrGone) View.VISIBLE else View.GONE
}