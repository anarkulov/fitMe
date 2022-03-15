package com.example.fitme.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.fitme.core.utils.Log
import com.google.gson.Gson
import org.json.JSONObject
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

    fun saveToMap(id: String, value: String) {
        val map = getMap()
        map[id] = value

        try {
            val jsonObject = JSONObject(map as Map<*, *>?)
            val jsonString = jsonObject.toString()
            val editor: SharedPreferences.Editor = prefs.edit()
            editor.remove("alarm_key").commit()
            editor.putString("alarm_key", jsonString)
            editor.commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getMap(): HashMap<String, String> {
        val map: HashMap<String, String> = hashMapOf()

        try {
            val jsonString = prefs.getString("alarm_key", JSONObject().toString())
            if (jsonString != null) {
                val jsonObject = JSONObject(jsonString)
                val ids: Iterator<String> = jsonObject.keys()

                while (ids.hasNext()) {
                    val id = ids.next()
                    val value = jsonObject.get(id) as String
                    map[id] = value
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return map
    }
}