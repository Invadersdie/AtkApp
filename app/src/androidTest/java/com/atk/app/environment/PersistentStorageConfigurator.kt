package com.atk.app.environment

import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import com.atk.app.environment.AssetsReader.Companion.readTextFromAssets
import dagger.hilt.android.testing.HiltTestApplication

private const val SHARED_PREFERENCES_UPDATE_TRY_COUNT = 5

private val context: Context get() = ApplicationProvider.getApplicationContext<HiltTestApplication>()

private val authStateRepository: SharedPreferences
    get() = context.getSharedPreferences(
        "NAME_AUTH_STATE_REPOSITORY",
        Context.MODE_PRIVATE
    )

class PersistentStorageConfigurator(private val baseUrl: String) {

    fun setupActualAuthenticationToken() = tryUpdateAuthStateFromAsset("authenticated-state.json")

    fun setupExpiredAuthenticatedToken() =
        tryUpdateAuthStateFromAsset("authentication-expired-state.json")

    private fun tryUpdateAuthStateFromAsset(authStateAssetName: String) {
        var editTry = 0
        do {
            if (++editTry > SHARED_PREFERENCES_UPDATE_TRY_COUNT) {
                throw Error("Can't update auth state in $SHARED_PREFERENCES_UPDATE_TRY_COUNT tries")
            }
            val editingSuccess = updateAuthStateFromAsset(authStateAssetName)
        } while (editingSuccess.not())
        android.util.Log.i("access_token_updated", "updated from file $authStateAssetName")
    }

    private fun updateAuthStateFromAsset(authStateAssetName: String): Boolean =
        authStateRepository
            .edit()
            .putString(
                "KEY_AUTH_STATE_JSON",
                readTextFromAssets(authStateAssetName).replace(
                    "\$baseUrl",
                    baseUrl.replace("/", "\\/")
                )
            )
            .commit()
}