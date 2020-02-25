package com.atk.app.environment

import com.atk.app.core.dagger.ATK_BASE_URL
import com.atk.app.core.dagger.BaseUrlsModule
import com.atk.app.core.dagger.HOSTING_BASE_URL
import com.atk.app.core.dagger.LOCAL_BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Named

const val TEST_LOCAL_URL = "local"
const val TEST_HOSTING_URL = "hosting"

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [BaseUrlsModule::class]
)
class TestBaseUrlsModule {

    @Provides
    @Named(ATK_BASE_URL)
    fun atkBaseUrl(): String =
        "http://localhost:8080/"

    @Provides
    @Named(LOCAL_BASE_URL)
    fun localBaseUrl(): String =
        "http://localhost:8080/$TEST_LOCAL_URL/"

    @Provides
    @Named(HOSTING_BASE_URL)
    fun hostingBaseUrl(): String =
        "http://localhost:8080/$TEST_HOSTING_URL/"
}