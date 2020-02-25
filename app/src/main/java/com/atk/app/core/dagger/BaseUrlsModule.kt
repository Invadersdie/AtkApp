package com.atk.app.core.dagger

import android.content.Context
import com.atk.app.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

const val LOCAL_BASE_URL = "MOBILE-SERVICE-LOCAL-BASE-URL"
const val HOSTING_BASE_URL = "MOBILE-SERVICE-HOSTING-BASE-URL"
const val ATK_BASE_URL = "MOBILE-SERVICE-ATK-BASE-URL"

@Module
@InstallIn(SingletonComponent::class)
open class BaseUrlsModule {
    @Provides
    @Named(ATK_BASE_URL)
    open fun atkBaseUrl(@ApplicationContext context: Context): String =
        context.getString(R.string.endpoint_atk)

    @Provides
    @Named(LOCAL_BASE_URL)
    open fun localBaseUrl(@ApplicationContext context: Context): String =
        context.getString(R.string.endpoint_local)

    @Provides
    @Named(HOSTING_BASE_URL)
    open fun hostingBaseUrl(@ApplicationContext context: Context): String =
        context.getString(R.string.endpoint_hosting)
}