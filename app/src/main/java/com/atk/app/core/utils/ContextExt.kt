package com.atk.app.core.utils

import android.content.Context
import android.content.pm.PackageManager

fun Context.isAppAvailable(appName: String): Boolean {
    val pm: PackageManager = this.packageManager
    return try {
        pm.getPackageInfo(appName, 0)
        true
    } catch (e: java.lang.Exception) {
        false
    }
}