package com.example.fitme.core.ui.widgets;

import android.os.CountDownTimer

abstract class CountUpTimer protected constructor(private val duration: Long) : CountDownTimer(duration, 1000) {

    abstract fun onTicks(second: Long)

    override fun onTick(p0: Long) {
        val second = (duration - p0) / 1000
        onTicks(second)
    }

    override fun onFinish() {
    }
}
