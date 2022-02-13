package com.example.fitme.data.local

import android.content.Context
import com.example.fitme.core.utils.Log
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val prefModule = module {
    single { AppPrefs(androidContext()) }
}

class AppPrefs(context: Context) {

    private val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    private val gson: Gson = Gson()

    private var _token: String? = null

    var accessToken: String?
        get() = token()
        set(value) = prefs.edit().putString("token", value).apply()

    private fun token(): String? {
        _token?.let { return it }
            ?: run {
                _token = prefs.getString("token", null)
                Log.d("AccessToken: $_token")
                return _token
            }
    }

    var refreshToken: String?
        get() = prefs.getString("refresh_token", null)
        set(value) = prefs.edit().putString("refresh_token", value).apply()

    fun clearPrefs() {
        refreshToken = null
        accessToken = null
    }
}