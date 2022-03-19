package com.example.fitme.managers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.fitme.R

class MyNotificationManager {
    companion object {
        fun notify(context: Context, appName: String, msg: String) {
            val channelId = "fitme_app"
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val channel =
                    NotificationChannel(channelId, appName, NotificationManager.IMPORTANCE_DEFAULT)
                channel.description = "Samples Channel"
                channel.enableLights(true)
                channel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
                channel.enableVibration(true)
                notificationManager.createNotificationChannel(channel)
            }
            val builder = NotificationCompat.Builder(context, channelId)
                .setVibrate(longArrayOf(0, 100, 100, 100, 100, 100))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(appName)
                .setContentText(msg)
            notificationManager.notify(builder.hashCode(), builder.build())
        }
    }
}