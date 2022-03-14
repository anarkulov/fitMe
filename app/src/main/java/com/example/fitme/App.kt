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
        instance = this
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Init Koin DI
        startKoin {
            androidContext(this@App)
            modules(koinModules)
        }

        // Init Timber log
        Log.init()
    }

    companion object {
        private var instance: App? = null
        fun getInstance(): App {
            return instance as App
        }
    }
}