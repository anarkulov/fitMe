package com.example.fitme.managers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.example.fitme.core.utils.Log
import com.example.fitme.data.local.AppPrefs
import com.example.fitme.data.models.Alarm
import java.util.*


class MyAlarmManager(private val prefs: AppPrefs): BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("Broadcast Received", myTag)
        if (intent != null && context != null) {
            Log.d("Alarm Received. So launch notification", myTag)
            val title = intent.getStringExtra("title")
            val service = Intent(context, MyAlarmService::class.java).apply {
                putExtra("title", title)
            }
            if (intent.action.equals("android.intent.action.BOOT_COMPLETED")) {
                setTime()
                return
            }
            context.startService(service)
        }
    }

    private fun setTime() {

    }

    companion object {
        private const val myTag = "AlarmManager"
        private lateinit var alarmManager: AlarmManager

        fun setAlarm3(context: Context, id: String, title: String, time: String) {
            val intent = Intent(context, MyAlarmManager::class.java).apply {
                putExtra("id", id)
                putExtra("title", title)
            }
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR, time.split(":")[0].toInt())
            calendar.set(Calendar.MINUTE, time.split(":")[1].toInt())

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
            Log.d("Alarm set for $time", myTag)
        }

        fun cancelAlarm3(context: Context) {
            Log.d("cancelAlarm", myTag)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, MyAlarmManager::class.java)
            val broadcast = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            alarmManager.cancel(broadcast)
        }

        fun scheduleAlarm(context: Context, time: String, isRepeatable: Boolean, days: TreeMap<Int, String>, alarmId: Int) {
            val timeInMs = Alarm().convertHMtoMS(time, isRepeatable, days)

            val intent = Intent(context, MyAlarmManager::class.java)
            val bundle = Bundle().apply {
                putParcelable("ALARM_KEY", this)
            }
            intent.putExtra("ALARM_KEY", bundle)
            val pendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager? ?: return

            alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(timeInMs, pendingIntent), pendingIntent)
            Log.d("Alarm set for $timeInMs", myTag)
        }

    }
}