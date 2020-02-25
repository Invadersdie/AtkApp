package com.atk.app.core.base

import android.widget.EditText
import androidx.databinding.InverseBindingAdapter

@InverseBindingAdapter(attribute = "android:text", event = "android:textAttrChanged")
fun getTextInt(view: EditText): Int {
    return view.text.toString().toInt()
}

@InverseBindingAdapter(attribute = "android:text", event = "android:textAttrChanged")
fun getTextFloat(view: EditText): Float {
    return view.text.toString().toFloat()
}