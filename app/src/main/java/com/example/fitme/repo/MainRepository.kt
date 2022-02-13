package com.example.fitme.repo

import com.example.fitme.data.local.AppPrefs
import com.example.fitme.data.remote.RemoteDataSource

class MainRepository(private val dataSource: RemoteDataSource, private val prefs: AppPrefs) {

    fun setAccessToken(accessToken: String) {
        prefs.accessToken = accessToken
    }

    fun setRefreshToken(refreshToken: String) {
        prefs.refreshToken = refreshToken
    }

    fun getAccessToken(): String? {
        return prefs.accessToken
    }

    fun getRefreshToken(): String? {
        return prefs.refreshToken
    }
}