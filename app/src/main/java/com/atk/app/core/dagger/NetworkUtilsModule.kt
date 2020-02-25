package com.atk.app.core.dagger

import android.content.Context
import com.atk.app.core.repository.internet.utils.NetworkUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object NetworkUtilsModule {

    @Provides
    fun getNetworkUtils(@ApplicationContext context: Context): NetworkUtils {
        return NetworkUtils(context = context)
    }
}