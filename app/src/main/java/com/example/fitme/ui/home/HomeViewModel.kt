package com.example.fitme.ui.home

import androidx.lifecycle.MutableLiveData
import com.example.fitme.core.ui.BaseViewModel
import com.example.fitme.repo.MainRepository
import com.google.firebase.auth.FirebaseUser

class HomeViewModel(private val mainRepository: MainRepository): BaseViewModel() {

    fun getUser(): MutableLiveData<FirebaseUser?> {
        return mainRepository.getFirebaseUser()
    }

    fun logOut(): MutableLiveData<Boolean> {
        return mainRepository.logOut()
    }
}