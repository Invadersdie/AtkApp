package com.atk.app.environment

interface EnvironmentConfigurator {
    val mocksVerifyInfo: MocksVerifyInfo
    fun setupMockSever(setupFunc: MockServerConfigurator.() -> Unit)
    fun replaceMockServerConfig(setupFunc: MockServerConfigurator.() -> Unit)
}