package com.example.fitme

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.fitme.core.utils.Log
import com.example.fitme.di.koinModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Init Koin DI
        startKoin {
            androidContext(this@App)
            modules(koinModules)
        }

        // Init Timber log
        Log.init()
    }
}