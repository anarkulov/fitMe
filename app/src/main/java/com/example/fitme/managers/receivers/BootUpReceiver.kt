package com.example.fitme.managers.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootUpReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action ||
            intent.action === "android.intent.action.QUICKBOOT_POWERON"
        ) {
            refreshAlarm(context)
        }
        if (intent.action === "smart alarm refresh alarm") {
            refreshAlarm(context)
        }
    }

    private fun refreshAlarm(context: Context) {
        try {
            /*
            get alarm list and schedule again
            * */
        } catch (e: Exception) {
        }
    }
}