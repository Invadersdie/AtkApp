package com.atk.app.core.base

import android.widget.CheckBox
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter

@BindingAdapter("android:checked")
fun checkValue(view: CheckBox, value: Boolean) {
    view.isChecked = value
}

@InverseBindingAdapter(attribute = "android:checked", event = "android:checkedAttrChanged")
fun getcheckValue(view: CheckBox): Boolean {
    return view.isChecked
}