package com.example.fitme.ui.alarm

import androidx.lifecycle.MutableLiveData
import com.example.fitme.core.network.result.Resource
import com.example.fitme.data.models.Activity
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

    fun saveActivity(activity: Activity): MutableLiveData<Resource<Boolean>>  {
        return db.createActivity(activity)
    }

    fun saveAlarmData(alarm: Alarm): MutableLiveData<Resource<String>>  {
        return db.saveAlarm(alarm)
    }

    fun updateAlarm(alarm: Alarm): MutableLiveData<Resource<Boolean>> {
        return db.updateAlarm(alarm)
    }

    fun deleteAlarm(docId: String?): MutableLiveData<Resource<Boolean>> {
        return db.deleteAlarm(docId)
    }
}