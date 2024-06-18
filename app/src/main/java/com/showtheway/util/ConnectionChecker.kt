package com.showtheway.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.content.ContextCompat

class ConnectionChecker(context: Context) {
    private val connectivityManager = ContextCompat.getSystemService(context, ConnectivityManager::class.java)

    val isConnected: () -> Boolean = {
        val activeNetwork = connectivityManager?.activeNetwork
        val capabilities = connectivityManager?.getNetworkCapabilities(activeNetwork)

        capabilities?.run {
            hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        } ?: false
    }
}