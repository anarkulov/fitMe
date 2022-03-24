package com.example.fitme.managers

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.IBinder
import com.example.fitme.R
import com.example.fitme.core.utils.Log
import com.example.fitme.data.models.Alarm
import com.example.fitme.managers.MyAlarmManager.Companion.ALARM_KEY
import com.example.fitme.managers.alarm.startAlarmActivity
import com.example.fitme.managers.alarm.stopAlarm
import com.example.fitme.ui.alarm.AlarmActivity

class MyAlarmService : Service() {
    private val myTag = "MyAlarmService"

    companion object {
        var ringtone: Ringtone? = null

        const val ACTION_SHOW_ALARM = "a.show_alarm"
        const val ACTION_STOP = "a.stop_alarm"
        const val ACTION_STOP_POSE = "stop_alarm"
        const val NOTIFICATION_ID = 1
        var mAlarm: Alarm? = null
    }

    private var isPlaying = false

    override fun onCreate() {
        super.onCreate()
        createAlarmNotificationChannel(this)
        startForeground(NOTIFICATION_ID, buildStartupNotification(this))
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("onStartCommand: ${intent?.action}", myTag)
        val alarm = intent?.getBundleExtra(ALARM_KEY)?.getSerializable(ALARM_KEY) as Alarm?

        if (!isPlaying) {
            playAlarm()
//            val bundle = Bundle().apply {
//                putSerializable(ALARM_KEY, alarm)
//            }
//            intent?.putExtra(ALARM_KEY, bundle)

            startAlarmActivity(this, alarm)

            Log.d("intentAlarms: $alarm", myTag)
            startForeground(NOTIFICATION_ID, buildNotification(this, alarm?.title, alarm?.time, isPlaying))
        }

        isPlaying = true
        when (intent?.action) {
            ACTION_SHOW_ALARM, ACTION_STOP -> {
                startAlarmActivity(this, alarm)
            }
            ACTION_STOP_POSE -> {
                stopAlarmPlay()
                stopAlarm()
            }
        }

        return START_NOT_STICKY
    }

    private fun stopAlarmPlay() {
        ringtone?.stop()
        ringtone = null
    }

    private fun sendNotification(title: String?) {
        val channelId = "fitme_app"
        val alarmActivityIntent = Intent(this, AlarmActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 1, alarmActivityIntent, 0)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = Notification.Builder(this, channelId)
            .setContentTitle("FitMe Alarm")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setSound(defaultSoundUri)
            .setContentText(title)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(0, notification)
    }

    private fun playAlarm() {
        var notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        if (notificationUri == null) {
            notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        }

        ringtone = RingtoneManager.getRingtone(this, notificationUri)
        ringtone?.isLooping = true
        ringtone?.play()
    }
}