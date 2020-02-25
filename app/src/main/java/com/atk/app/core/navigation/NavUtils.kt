package com.atk.app.core.navigation

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.FragmentNavigator
import com.atk.app.R

fun createDefaultNavOptions() = NavOptions.Builder()
    .setEnterAnim(R.anim.nav_default_enter_anim)
    .setExitAnim(R.anim.nav_default_exit_anim)
    .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
    .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
    .build()

fun NavController.navigateUriWithDefaultOptions(
    uri: Uri,
    extras: FragmentNavigator.Extras? = null
) {
    this.navigate(uri, createDefaultNavOptions(), extras)
}