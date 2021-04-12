package com.mambo.poetree.utils

import android.util.Log
import java.io.IOException
import java.net.InetSocketAddress
import javax.net.SocketFactory

object DoesNetworkHaveInternet {

    // Make sure to execute this on a background thread.
    fun execute(socketFactory: SocketFactory): Boolean {
        return try {
            Log.d("NETWORK INTERNET", "PINGING google.")
            val socket = socketFactory.createSocket() ?: throw IOException("Socket is null.")
            socket.connect(InetSocketAddress("8.8.8.8", 53), 1500)
            socket.close()
            Log.d("NETWORK INTERNET", "PING success.")
            true
        } catch (e: IOException) {
            Log.e("NETWORK INTERNET", "No internet connection. ${e}")
            false
        }
    }
}