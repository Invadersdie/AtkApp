package com.atk.app.ui.test

import com.atk.app.ui.base.UiTestAssertions
import com.atk.app.ui.base.UiTestRobot

fun loginScreen(
    func: CreateUnitUiTestRobot.() -> Unit
) = CreateUnitUiTestRobot().apply { func() }

class CreateUnitUiTestRobot : UiTestRobot<CreateUnitTestAssertions> {
    override fun launchScreen() {
        object : UiTestAssertions {

        }
        TODO("Not yet implemented")
    }

    override fun assert(func: CreateUnitTestAssertions.() -> Unit): CreateUnitTestAssertions {
        TODO("Not yet implemented")
    }

}