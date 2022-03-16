package com.example.fitme.managers

import android.content.Context
import android.os.PowerManager
import com.example.fitme.core.utils.Log

class AlarmWakeLock {
    private val myTag = "AlarmWakeLock"

    private var sCpuWakeLock: PowerManager.WakeLock? = null

    fun acquireCpu(context: Context?) {
        if (sCpuWakeLock != null) {
            return
        }

        val powerManager: PowerManager =
            context?.getSystemService(Context.POWER_SERVICE) as PowerManager
        sCpuWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
            "smart_alarm:AlarmWakeLock") as PowerManager.WakeLock

        Log.d("acquireCpu: going to acquire", myTag)
        sCpuWakeLock?.acquire(10 * 60 * 1000L /*10 minutes*/)
        Log.d("acquireCpu: acquired", myTag)
    }

    fun acquireScreenCpu(context: Context?) {
        if (sCpuWakeLock != null) {
            Log.d("acquireCpu: already acquired", myTag)
            return
        }
        val powerManager: PowerManager =
            context?.getSystemService(Context.POWER_SERVICE) as PowerManager

        sCpuWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
                or PowerManager.ACQUIRE_CAUSES_WAKEUP
                or PowerManager.ON_AFTER_RELEASE,
            "smart_alarm:AlarmWakeLock")
        Log.d("acquireCpu: going to acquire", myTag)
        sCpuWakeLock?.acquire(10*60*1000L /*10 minutes*/)
        Log.d("acquireCpu: acquired", myTag)
    }

    fun releaseCpu() {
        if (sCpuWakeLock != null) {
            sCpuWakeLock?.release()
            sCpuWakeLock = null
        }
    }
}