package com.example.fitme.core.network

import android.content.Context
import android.content.Intent
import com.example.fitme.data.local.AppPrefs
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val prefs: AppPrefs, private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request()
        val request = req.newBuilder()

        if (prefs.accessToken != null) {
            request.addHeader("Authorization", "Bearer ${prefs.accessToken}")
        }
        //request.addHeader("Content-Type", "application/x-www-form-urlencoded")

        val response = chain.proceed(request.build())

        if (response.code == 401 || response.code == 403) {

//            if (prefs.accessToken != null) {
//
//                val context: Context = (context.applicationContext as App).applicationContext
//
//                val intent = Intent(context, AuthActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                context.startActivity(intent)
//            }
//
//            prefs.accessToken = null
        }
        return response
    }
}