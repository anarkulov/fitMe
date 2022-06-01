package com.example.fitme.ui.home

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.example.fitme.core.network.result.Resource
import com.example.fitme.core.ui.BaseViewModel
import com.example.fitme.data.local.AppPrefs
import com.example.fitme.data.models.Activity
import com.example.fitme.data.models.User
import com.example.fitme.repo.MainRepository
import com.google.firebase.auth.FirebaseUser

class HomeViewModel(private val mainRepository: MainRepository, private val appPrefs: AppPrefs): BaseViewModel() {
    val activityList = ArrayList<Activity>()
    val sortedActivityList = ArrayList<Activity>()

    fun getUser(): MutableLiveData<FirebaseUser?> {
        return mainRepository.getFirebaseUser()
    }

    private var _getUser = MutableLiveData<Boolean>()
    var getProfile: LiveData<Resource<User>> = _getUser.switchMap {
        mainRepository.getUser()
    }

    fun getUserProfile() {
        _getUser.postValue(true)
    }

    fun logOut(): MutableLiveData<Boolean> {
        return mainRepository.logOut()
    }

    fun getActivityList(): LiveData<Resource<List<Activity>>> {
        return mainRepository.getActivityList()
    }

    fun getAllActivityCountersBy(type: Int): LiveData<Resource<List<Activity>>> {
        return mainRepository.getAllActivityCountersBy(type)
    }

    fun updateUser(user: User): LiveData<Resource<Boolean>> {
        return mainRepository.updateUser(user)
    }

    fun clearPrefs() {
        appPrefs.clearPrefs()
    }

    fun uploadImageFile(filePath: Uri): LiveData<Resource<String>> {
        return mainRepository.uploadImageFile(filePath)
    }

    fun getUsers(): LiveData<Resource<List<User>>> {
        return mainRepository.getUsers()
    }

    fun getLocalProfile(): User? {
        return appPrefs.profile
    }

    fun setLocale(lang: String){
        appPrefs.lang = lang
    }

    fun setProfile(profile: User) {
        appPrefs.profile = profile
    }
}