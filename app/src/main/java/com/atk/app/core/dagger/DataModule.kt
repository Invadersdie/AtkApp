package com.atk.app.core.dagger

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.room.Room
import com.atk.app.R
import com.atk.app.core.repository.database.AppDatabase
import com.atk.app.core.repository.internet.api.WialonApi
import com.atk.app.core.repository.internet.data.model.send.Sensor
import com.atk.app.core.repository.internet.wialon.authorization.AuthorizationRepository
import com.atk.app.core.repository.wialon.WialonRepositoryImpl
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.InputStreamReader
import javax.inject.Named
import javax.inject.Singleton

internal const val NAME_AUTH_STATE_REPOSITORY = "NAME_AUTH_STATE_REPOSITORY"
const val NAME_USER_SETTINGS_REPOSITORY = "NAME_USER_SETTINGS_REPOSITORY"
const val SENSORS_DATA = "SENSORS_DATA"

@Module
@InstallIn(SingletonComponent::class)
internal class DataModule {

    @Provides
    @Named(SENSORS_DATA)
    fun sensorsData(
        gson: Gson,
        @ApplicationContext context: Context
    ): HashMap<String, List<Sensor>> {
        val inputStream = context.resources.openRawResource(R.raw.sensors)
        val sensorList = JsonParser().parse(InputStreamReader(inputStream)).asJsonArray
        val outputList = HashMap<String, List<Sensor>>(sensorList.size())
        sensorList.forEach {
            outputList[it.asJsonObject.getAsJsonPrimitive("name").asString] =
                gson.fromJson(
                    it.asJsonObject.getAsJsonArray("sensors"),
                    object : TypeToken<List<Sensor>>() {}.type
                )
        }
        Log.d("DATA_MODULE", outputList.toString())
        return outputList
    }

    @Singleton
    @Provides
    fun database(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "database").build()
    }

    @Named(NAME_AUTH_STATE_REPOSITORY)
    @Provides
    fun providesAuthStateRepository(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(NAME_AUTH_STATE_REPOSITORY, Context.MODE_PRIVATE)
    }

    @Named(NAME_USER_SETTINGS_REPOSITORY)
    @Provides
    fun providesUserSettingsRepository(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(NAME_USER_SETTINGS_REPOSITORY, Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideWialonRepository(
        gson: Gson,
        @Named(LOCAL) localWialonApi: WialonApi,
        @Named(HOSTING) hostingWialonApi: WialonApi,
        authorizationRepository: AuthorizationRepository
    ): WialonRepositoryImpl =
        WialonRepositoryImpl(gson, localWialonApi, hostingWialonApi, authorizationRepository)
}