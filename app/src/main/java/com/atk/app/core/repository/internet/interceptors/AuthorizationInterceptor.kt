package com.atk.app.core.repository.internet.interceptors

import com.atk.app.core.repository.internet.wialon.authorization.AuthorizationRepository
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

class AuthorizationInterceptor @Inject constructor(
    private val authorizationRepository: AuthorizationRepository
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request: Request = chain.request()
        val queryParameterName: String
        val token: String
        when {
            request.url.host.contains("local") -> {
                queryParameterName = "sid"
                token = authorizationRepository.localSid
            }
            request.url.host.contains("hst-api") -> {
                queryParameterName = "sid"
                token = authorizationRepository.hostingSid
            }
            else -> {
                queryParameterName = "Bearer"
                token = ""
            }
        }
        if (token.isNotBlank()) {
            val url: HttpUrl =
                request.url
                    .newBuilder()
                    .addQueryParameter(queryParameterName, token)
                    .build()
            request = request.newBuilder().url(url).build()
        }
        return chain.proceed(request)
    }
}