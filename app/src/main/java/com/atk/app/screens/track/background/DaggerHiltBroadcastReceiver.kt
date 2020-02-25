package com.atk.app.screens.track.background

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Work around to inject dependencies in Broadcast receivers via Dagger Hilt.
 * Refer: https://github.com/google/dagger/issues/1918
 */
abstract class DaggerHiltBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // No-op
    }
}