package com.example.fitme.repo

import androidx.lifecycle.MutableLiveData
import com.example.fitme.core.network.result.Resource
import com.example.fitme.data.local.AppPrefs
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

    fun getUser(uid: String): MutableLiveData<Resource<User>> {
        return db.getUser(uid)
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