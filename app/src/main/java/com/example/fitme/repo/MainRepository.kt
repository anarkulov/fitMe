package com.example.fitme.repo

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.fitme.core.network.result.Resource
import com.example.fitme.data.local.AppPrefs
import com.example.fitme.data.models.Activity
import com.example.fitme.data.models.User
import com.example.fitme.data.remote.RemoteDataSource
import com.google.firebase.auth.FirebaseUser

class MainRepository(
    private val dataSource: RemoteDataSource,
    private val prefs: AppPrefs,
    private val db: UserDatabase,
) {

    private val firebaseUser = MutableLiveData<FirebaseUser?>()

    fun logOut(): MutableLiveData<Boolean> {
        return db.signOut()
    }

    fun getFirebaseUser(): MutableLiveData<FirebaseUser?> {
        return db.getCurrentUser()
    }

    fun getUser(): MutableLiveData<Resource<User>> {
        return db.getUser()
    }

    fun getActivityList(): MutableLiveData<Resource<List<Activity>>> {
        return db.getActivityList()
    }

    fun getAllActivityCountersBy(type: Int): MutableLiveData<Resource<List<Activity>>> {
        return db.getAllActivityCountersBy(type)
    }

    fun updateUser(user: User): MutableLiveData<Resource<Boolean>> {
        return db.updateUser(user)
    }

    fun uploadImageFile(filePath: Uri): LiveData<Resource<String>> {
        return db.uploadImageFile(filePath)
    }

    fun getUsers(): LiveData<Resource<List<User>>> {
        return db.getUsers()
    }

//    fun setAccessToken(accessToken: String) {
//        prefs.accessToken = accessToken
//    }
//
//    fun setRefreshToken(refreshToken: String) {
//        prefs.refreshToken = refreshToken
//    }
//
//    fun getAccessToken(): String? {
//        return prefs.accessToken
//    }
//
//    fun getRefreshToken(): String? {
//        return prefs.refreshToken
//    }
//
//    fun forgotPassword(email: String) {
//        userDatabase.forgotPassword(email)
//    }
//
//    fun createUser(
//        email: String,
//        password: String
//    ) {
//       userDatabase.createUser(email, password)
//    }
//
//    fun getProfile(uid: String, email: String) {
//
//    }
//
//    fun createProfile(
//        uid: String,
//        firstName: String,
//        lastName: String,
//        email: String,
//        phone: String
//    ) {
//        userDatabase.createProfile(uid, firstName, lastName, email, phone, "", "", "")
//    }
}