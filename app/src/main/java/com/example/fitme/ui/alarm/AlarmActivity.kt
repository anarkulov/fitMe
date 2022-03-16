package com.example.fitme.ui.alarm

import android.view.LayoutInflater
import com.example.fitme.core.ui.BaseActivity
import com.example.fitme.core.utils.Log
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