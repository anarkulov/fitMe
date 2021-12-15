package com.example.fitme.core.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {

    val loading = MutableLiveData<Boolean>()
}
