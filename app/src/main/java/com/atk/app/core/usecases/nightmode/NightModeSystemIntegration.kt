package com.atk.app.core.usecases.nightmode

import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import com.atk.app.core.provider.ActivityProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
class NightModeSystemIntegration @Inject constructor(private val activityProvider: ActivityProvider) {

    suspend fun applyNightModeValue(nightModeValue: NightModeValues) {
        when (nightModeValue) {
            NightModeValues.NIGHT_MODE_DAY -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO
            )
            NightModeValues.NIGHT_MODE_DEFAULT -> AppCompatDelegate.setDefaultNightMode(
                defaultNightModeForCurrentSystem()
            )
            NightModeValues.NIGHT_MODE_NIGHT -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_YES
            )
        }
        applyUpdatedTheme()
    }

    private fun defaultNightModeForCurrentSystem() =
        when (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            true -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            false -> AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
        }

    private suspend fun applyUpdatedTheme() =
        activityProvider.submitBlock { delegate.applyDayNight() }
}