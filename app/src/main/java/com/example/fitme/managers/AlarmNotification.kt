package com.example.fitme.managers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.fitme.R
import com.example.fitme.managers.MyAlarmService.Companion.ACTION_SHOW_ALARM
import com.example.fitme.managers.MyAlarmService.Companion.ACTION_STOP

const val NOTIFICATION_ALARM_CHANNEL_ID = "player_channel"

fun buildNotification(
    context: Context, title: String? = "Alarm", text: String?, playing: Boolean
): Notification? {
    val playerIntent = Intent(context.applicationContext, MyAlarmService::class.java)
    playerIntent.action = ACTION_SHOW_ALARM
    val pendingContentIntent =
        PendingIntent.getService(context.applicationContext, 1, playerIntent, 0)
    val builder =
        androidx.core.app.NotificationCompat.Builder(context, NOTIFICATION_ALARM_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setContentIntent(pendingContentIntent)
            .setSmallIcon(R.drawable.ic_logo)
            .setVisibility(androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC)

    builder.addAction(
        generateAction(context, R.drawable.ic_clear, R.string.stop, ACTION_STOP)
    )

    return builder.build()
}


private fun generateAction(
    context: Context,
    icon: Int,
    action: Int,
    intentAction: String
): androidx.core.app.NotificationCompat.Action {
    val intent = Intent(context.applicationContext, MyAlarmService::class.java)
    intent.action = intentAction
    val pendingIntent = PendingIntent.getService(context.applicationContext, 1, intent, 0)
    return androidx.core.app.NotificationCompat.Action.Builder(icon, context.getString(action), pendingIntent)
        .build()
}


fun buildStartupNotification(context: Context): Notification? {
    val intent = Intent(context.applicationContext, MyAlarmService::class.java)
    intent.action = ACTION_SHOW_ALARM
    val pendingContentIntent =
        PendingIntent.getService(context.applicationContext, 12, intent, 0)
    val builder =
        androidx.core.app.NotificationCompat.Builder(context, NOTIFICATION_ALARM_CHANNEL_ID)
            .setContentTitle("Alarm")
            .setContentText("Tap")
            .setContentIntent(pendingContentIntent)
            .setSmallIcon(R.drawable.ic_logo)
    return builder.build()
}


fun createAlarmNotificationChannel(ctx: Context) {
    val mNotificationManager =
        ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    if (mNotificationManager.getNotificationChannel(
            NOTIFICATION_ALARM_CHANNEL_ID
        ) == null
    ) {
        val name: CharSequence = "Alarm"
        val description = "Tap"
        val importance = NotificationManager.IMPORTANCE_LOW
        val mChannel = NotificationChannel(NOTIFICATION_ALARM_CHANNEL_ID, name, importance)
        mChannel.description = description
        mChannel.enableLights(false)
        mChannel.enableVibration(false)
        mNotificationManager.createNotificationChannel(mChannel)
    }
}