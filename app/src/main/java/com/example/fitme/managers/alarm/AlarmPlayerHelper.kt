package com.example.fitme.managers.alarm


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.example.fitme.App
import com.example.fitme.core.extentions.runAfter
import com.example.fitme.data.models.Alarm
import com.example.fitme.managers.MyAlarmManager.Companion.ALARM_KEY
import com.example.fitme.managers.MyAlarmService
import com.example.fitme.ui.alarm.AlarmActivity
import com.example.fitme.ui.main.MainActivity

fun stopAlarm() {
    val app = App.getInstance()
    val intent = Intent(
        app,
        MyAlarmService::class.java
    )
    app.stopService(intent)

    runAfter(1000) {
        val mainActivityIntent = Intent(app, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        app.startActivity(mainActivityIntent)
    }
}

fun startAlarmActivity(ctx: Context, alarm: Alarm?) {
//    Log.d("startAlarmActivity:${intent?.getBundleExtra(ALARM_KEY)?.getSerializable(ALARM_KEY) as Alarm?}", "AlarmActivity")
    val intent = Intent(ctx, AlarmActivity::class.java)
    val bundle = Bundle().apply {
        putSerializable(ALARM_KEY, alarm)
    }
    intent.putExtra(ALARM_KEY, bundle)

    if (ctx !is Activity) {
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    ctx.startActivity(intent)
}