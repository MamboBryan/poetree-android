package com.mambo.remote.interceptors

import com.mambo.data.preferences.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class RefreshTokenInterceptor @Inject constructor(preferences: UserPreferences) : Interceptor {

    private var token: String?

    init {
        runBlocking { token = preferences.accessToken.first() ?: "" }
    }

    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain
            .request()
            .newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()

        return chain.proceed(request)

    }
}