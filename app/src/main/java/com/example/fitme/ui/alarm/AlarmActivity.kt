package com.example.fitme.ui.alarm

import android.content.Intent
import android.view.LayoutInflater
import com.example.fitme.core.ui.BaseActivity
import com.example.fitme.core.utils.Log
import com.example.fitme.data.models.Alarm
import com.example.fitme.databinding.ActivityAlarmBinding
import com.example.fitme.managers.MyAlarmManager
import org.koin.androidx.viewmodel.ext.android.viewModel

class AlarmActivity : BaseActivity<AlarmViewModel, ActivityAlarmBinding>() {

    private val myTag = "AlarmActivity"
    override val viewModel: AlarmViewModel by viewModel()

    override fun initViewModel() {
        super.initViewModel()
    }

    override fun initView() {
        super.initView()

        handleMyIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleMyIntent(intent)
    }

    private fun handleMyIntent(intent: Intent?) {
        val alarm = intent?.getBundleExtra(MyAlarmManager.ALARM_KEY)?.getSerializable(MyAlarmManager.ALARM_KEY) as Alarm?
        Log.d("handleMyIntent: $alarm", myTag)
    }

    override fun initListeners() {
        super.initListeners()

        binding.tvName.setOnClickListener {
            MyAlarmManager.stopAlarm(this)
        }
    }


    override fun onBackPressed() {
        Log.d("onBackPressed", myTag)
    }

    override fun inflateViewBinding(inflater: LayoutInflater): ActivityAlarmBinding {
        return ActivityAlarmBinding.inflate(inflater)
    }
}