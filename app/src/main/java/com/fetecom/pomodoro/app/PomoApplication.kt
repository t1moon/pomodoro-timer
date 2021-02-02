package com.fetecom.pomodoro.app

import android.app.Application
import com.fetecom.data.localModule
import com.fetecom.device.deviceModule
import com.fetecom.pomodoro.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class PomoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        startKoin {
            androidContext(this@PomoApplication)
            modules(listOf(
                localModule,
                appModule,
                deviceModule
            ))
        }
    }
}
