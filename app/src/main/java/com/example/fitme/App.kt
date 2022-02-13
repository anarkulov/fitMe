package com.example.fitme

import android.app.Application
import com.example.fitme.di.koinModules
import com.example.fitme.core.utils.Log
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // Init Koin DI
        startKoin {
            androidContext(this@App)
            modules(koinModules)
        }

        // Init Timber log
        Log.init()
    }
}