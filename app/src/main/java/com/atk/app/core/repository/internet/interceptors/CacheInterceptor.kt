package com.atk.app.core.repository.internet.interceptors

import com.atk.app.core.dagger.LOCAL_BASE_URL
import okhttp3.CacheControl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

class CacheInterceptor @Inject constructor(
    @Named(LOCAL_BASE_URL) val mobileServiceUrl: Provider<String>
) : Interceptor {

    private val personalAreaApiUrl by lazy {
        mobileServiceUrl.get().toHttpUrlOrNull()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (shouldCacheResponse(chain)) {
            return addCacheHeaderToResponse(response)
        }
        return response
    }

    private fun addCacheHeaderToResponse(response: Response): Response {
        val cacheControl = CacheControl.Builder()
            .maxAge(5, TimeUnit.MINUTES)
            .build()
        return response.newBuilder()
            .removeHeader("Pragma")
            .removeHeader("Cache-Control")
            .header("Cache-Control", cacheControl.toString())
            .header("Accept", "application/json")
            .build()
    }

    private fun shouldCacheResponse(chain: Interceptor.Chain) =
        chain.request().url == personalAreaApiUrl
}