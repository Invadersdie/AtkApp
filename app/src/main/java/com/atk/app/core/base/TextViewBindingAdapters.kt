package com.atk.app.core.base

import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("android:text")
fun setText(view: TextView, value: Float?) {
    if (value == null) return
    view.text = value.toString()
}