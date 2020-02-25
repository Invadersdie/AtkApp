package com.atk.app.core.base.toolbar

import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.atk.app.R

var Toolbar.configuration: ToolbarConfiguration?
    get() {
        return tag as? ToolbarConfiguration
    }
    set(value) {
        tag = value

        value?.run {
            if (titleResId != null) {
                setTitle(titleResId)
            } else {
                setTitle(title)
            }

            if (menuResId != null) {
                inflateMenu(menuResId)
                for (i in 0 until menu.size()) {
                    menu.getItem(i).apply {
                        setOnMenuItemClickListener(menuItemClick)
                    }
                }
            }

            if (homeClick != null) {
                navigationIcon = ContextCompat.getDrawable(context, homeIconResId)
                setNavigationOnClickListener { homeClick.invoke() }
            } else if (homeIconResId != R.drawable.icon_arrow_back) {
                navigationIcon = ContextCompat.getDrawable(context, homeIconResId)
            }

            applyOnCreate?.forEach {
                it.value.invoke(this@configuration.menu.findItem(it.key))
            }
        }
    }

var Toolbar.searchFocused: Boolean
    get() {
        return searchView?.hasFocus() ?: false
    }
    set(value) {
        when {
            searchFocused && value -> Unit
            searchFocused && value.not() -> searchView?.clearFocus()
            searchFocused.not() && value -> searchView?.requestFocus()
            searchFocused.not() && value.not() -> Unit
        }
    }

val Toolbar.searchView: SearchView?
    get() {
        for (i in 0 until (menu?.size() ?: 0)) {
            val actionView = menu?.getItem(i)?.actionView

            if (actionView is SearchView) {
                return actionView
            }
        }
        return null
    }
val Toolbar.searchViewMenuItem: MenuItem?
    get() {
        for (i in 0 until (menu?.size() ?: 0)) {
            val item = menu?.getItem(i)
            if (item?.actionView is SearchView) {
                return item
            }
        }
        return null
    }