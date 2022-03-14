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
import android.support.v4.media.session.MediaSessionCompat
import com.example.fitme.R
import com.example.fitme.core.utils.Log
import com.example.fitme.data.models.Alarm
import com.example.fitme.managers.alarm.startAlarmActivity
import com.example.fitme.ui.alarm.AlarmActivity

class MyAlarmService : Service() {
    private val myTag = "MyAlarmService"

    companion object {
        var ringtone: Ringtone? = null

        const val ACTION_SHOW_ALARM = "a.show_alarm"
        const val ACTION_STOP = "a.stop_alarm"
        const val NOTIFICATION_ID = 1
        var alarm: Alarm? = null
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
        val title = intent?.getStringExtra("title")
        Log.d("onStartCommand: $title", myTag)

//        id = if (state == true) 1 else 0
//
//        if (!isPlaying && id == 1) {
//            playAlarm()
//            isPlaying = true
//            sendNotification(title)
//        } else if (isPlaying && id == 0) {
//            ringtone?.stop()
//            isPlaying = false
//        }

        if (!isPlaying) {
            playAlarm()
            startForeground(NOTIFICATION_ID, buildNotification(this, "Alarm", title, isPlaying))
        }

        isPlaying = true

        when (intent?.action) {
            ACTION_SHOW_ALARM, ACTION_STOP -> {
                startAlarmActivity(this, intent.extras)
            }
        }

        return START_NOT_STICKY
    }

    private fun initAlarmSession() {

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
        ringtone?.setLooping(true)
        ringtone?.play()
    }
}