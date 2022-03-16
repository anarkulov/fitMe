package com.example.fitme.managers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.example.fitme.core.utils.Log
import com.example.fitme.data.models.Alarm
import com.example.fitme.managers.MyAlarmService.Companion.ACTION_STOP_POSE
import com.example.fitme.managers.receivers.AlarmReceiver
import com.example.fitme.utils.Utils
import java.util.*


class MyAlarmManager {

    companion object {
        private const val myTag = "AlarmManager"
        const val ALARM_KEY = "ALARM_KEY"
        const val ALARM_ACTIVE = "ALARM_ACTIVE"
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
            Log.d("Alarm set for $calendar.timeInMillis", myTag)
        }

        fun cancelAlarm3(context: Context) {
            Log.d("cancelAlarm", myTag)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, MyAlarmManager::class.java)
            val broadcast = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            alarmManager.cancel(broadcast)
        }

        fun scheduleAlarm(context: Context, alarm: Alarm) {
            val treeMap = TreeMap<Int, Boolean>()
            val days = alarm.days
            for ((index, day) in days.withIndex()) {
                treeMap[index] = day
            }

            val timeInMs = Utils.convertHMtoMS(alarm.timestamp, alarm.isRepeatable, treeMap)

            val intent = Intent(context, AlarmReceiver::class.java)
            val bundle = Bundle().apply {
                putSerializable(ALARM_KEY, alarm)
            }
            intent.putExtra(ALARM_KEY, bundle)
            val pendingIntent = PendingIntent.getBroadcast(context, alarm.id.substring(8, 13).toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager? ?: return

            alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(timeInMs, pendingIntent), pendingIntent)
            Log.d("Alarm set for $timeInMs", myTag)
        }

        fun stopAlarm(context: Context) {
            Log.d("stopAlarm", myTag)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                action = ACTION_STOP_POSE
            }
            val broadcast = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            alarmManager.set(AlarmManager.RTC_WAKEUP, 0, broadcast)
        }
    }
}