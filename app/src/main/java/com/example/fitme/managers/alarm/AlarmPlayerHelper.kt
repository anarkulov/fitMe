package com.example.fitme.managers.alarm


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.example.fitme.App
import com.example.fitme.managers.MyAlarmService
import com.example.fitme.ui.alarm.AlarmActivity
fun stopAlarm() {
    val intent = Intent(
        App.getInstance(),
        MyAlarmService::class.java
    )
    App.getInstance().stopService(intent)
}

fun startAlarmActivity(ctx: Context, extras: Bundle?) {
    val intent = Intent(ctx, AlarmActivity::class.java).apply {
        extras?.let { putExtras(it) }
    }
    if (ctx !is Activity) {
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    ctx.startActivity(intent)
}