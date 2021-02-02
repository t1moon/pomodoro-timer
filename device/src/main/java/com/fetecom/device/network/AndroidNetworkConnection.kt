package com.fetecom.device.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

class AndroidNetworkConnection(
        private val applicationContext: Context
) : NetworkConnection {
    override fun isAvailable(): Boolean {
//        val cm = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
//        return activeNetwork?.isConnected == true
        return false
    }
}

interface NetworkConnection {
    fun isAvailable() : Boolean
}