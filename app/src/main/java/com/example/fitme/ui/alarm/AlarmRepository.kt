package com.example.fitme.ui.alarm

import androidx.lifecycle.MutableLiveData
import com.example.fitme.core.network.result.Resource
import com.example.fitme.data.models.Alarm
import com.example.fitme.repo.UserDatabase

class AlarmRepository(
    private val db: UserDatabase
) {

    fun getAlarmList(): MutableLiveData<Resource<List<Alarm>>> {
        return db.getAlarmList()
    }

    fun getUserId() : String? {
        return db.getUserId()
    }

    fun saveAlarmData(alarm: Alarm): MutableLiveData<Resource<String>>  {
        return db.saveAlarm(alarm)
    }

    fun updateAlarm(alarm: Alarm): MutableLiveData<Resource<Boolean>> {
        return db.updateAlarm(alarm)
    }
}