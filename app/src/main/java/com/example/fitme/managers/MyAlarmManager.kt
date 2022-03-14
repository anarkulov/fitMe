package com.example.fitme.managers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.fitme.core.utils.Log
import com.example.fitme.ui.alarm.AlarmActivity


class MyAlarmManager: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("Broadcast Received", myTag)
        if (intent != null && context != null) {
            Log.d("Alarm Received. So launch notification", myTag)
            val title = intent.getStringExtra("title")
            val service = Intent(context, MyAlarmService::class.java).apply {
                putExtra("title", title)
            }
            context.startService(service)
        }
    }

    companion object {
        private const val myTag = "AlarmManager"
        private lateinit var alarmManager: AlarmManager

        fun setAlarm3(context: Context, id: String, title: String, time: Long) {
            val intent = Intent(context, MyAlarmManager::class.java).apply {
                putExtra("id", id)
                putExtra("title", title)
            }
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent)
            Log.d("Alarm set for $time", myTag)
        }

        fun cancelAlarm3(context: Context) {
            Log.d("cancelAlarm", myTag)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, MyAlarmManager::class.java)
            val broadcast = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            alarmManager.cancel(broadcast)
        }

        fun setAlarm(context: Context, title: String, time: Long) {
            alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmClockInfo =
                AlarmManager.AlarmClockInfo(time, getAlarmInfoPendingIntent(context))
            alarmManager.setAlarmClock(alarmClockInfo, getAlarmActionPendingIntent(context))
            Log.d("Alarm set for $time", myTag)
        }

        fun cancelAlarm(context: Context, id: Int) {
            Log.d("cancelAlarm", myTag)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val notificationIntent = Intent("android.media.action.DISPLAY_NOTIFICATION")
            notificationIntent.addCategory("android.intent.category.DEFAULT")
            val broadcast = PendingIntent.getBroadcast(context,
                id,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            alarmManager.cancel(broadcast)
        }

        private fun getAlarmInfoPendingIntent(context: Context): PendingIntent? {
            val alarmInfoIntent = Intent(context, AlarmActivity::class.java)
            alarmInfoIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            return PendingIntent.getActivity(context,
                0,
                alarmInfoIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)
        }

        private fun getAlarmActionPendingIntent(context: Context): PendingIntent? {
            val intent = Intent(context, AlarmActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            return PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }


}