package com.example.fitme.ui.auth

import androidx.lifecycle.MutableLiveData
import com.example.fitme.core.network.result.Resource
import com.example.fitme.data.local.AppPrefs
import com.example.fitme.data.models.User
import com.example.fitme.data.remote.RemoteDataSource
import com.example.fitme.repo.UserDatabase
import com.google.firebase.auth.FirebaseUser

class AuthRepository(
    private val dataSource: RemoteDataSource,
    private val prefs: AppPrefs,
    private val db: UserDatabase,
) {

    fun login(email: String, password: String): MutableLiveData<Resource<String>> {
        return db.signIn(email, password)
    }

    fun register(email: String, password: String): MutableLiveData<Resource<String>> {
        return db.register(email, password)
    }

    fun getCurrentUser(): MutableLiveData<FirebaseUser?> {
        return db.getCurrentUser()
    }

    fun createProfile(
        uid: String,
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
        country: String,
        state: String,
        city: String,
    ): MutableLiveData<Resource<Int>> {
        return db.createProfile(uid, firstName, lastName, email, phone, country, state, city)
    }

    fun createUser(email: String, password: String): MutableLiveData<Resource<String>> {
        return db.createUser(email, password)
    }

    fun getUser(uid: String): MutableLiveData<Resource<User>> {
        return db.getUser(uid)
    }

    fun forgotPassword(email: String): MutableLiveData<Resource<Boolean>> {
        return db.forgotPassword(email)
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

}