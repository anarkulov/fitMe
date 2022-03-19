package com.example.fitme.ui.alarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fitme.data.local.AppPrefs

class AlarmViewModelFactory(private val alarmRepository: AlarmRepository, private val appPrefs: AppPrefs) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AlarmViewModel(alarmRepository, appPrefs) as T
    }
}
