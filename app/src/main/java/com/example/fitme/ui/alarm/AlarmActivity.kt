package com.example.fitme.ui.alarm

import android.media.Ringtone
import android.media.RingtoneManager
import android.view.LayoutInflater
import com.example.fitme.core.ui.BaseActivity
import com.example.fitme.core.utils.Log
import com.example.fitme.databinding.ActivityAlarmBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class AlarmActivity : BaseActivity<AlarmViewModel, ActivityAlarmBinding>() {

    private val myTag = "AlarmActivity"
    override val viewModel: AlarmViewModel by viewModel()
    private var ringtone: Ringtone? = null

    override fun initView() {
        super.initView()

        var notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ringtone = RingtoneManager.getRingtone(this, notificationUri)

        if (ringtone != null) {
            ringtone?.setLooping(true)
            ringtone?.play()
        } else {
            notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            ringtone = RingtoneManager.getRingtone(this, notificationUri)
            ringtone?.setLooping(true)
            ringtone?.play()
        }
    }

    override fun onBackPressed() {
        Log.d("onBackPressed", myTag)
    }

    override fun inflateViewBinding(inflater: LayoutInflater): ActivityAlarmBinding {
        return ActivityAlarmBinding.inflate(inflater)
    }
}