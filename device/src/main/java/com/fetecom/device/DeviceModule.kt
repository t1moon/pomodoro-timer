package com.fetecom.device

import com.fetecom.device.network.AndroidNetworkConnection
import com.fetecom.device.network.NetworkConnection
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


val deviceModule = module {
    single<NetworkConnection> { AndroidNetworkConnection( androidContext() ) }
}