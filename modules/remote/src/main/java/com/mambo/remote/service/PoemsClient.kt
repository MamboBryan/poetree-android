package com.mambo.remote.service

import com.mambo.remote.BuildConfig
import com.mambo.remote.interceptors.AuthInterceptor
import com.mambo.remote.interceptors.NetworkInterceptor
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.observer.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import javax.inject.Inject

class PoemsClient @Inject constructor(
    private val authInterceptor: AuthInterceptor,
    private val networkInterceptor: NetworkInterceptor,
    private val loggingInterceptor: HttpLoggingInterceptor
) {

    fun client() = HttpClient(OkHttp) {

        engine {
            this.addInterceptor(authInterceptor)
            this.addInterceptor(networkInterceptor)
            this.addInterceptor(loggingInterceptor)
        }

        install(ContentNegotiation) {
            json()
        }

        install(Logging) {
            level = LogLevel.BODY
        }

        install(ResponseObserver) {
            onResponse { response ->
                if (BuildConfig.DEBUG) Timber.i("RESPONSE -> \n $response")
            }
        }

        install(DefaultRequest) {
            header(HttpHeaders.Accept, ContentType.Application.Json)
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }

    }

}