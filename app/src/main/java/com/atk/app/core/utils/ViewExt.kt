package com.atk.app.core.utils

import android.view.View
import android.view.ViewTreeObserver

fun View.doOnceOnGlobalLayout(func: () -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            func()
            this@doOnceOnGlobalLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
        }
    })
}