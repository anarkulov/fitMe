package com.example.fitme.ui.alarm

import androidx.lifecycle.MutableLiveData
import com.example.fitme.core.network.result.Resource
import com.example.fitme.core.ui.BaseViewModel
import com.example.fitme.data.local.AppPrefs
import com.example.fitme.data.models.Alarm

class AlarmViewModel(
    private val alarmRepository: AlarmRepository,
    private val prefs: AppPrefs,
) : BaseViewModel() {

    val isChanged = MutableLiveData<Boolean>()

    fun getAlarmList(): MutableLiveData<Resource<List<Alarm>>> {
        return alarmRepository.getAlarmList()
    }

    fun getUserId(): String? {
        return alarmRepository.getUserId()
    }

    fun saveAlarmData(alarm: Alarm): MutableLiveData<Resource<String>> {
        return alarmRepository.saveAlarmData(alarm)
    }

    fun updateAlarm(alarm: Alarm): MutableLiveData<Resource<Boolean>> {
        return alarmRepository.updateAlarm(alarm)
    }
}