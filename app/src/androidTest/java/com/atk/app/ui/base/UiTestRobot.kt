package com.atk.app.ui.base

interface UiTestRobot<out T : UiTestAssertions> {
    fun launchScreen()
    infix fun assert(func: T.() -> Unit): T
}
