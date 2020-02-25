package com.atk.app.core.dagger

import android.util.Log
import com.atk.app.core.repository.internet.api.AtkApi
import com.atk.app.core.repository.internet.api.WialonApi
import com.atk.app.core.repository.internet.data.model.recieve.*
import com.atk.app.core.repository.internet.interceptors.AuthorizationInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

const val LOCAL = "local"
const val HOSTING = "hosting"

@Module
@InstallIn(SingletonComponent::class)
internal class InternetModule {

    @Provides
    @Named(LOCAL)
    fun providesWialonApiClient(
        okHttpClient: OkHttpClient,
        converterFactory: Converter.Factory,
        @Named(LOCAL_BASE_URL) mobileServiceEndpoint: String
    ): WialonApi {
        return Retrofit.Builder()
            .baseUrl(mobileServiceEndpoint)
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .build()
            .create(WialonApi::class.java)
    }

    @Provides
    @Named(HOSTING)
    fun providesWialonHostingApiClient(
        okHttpClient: OkHttpClient,
        converterFactory: Converter.Factory,
        @Named(HOSTING_BASE_URL) mobileServiceEndpoint: String
    ): WialonApi {
        return Retrofit.Builder()
            .baseUrl(mobileServiceEndpoint)
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .build()
            .create(WialonApi::class.java)
    }

    @Provides
    fun providesAtkApiClient(
        okHttpClient: OkHttpClient,
        converterFactory: Converter.Factory,
        @Named(ATK_BASE_URL) mobileServiceEndpoint: String
    ): AtkApi {
        return Retrofit.Builder()
            .baseUrl(mobileServiceEndpoint)
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .build()
            .create(AtkApi::class.java)
    }

    @Provides
    fun providesConverterFactory(gson: Gson): Converter.Factory {
        return GsonConverterFactory.create(gson)
    }

    @Singleton
    @Provides
    fun providesOkHttpClient(
        authorizationInterceptor: AuthorizationInterceptor,
        httpLoggingInterceptor: HttpLoggingInterceptor,
        builder: OkHttpClient.Builder
    ): OkHttpClient {
        return builder
            .protocols(listOf(Protocol.HTTP_1_1))
            .addInterceptor(authorizationInterceptor)
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    @Provides
    fun providesGson(): Gson {
        val longSensorType =
            object : TypeToken<ArrayPrimitiveAndObjectResponse<Long, SensorResponse>>() {}.type

        return GsonBuilder()
            .registerTypeAdapter(ListOfHwTypes::class.java, ListOfHwTypesDeserializer())
            .registerTypeAdapter(CustomFieldResponse::class.java, CustomFieldResponseDeserializer())
            .registerTypeAdapter(MessageItem::class.java, MessageItemDeserializer())
            .registerTypeAdapter(
                longSensorType,
                CustomPrimitiveObjectDeserializer(Long::class.java, SensorResponse::class.java)
            )
            .create()
    }

    @Singleton
    @Provides
    fun provideOkHttpClientBuilder() = OkHttpClient.Builder()

    @Singleton
    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor((object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Log.d("HTTP", message)
            }
        })).apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private companion object {
        private const val OK_HTTP_CACHE_SIZE = 10L * 1024L * 1024L
    }
}