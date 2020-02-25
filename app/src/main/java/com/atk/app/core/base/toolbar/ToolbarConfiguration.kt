package com.atk.app.core.base.toolbar

import android.view.MenuItem
import com.atk.app.R

data class ToolbarConfiguration(
    val titleResId: Int? = null,
    val title: CharSequence? = null,
    val menuResId: Int? = null,
    val menuItemClick: (MenuItem) -> Boolean = { false },
    val applyOnCreate: Map<Int, (MenuItem) -> Unit>? = null,
    val homeIconResId: Int = R.drawable.icon_arrow_back,
    val homeClick: (() -> Unit)? = null
)