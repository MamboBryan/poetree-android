package com.mambo.remote.interceptors

import com.mambo.data.preferences.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(preferences: UserPreferences) : Interceptor {
    
    private var token = ""

    init {
        runBlocking { preferences.accessToken.first() }
    }

    override fun intercept(chain: Interceptor.Chain): Response = runBlocking {

        val request = chain
            .request()
            .newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Content-Type", "application/json")
            .build()

        chain.proceed(request)
    }

}