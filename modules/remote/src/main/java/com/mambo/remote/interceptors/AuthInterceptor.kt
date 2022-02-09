package com.mambo.remote.interceptors

import com.mambo.data.preferences.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor() : Interceptor {

    @Inject
    lateinit var preferences: UserPreferences

    override fun intercept(chain: Interceptor.Chain): Response = runBlocking {
        val accessToken = preferences.accessToken.first()

        val request = chain
            .request()
            .newBuilder()
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Content-Type", "application/json")
            .build()

        chain.proceed(request)
    }

}