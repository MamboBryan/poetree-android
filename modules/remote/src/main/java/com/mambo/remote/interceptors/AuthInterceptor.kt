package com.mambo.remote.interceptors

import com.mambo.data.preferences.UserPreferences
import com.mambo.data.responses.ServerResponse
import com.mambo.data.responses.TokenResponse
import com.mambo.remote.BuildConfig
import com.mambo.remote.service.PoemsApi
import io.ktor.http.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import okio.IOException
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    val preferences: UserPreferences,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val refreshToken = runBlocking { preferences.refreshToken.first() ?: "" }
        val accessToken = runBlocking { preferences.accessToken.first() ?: "" }

        val request = chain.request()
        val response = chain.proceed(request.newRequestWithAccessToken(token = accessToken))

        return when (response.code) {
            HttpStatusCode.Unauthorized.value -> {
                val newAccessToken =
                    refreshToken(refreshToken) ?: throw IOException("Login again to continue")
                chain.proceed(request.newRequestWithAccessToken(token = newAccessToken))
            }
            else -> response
        }

    }

    private fun Request.newRequestWithAccessToken(token: String): Request {
        return this.newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
    }

    private fun refreshToken(refreshToken: String): String? {
        synchronized(this) {

            val okHttpClient = OkHttpClient.Builder()
                .apply {
                    if (BuildConfig.DEBUG)
                        addInterceptor(
                            HttpLoggingInterceptor().apply {
                                level = HttpLoggingInterceptor.Level.BODY
                            }
                        )
                }
                .build()

            val body = Json.encodeToString(mapOf("token" to refreshToken))
                .toRequestBody("application/json".toMediaTypeOrNull())

            val request = Request.Builder()
                .post(body = body)
                .url(PoemsApi.Endpoints.REFRESH_TOKEN.url)
                .build()

            val response = okHttpClient.newCall(request).execute()

            return when (response.isSuccessful) {
                true -> {

                    val responseBody = response.body?.string() ?: return null
                    val data = Json.decodeFromString<ServerResponse<TokenResponse>>(responseBody)

                    val tokens = data.data!!

                    runBlocking {
                        preferences.updateTokens(
                            access = tokens.accessToken,
                            refresh = tokens.refreshToken
                        )
                    }

                    tokens.accessToken
                }
                false -> {
                    runBlocking { preferences.forcedSignOut() }
                    null
                }
            }

        }

    }

}