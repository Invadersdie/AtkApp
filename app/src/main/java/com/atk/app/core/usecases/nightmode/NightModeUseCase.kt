package com.atk.app.core.usecases.nightmode

import android.content.SharedPreferences
import com.atk.app.core.dagger.NAME_USER_SETTINGS_REPOSITORY
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject
import javax.inject.Named

private const val NIGHT_MODE_SETTING_KEY = "NIGHT_MODE"

private val NIGHT_MODE_DEFAULT_VALUE =
    NightModeValues.NIGHT_MODE_DEFAULT

@ExperimentalCoroutinesApi
class NightModeUseCase @Inject constructor(
    @Named(NAME_USER_SETTINGS_REPOSITORY)
    private val preferenceRepository: SharedPreferences,
    private val nightModeSystemIntegration: NightModeSystemIntegration
) {

    suspend fun updateNightModeAccordingToUserPreferences() =
        updateNightModeValue(getNightModeValue())

    suspend fun setNightModeValue(nightModeValue: NightModeValues) {
        preferenceRepository.edit().putInt(
            NIGHT_MODE_SETTING_KEY, nightModeValue.value
        ).apply()
        updateNightModeValue(nightModeValue)
    }

    fun getNightModeValue(): NightModeValues {
        return NightModeValues.valueOf(
            preferenceRepository.getInt(NIGHT_MODE_SETTING_KEY, NIGHT_MODE_DEFAULT_VALUE.value)
        )
    }

    private suspend fun updateNightModeValue(nightModeValue: NightModeValues) {
        nightModeSystemIntegration.applyNightModeValue(nightModeValue)
    }
}

enum class NightModeValues(val value: Int) {
    NIGHT_MODE_DEFAULT(0),
    NIGHT_MODE_NIGHT(-1),
    NIGHT_MODE_DAY(1);

    companion object {
        fun valueOf(value: Int) = when (value) {
            -1 -> NIGHT_MODE_NIGHT
            0 -> NIGHT_MODE_DEFAULT
            1 -> NIGHT_MODE_DAY
            else -> throw IllegalArgumentException()
        }
    }
}