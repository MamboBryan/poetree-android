package com.mambo.remote.interceptors

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import javax.inject.Inject

class NetworkInterceptor @Inject constructor(
    @ApplicationContext val context: Context
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        if (isConnectionOn().not()) throw Exception("No Internet")

        if (isInternetAvailable().not()) throw Exception("Connection has no internet.")

        return chain.proceed(chain.request())

    }

    /**
     * Checks if connection is on
     */
    private fun isConnectionOn(): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return postAndroidMInternetCheck(manager)
    }

    /**
     * Check connection for post M devices
     */
    private fun postAndroidMInternetCheck(
        connectivityManager: ConnectivityManager
    ): Boolean {
        val network = connectivityManager.activeNetwork
        val connection = connectivityManager.getNetworkCapabilities(network)
        return connection != null && connection.hasCapability(
            NetworkCapabilities.NET_CAPABILITY_INTERNET
        )
    }

    /**
     * Checks if Internet is available
     */
    private fun isInternetAvailable(): Boolean {
        return true
    }
}