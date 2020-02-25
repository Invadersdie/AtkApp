package com.atk.app.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.atk.app.MainActivity

class Navigator {

    fun showHomeScreen(activity: Activity, clearTask: Boolean = false) {
        val homeIntent = Intent(activity, MainActivity::class.java).apply {
            if (clearTask) {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
        open(activity, homeIntent)
    }

    private fun open(context: Context, intent: Intent) {
        if (context !is Activity) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}