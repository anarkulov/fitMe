package com.example.fitme.managers.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.example.fitme.core.utils.Log
import com.example.fitme.data.models.Alarm
import com.example.fitme.managers.AlarmWakeLock
import com.example.fitme.managers.MyAlarmManager.Companion.ALARM_KEY
import com.example.fitme.managers.MyAlarmService

class AlarmReceiver: BroadcastReceiver() {

    private val myTag = "AlarmReceiver"

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("Broadcast Received", myTag)

        if (intent != null && context != null) {
            try {
                AlarmWakeLock().acquireCpu(context)
                Log.d("onReceive: wakelock acquired", myTag)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val alarm = intent.getBundleExtra(ALARM_KEY)?.getSerializable(ALARM_KEY) as Alarm? ?: return

            alarm.isPlayed = false
            val bundle = Bundle().apply {
                putSerializable(ALARM_KEY, alarm)
            }
            intent.putExtra(ALARM_KEY, bundle)

            Log.d("Alarm Received. So launch notification", myTag)

            val service = Intent(context, MyAlarmService::class.java).apply {
                putExtra(ALARM_KEY, bundle)
            }
            if (intent.action.equals("android.intent.action.BOOT_COMPLETED")) {
                return
            }

            try {
                AlarmWakeLock().releaseCpu()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

            context.startService(service)
        }
    }
}