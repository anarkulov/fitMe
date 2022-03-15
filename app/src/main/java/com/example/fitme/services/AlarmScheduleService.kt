package com.example.fitme.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.fitme.core.utils.Log

class AlarmScheduleService: Service() {

    private val myTag = "AlarmScheduleService"

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("onStartCommand: received", myTag)

        try {

        } catch(e: Exception) {
            e.printStackTrace()
            Log.d("onStartCommand: exceptionError", myTag)
        }

        return START_NOT_STICKY
    }
}