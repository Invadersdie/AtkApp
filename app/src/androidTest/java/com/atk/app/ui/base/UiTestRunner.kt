package com.atk.app.ui.base

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.provider.Settings.Global.*
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

class UiTestRunner : AndroidJUnitRunner() {
    override fun onCreate(arguments: Bundle?) {
        super.onCreate(arguments)
        setAnimations(false)
    }

    override fun finish(resultCode: Int, results: Bundle?) {
        setAnimations(true)
        super.finish(resultCode, results)
    }

    override fun newApplication(cl: ClassLoader?, cn: String?, context: Context?): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }

    private fun setAnimations(enabled: Boolean) {
        val value = if (enabled) "1.0" else "0.0"
        try {
            InstrumentationRegistry.getInstrumentation().uiAutomation.run {
                this.executeShellCommand("settings put global $WINDOW_ANIMATION_SCALE $value")
                this.executeShellCommand("settings put global $TRANSITION_ANIMATION_SCALE $value")
                this.executeShellCommand("settings put global $ANIMATOR_DURATION_SCALE $value")
            }
        } catch (ignore: RuntimeException) {
            // Ignore if UiAutomation fails to enable/disable animations as It is not mandatory to
            // run UI tests.
        }
    }
}
