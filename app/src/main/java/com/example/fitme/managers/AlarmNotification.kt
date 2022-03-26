package com.example.fitme.managers

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.example.fitme.R
import com.example.fitme.core.utils.Log
import com.example.fitme.data.models.Alarm
import com.example.fitme.managers.MyAlarmService.Companion.ACTION_SHOW_ALARM

const val NOTIFICATION_ALARM_CHANNEL_ID = "player_channel"

@SuppressLint("LaunchActivityFromNotification")
fun buildNotification(
    context: Context, alarm: Alarm?
): Notification? {
    Log.d("buildNotification: ${alarm?.time}", "AlarmNotification")

    val playerIntent = Intent(context.applicationContext, MyAlarmService::class.java)
    val bundle = Bundle().apply {
        putSerializable(MyAlarmManager.ALARM_KEY, alarm)
    }
    playerIntent.putExtra(MyAlarmManager.ALARM_KEY, bundle)
    playerIntent.action = ACTION_SHOW_ALARM

    val pendingContentIntent =
        PendingIntent.getService(context.applicationContext, 1, playerIntent, PendingIntent.FLAG_UPDATE_CURRENT)

    val builder =
        androidx.core.app.NotificationCompat.Builder(context, NOTIFICATION_ALARM_CHANNEL_ID)
            .setContentTitle(alarm?.title)
            .setContentText(alarm?.time)
            .setContentIntent(pendingContentIntent)
            .setSmallIcon(R.drawable.ic_logo)
            .setVisibility(androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC)

//    builder.addAction(
//        generateAction(context, R.drawable.ic_clear, R.string.stop, ACTION_STOP, alarm)
//    )

    return builder.build()
}


private fun generateAction(
    context: Context,
    icon: Int,
    action: Int,
    intentAction: String,
    alarm: Alarm?
): androidx.core.app.NotificationCompat.Action {
    Log.d("generateAction", "AlarmNotification")

    val intent = Intent(context.applicationContext, MyAlarmService::class.java)
    intent.action = intentAction
    val bundle = Bundle().apply {
        putSerializable(MyAlarmManager.ALARM_KEY, alarm)
    }
    intent.putExtra(MyAlarmManager.ALARM_KEY, bundle)
    val pendingIntent = PendingIntent.getService(context.applicationContext, 1, intent, 0)
    return androidx.core.app.NotificationCompat.Action.Builder(icon, context.getString(action), pendingIntent)
        .build()
}


//fun buildStartupNotification(context: Context): Notification? {
//    Log.d("buildStartupNotification", "AlarmNotification")
//
//    val intent = Intent(context.applicationContext, MyAlarmService::class.java)
//    intent.action = ACTION_SHOW_ALARM
////    val bundle = Bundle().apply {
////        putSerializable(MyAlarmManager.ALARM_KEY, alarm)
////    }
////    intent.putExtra(MyAlarmManager.ALARM_KEY, bundle)
//    val pendingContentIntent =
//        PendingIntent.getService(context.applicationContext, 12, intent, 0)
//    val builder =
//        androidx.core.app.NotificationCompat.Builder(context, NOTIFICATION_ALARM_CHANNEL_ID)
//            .setContentTitle("Alarm")
//            .setContentText("Tap")
//            .setContentIntent(pendingContentIntent)
//            .setSmallIcon(R.drawable.ic_logo)
//    return builder.build()
//}


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